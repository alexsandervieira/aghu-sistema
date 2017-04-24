package br.gov.mec.aghu.internacao.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.transaction.SystemException;

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.internacao.vo.RelatorioAltasObitoVO;
import br.gov.mec.aghu.model.AghParametros;

import com.itextpdf.text.DocumentException;



public class RelatorioAltasObitoController  extends ActionReport implements ActionPaginator {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3196219852427610306L;

	private static final Log LOG = LogFactory.getLog(RelatorioAltasObitoController.class);

	private enum RelatorioAltasObitoControllerExceptionCode implements BusinessExceptionCode {
		MENSAGEM_RELATORIO_VAZIO;
	}

	private static final String RELATORIO_ALTAS_OBITO = "internacao-relatorioAltasObito";
	
	private static final String RELATORIO_ALTAS_OBITO_PDF = "internacao-visualizarRelatorioAltasObitoPDF";
	
	private static final String AAC_00145 = "AAC_00145";
	
	private Date dataInicial;
	private Date dataFinal;
	private List<RelatorioAltasObitoVO> colecao;
	
	@Inject @Paginator
	private DynamicDataModel<RelatorioAltasObitoVO> dataModel;	

	@EJB
	private IInternacaoFacade internacaoFacade; 
	
	@Inject
	private SistemaImpressao sistemaImpressao;
	
	@EJB
	private IParametroFacade parametroFacade;

	private StreamedContent streamedContent;
	
	@PostConstruct
	public void inicio() {
		begin(conversation);
		this.dataInicial = new Date();
	}
	
	public void directPrint() throws SistemaImpressaoException, ApplicationBusinessException {

		try {
			DocumentoJasper documento = gerarDocumento(false);
			this.sistemaImpressao.imprimir(documento.getJasperPrint(),super.getEnderecoIPv4HostRemoto());
			apresentarMsgNegocio("MENSAGEM_SUCESSO_IMPRESSAO");
			
		} catch (SistemaImpressaoException e) {
			apresentarExcecaoNegocio(e);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		} catch (UnknownHostException e) {
			LOG.error(e.getMessage(),e);
			this.apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		} catch (JRException e) {
			LOG.error(e.getMessage(),e);
			this.apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}
	}
	
	public String imprimirRelatorio(){
		if (dataFinal!=null && dataFinal.before(dataInicial)){
			apresentarMsgNegocio(Severity.ERROR, AAC_00145);
			return null;
		}	
		try {
			carregarColecao();
			this.directPrint();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		} catch (SistemaImpressaoException e) {
			LOG.error(e.getMessage(),e);
			this.apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}
		
		return null;
	}
	
	public String visualizarRelatorio(){
		if (dataFinal!=null && dataFinal.before(dataInicial)){
			apresentarMsgNegocio(Severity.ERROR, AAC_00145);
			return null;
		}	
		try {
			carregarColecao();
			return RELATORIO_ALTAS_OBITO_PDF;
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
	}

	public String voltar() {
		return RELATORIO_ALTAS_OBITO;
	}
	
	public StreamedContent getRenderPdf() throws IOException, JRException, SystemException, DocumentException, ApplicationBusinessException {	
		DocumentoJasper documento = gerarDocumento();
		return this.criarStreamedContentPdf(documento.getPdfByteArray(false));		
		
	}	

	private void carregarColecao() throws ApplicationBusinessException{
		colecao = internacaoFacade.pesquisaRelatorioAltasObito(this.dataInicial, this.dataFinal);
		if(colecao == null || colecao.isEmpty()){
			throw new ApplicationBusinessException(RelatorioAltasObitoControllerExceptionCode.MENSAGEM_RELATORIO_VAZIO); 
		}
	}
	
	@Override
	public List<RelatorioAltasObitoVO> recuperarColecao() throws ApplicationBusinessException {
		return colecao;
	}
	
	@Override
	public String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/internacao/report/relatorioAltasObito.jasper";
	}
	
	@Override
	public Map<String, Object> recuperarParametros() {

		Map<String, Object> params = new HashMap<String, Object>();
		SimpleDateFormat dataAtualRelatorio = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		SimpleDateFormat data = new SimpleDateFormat("dd/MM/yyyy");
		
		try {
			AghParametros hospital = parametroFacade.obterAghParametro(AghuParametrosEnum.P_HOSPITAL_RAZAO_SOCIAL);
			params.put("hospitalLocal", hospital.getVlrTexto());
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		params.put("tituloRelatorio", "Relação de Altas por Óbito de " + data.format(dataInicial) + " até " + data.format(dataFinal != null ? dataFinal : new Date()));
		params.put("dataAtual", dataAtualRelatorio.format(new Date()));
		params.put("nomeRelatorio", "Relatório Negativas de Internação.");
		
		return params;
	}

	public void gerarArquivo() throws NumberFormatException, IOException, ApplicationBusinessException{
		if (dataFinal!=null && dataFinal.before(dataInicial)){
			apresentarMsgNegocio(Severity.ERROR, AAC_00145);
			return;
		}	
		
		try {
			File file = internacaoFacade.geraArquivoAltasObito(dataInicial, dataFinal);
			if (file != null){
				this.apresentarMsgNegocio(Severity.INFO, "MSG_INFO_ARQ_GERADO_SUCESSO");
				this.streamedContent = new DefaultStreamedContent(new FileInputStream(file),"text/csv", file.getName());
			}else{
				streamedContent = null; 
				apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_RELATORIO_VAZIO");
			}	
		}catch(ApplicationBusinessException e) {
			LOG.error(e.getMessage(),e);
			apresentarExcecaoNegocio(e);
			return;
		}
	}

	/**
	 * Método responsável para ação do botão 'Pesquisar'.
	 */
	public void pesquisar() {
		
		if (dataFinal!=null && dataFinal.before(dataInicial)){
			apresentarMsgNegocio(Severity.ERROR, AAC_00145);
			return;
		}
		
		this.dataModel.reiniciarPaginator();		
	}

	/**
	 * Método que limpa os campos da tela de pesquisa.
	 */
	public void limpar() {
		this.setDataInicial(new Date());
		this.dataModel.limparPesquisa();	
	}

	@Override
	public Long recuperarCount() {
		return internacaoFacade.pesquisaRelatorioAltasObitoCount(this.dataInicial, this.dataFinal);
	}

	@Override
	public List<RelatorioAltasObitoVO> recuperarListaPaginada(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc) {
		return internacaoFacade.pesquisaRelatorioAltasObitoPaginado(
				firstResult, maxResult, orderProperty, asc, this.dataInicial, this.dataFinal);
	}

	public Date getDataInicial() {
		return dataInicial;
	}

	public void setDataInicial(Date dataInicial) {
		this.dataInicial = dataInicial;
	}

	public Date getDataFinal() {
		return dataFinal;
	}

	public void setDataFinal(Date dataFinal) {
		this.dataFinal = dataFinal;
	}

	public StreamedContent getStreamedContent() {
		return streamedContent;
	}

	public void setStreamedContent(StreamedContent streamedContent) {
		this.streamedContent = streamedContent;
	}

	public DynamicDataModel<RelatorioAltasObitoVO> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<RelatorioAltasObitoVO> dataModel) {
		this.dataModel = dataModel;
	}
	
}