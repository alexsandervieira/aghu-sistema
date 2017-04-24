package br.gov.mec.aghu.menu.portal.controller;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.casca.menu.portal.rss.Channel;
import br.gov.mec.aghu.casca.menu.portal.rss.Item;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.utils.DateFormatUtil;
import br.gov.mec.aghu.model.AghParametros;


public class PortalRSSController extends ActionController {

	private static final String SEM_LINK = "Sem link";

	private static final int QUANTIDADE_NOTICIAS_PADRAO = 3;

	@EJB
	private IParametroFacade parametroFacade;

	private static final long serialVersionUID = 1451619513907861671L;
	private static final Logger log = LoggerFactory.getLogger(PortalRSSController.class);

	private static final int TIMEOUT = 3 * 1000;

	private static final String CHAVE_CACHE = "portalRssNews";

	private static final String PORTALRSS_HOSTNAME = "www.ebserh.gov.br";
	
	private int quantBuscaNoticiasEftuadas = 0;
	private Channel canalPortal;
	
	@Inject
	private PortalRSSCacheManager portalRSSCache;

	
	
	
	public void inicio() throws IOException {
		iniciarCanalPortal();
	}
	
	public boolean hasPararCarregamentoNoticiasPortalRss() {
		return (quantBuscaNoticiasEftuadas > 3);
	}

	public void carregarNoticiasPortalRss() {
		try {
			quantBuscaNoticiasEftuadas++;
			Channel cacheRecuperado = recuperaCache();
			if (cacheRecuperado == null) {
				recuperaNoticiasPortal();
				gravaCache();
				log.info("Recuperando notícias do portal no cache " + CHAVE_CACHE);
			} else {
				setCanalPortal(cacheRecuperado);
			}
			
		} catch (Exception e) {
			log.error("Ocorreu um erro ao tentar renderizar as notícias do portal", e);
			iniciarCanalPortal();
		} finally {
			if (this.getCanalPortal() != null 
					&& !this.getCanalPortal().getItem().isEmpty() 
					&& SEM_LINK.equalsIgnoreCase(this.getCanalPortal().getItem().get(0).getLink())) {
				this.getCanalPortal().getItem().get(0).setDescription("Tentativa #"  + quantBuscaNoticiasEftuadas);
				if (quantBuscaNoticiasEftuadas > 3) {
					this.getCanalPortal().getItem().get(0).setDescription("Portal Indisponível");				
				}
			}
		}
	}

	private void iniciarCanalPortal() {
		canalPortal = new Channel();
		Item item = new Item();
		item.setTitle("Portal de Notícias do AGHU");
		item.setDescription("Iniciando busca ...");
		item.setLink(SEM_LINK);
		item.setPubDate(DateFormatUtil.fomataDiaMesAno(new Date()));
		List<Item> itemList = new LinkedList<>();
		itemList.add(item);
		canalPortal.setItem(itemList);
	}

	private Channel recuperaCache() throws ApplicationBusinessException {
		return (Channel) this.portalRSSCache.get(CHAVE_CACHE);
	}

	private void gravaCache() {
		this.portalRSSCache.put(CHAVE_CACHE, canalPortal);
	}

	private void recuperaNoticiasPortal() throws JAXBException, ApplicationBusinessException {
		String conteudoRSSPortal = recuperaXmlRSSPortal();
		canalPortal = converteConteudoPortalObjetos(conteudoRSSPortal);
		limitaNoticiasPeloParametro(canalPortal);
		log.info("Renderizando notícias do portal");
	}

	private void limitaNoticiasPeloParametro(Channel canalPortal) throws ApplicationBusinessException {
		Integer quantidadeNoticiasParametro = recuperaValorNumericoParametroQuantidadeNoticias();
		selecionaNoticias(canalPortal, quantidadeNoticiasParametro);
	}

	private void selecionaNoticias(Channel canalPortal, Integer quantidadeNoticiasParametro) {
		List<Item> itens = new ArrayList<Item>();
		Integer contador = 0;
		for (Item item : canalPortal.getItem()) {
			if (contador < quantidadeNoticiasParametro) {
				itens.add(item);
				contador++;
				continue;
			}
			break;
		}
		canalPortal.setItem(itens);
	}
	
	private String recuperarValorTextoParametroHostPortalRss() {
		String hostname = PORTALRSS_HOSTNAME;
		if (getParametroFacade().verificarExisteAghParametro(AghuParametrosEnum.P_AGHU_PORTALRSS_HOSTNAME)) {
			AghParametros param = getParametroFacade().getAghParametro(AghuParametrosEnum.P_AGHU_PORTALRSS_HOSTNAME);
			if (param != null && param.getVlrTexto() != null && !"".equalsIgnoreCase(param.getVlrTexto().trim())) {
				hostname = param.getVlrTexto();
			}
		}
		return hostname;
	}
	
	private Integer recuperaValorNumericoParametroQuantidadeNoticias() {
		AghParametros qtdeNoticiasParam = null;
		Integer quantidadeNoticias = QUANTIDADE_NOTICIAS_PADRAO;
		try {
			qtdeNoticiasParam = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_AGHU_PORTALRSS_NUMERO_DE_NOTICIAS_TELA_LOGIN);
			quantidadeNoticias = recuperaValorNumericoParametroNoticias(qtdeNoticiasParam);
		} catch (ApplicationBusinessException e) {
			log.error("Ocorreu um erro ao tentar recuperar o parâmetro " + AghuParametrosEnum.P_AGHU_PORTALRSS_NUMERO_DE_NOTICIAS_TELA_LOGIN);
			log.info("Quantidade de notícias carregadas padrão: " + QUANTIDADE_NOTICIAS_PADRAO);
		}
		return quantidadeNoticias;
	}

	private Integer recuperaValorNumericoParametroNoticias(AghParametros qtdeNoticiasParam) {
		Integer quantidadeNoticias = qtdeNoticiasParam.getVlrNumerico() == null ? 0 : qtdeNoticiasParam.getVlrNumerico().intValue();
		if (quantidadeNoticias == 0) {
			quantidadeNoticias = Integer.valueOf(qtdeNoticiasParam.getVlrNumericoPadrao().intValue());
		}
		return quantidadeNoticias;
	}

	private Channel converteConteudoPortalObjetos(String conteudoRSSPortal) throws JAXBException {
		log.info("Convertendo RSS em Objetos");
		JAXBContext jaxbContext = JAXBContext.newInstance(Channel.class);
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		Channel noticiasPortal = (Channel) jaxbUnmarshaller.unmarshal(new StringReader(conteudoRSSPortal));
		log.info("RSS Convertido");
		return noticiasPortal;
	}

	private String recuperaXmlRSSPortal() {
		HttpGet request = new HttpGet(criaURL());
		HttpResponse xml = null;
		HttpEntity httpEntity = null;
		try {
			CloseableHttpClient client = recuperaClienteHttp(configuraTimeOutRequisicao());
			xml = client.execute(request);
			httpEntity = xml.getEntity();
			String conteudoRSS = EntityUtils.toString(httpEntity);
			return removeCabecalhoXML(conteudoRSS);
		} catch (Exception e) {
			log.error("Erro ao tentar recuperar o xml do RSS do portal", e);
		}
		return StringUtils.EMPTY;
	}

	private CloseableHttpClient recuperaClienteHttp(RequestConfig config) {
		CloseableHttpClient client = HttpClientBuilder.create().setDefaultRequestConfig(config).build();
		return client;
	}

	private RequestConfig configuraTimeOutRequisicao() {
		return RequestConfig.custom().setConnectTimeout(TIMEOUT).setConnectionRequestTimeout(TIMEOUT).setSocketTimeout(TIMEOUT).build();
	}
	
	private String removeCabecalhoXML(String conteudoRSS) {
		return conteudoRSS.replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", StringUtils.EMPTY)
				.replace("<rss xmlns:dc=\"http://purl.org/dc/elements/1.1/\" version=\"2.0\"> ", StringUtils.EMPTY)
				.replace("</rss>", StringUtils.EMPTY).replace("dc:", StringUtils.EMPTY);
	}

	private URI criaURL() {
		log.info("Criando url para acesso ao RSS do portal");
		
    	URIBuilder urlBuilder = new URIBuilder();
    	urlBuilder.setScheme("http")
	    	.setHost(recuperarValorTextoParametroHostPortalRss())
	    	.setPath("/web/aghu/informes/-/blogs/rss")
	    	.setParameter("_33_displayStyle", "abstract")
	    	.setParameter("_33_type", "rss")
	    	.setParameter("_33_version", "2.0");
    	log.info("URL Criada: " + urlBuilder.toString());
    	
    	try {
			return urlBuilder.build();
		} catch (URISyntaxException e) {
			log.error("Erro ao tentar criar a URL do RSS do Portal", e);
			return null;
		}
	}
	
	/**
	 * @return the quantBuscaNoticiasEftuadas
	 */
	public int getQuantBuscaNoticiasEftuadas() {
		return quantBuscaNoticiasEftuadas;
	}

	/**
	 * @param quantBuscaNoticiasEftuadas the quantBuscaNoticiasEftuadas to set
	 */
	public void setQuantBuscaNoticiasEftuadas(int quantBuscaNoticiasEftuadas) {
		this.quantBuscaNoticiasEftuadas = quantBuscaNoticiasEftuadas;
	}

	public Channel getCanalPortal() {
		return canalPortal;
	}

	public void setCanalPortal(Channel canalPortal) {
		this.canalPortal = canalPortal;
	}

	public IParametroFacade getParametroFacade() {
		return parametroFacade;
	}
	
	
}
