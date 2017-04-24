package br.gov.mec.aghu.internacao.action;

import java.io.IOException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import org.primefaces.model.StreamedContent;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AinSolicitacoesInternacao;

import com.itextpdf.text.DocumentException;



public class RelatorioNegativasInternacaoDiaController  extends ActionReport {

	private enum RelatorioNegativasInternacaoDiaControllerExceptionCode implements BusinessExceptionCode {
		MENSAGEM_NENHUM_REGISTRO_ENCONTRADO_GENERICA;
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7480404643031346219L;

	private static final Log LOG = LogFactory.getLog(RelatorioNegativasInternacaoDiaController.class);

	private static final String RELATORIO_NEGATIVAS_INTERNCAO = "internacao-negativaSolicitacaoInternacao";
	
	private static final String RELATORIO_NEGATIVAS_INTERNCAO_PDF = "internacao-visualizarRelatorioNegativaInternacaoPDF";
	
	private Date dataNegativa;
	
	
	private AghEspecialidades especialidade;
	
	@EJB
	private IInternacaoFacade internacaoFacade; 
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@Inject
	private SistemaImpressao sistemaImpressao;
	
	@EJB
	private IParametroFacade parametroFacade;

	List<AinSolicitacoesInternacao> colecao = new ArrayList<AinSolicitacoesInternacao>();
	@PostConstruct
	public void inicio() {
		begin(conversation);
		this.dataNegativa = new Date();
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
		try {
			carregarColecao();
			
			return RELATORIO_NEGATIVAS_INTERNCAO_PDF;
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
	}

	private void carregarColecao() throws ApplicationBusinessException {
		colecao = internacaoFacade.pesquisaNegativasDia(this.dataNegativa, this.especialidade);
		if(colecao == null || colecao.isEmpty()){
			throw new ApplicationBusinessException(RelatorioNegativasInternacaoDiaControllerExceptionCode.MENSAGEM_NENHUM_REGISTRO_ENCONTRADO_GENERICA); 
		}
	}

	public String voltar() {
		return RELATORIO_NEGATIVAS_INTERNCAO;
	}
	
	public StreamedContent getRenderPdf() throws IOException, JRException, SystemException, DocumentException, ApplicationBusinessException {	
		DocumentoJasper documento = gerarDocumento();
		return this.criarStreamedContentPdf(documento.getPdfByteArray(false));		
		
	}	
	
//Metodos para a suggestion de especialidades
	public List<AghEspecialidades> listarEspecialidadesPorSigla(String paramPesquisa) {
		return this.aghuFacade.listarPorSiglaAtivas(paramPesquisa);
	}
	
	@Override
	public List<AinSolicitacoesInternacao> recuperarColecao() throws ApplicationBusinessException {
		return colecao;
	}
	
	@Override
	public String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/internacao/report/relatorioNegativasInternacaoDia.jasper";
	}
	
	
	
	@Override
	public Map<String, Object> recuperarParametros() {

		Map<String, Object> params = new HashMap<String, Object>();
		Date dataAtual = new Date();
		SimpleDateFormat dataAtualRelatorio = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		SimpleDateFormat data = new SimpleDateFormat("dd/MM/yyyy");
		
		try {
			AghParametros hospital = parametroFacade.obterAghParametro(AghuParametrosEnum.P_HOSPITAL_RAZAO_SOCIAL);
			params.put("hospitalLocal", hospital.getVlrTexto());
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		params.put("dataRelatorio", "Relação de Negativa de Internação do Dia " + data.format(dataNegativa));
		params.put("dataAtual", dataAtualRelatorio.format(dataAtual));
		params.put("nomeRelatorio", "Relatório Negativas de Internação.");
		params.put("tituloRelatorio", "Negativas de Internação no dia :  ".concat(data.format(this.dataNegativa)));
		
		return params;
	}
	
	public Date getDataNegativa() {
		return dataNegativa;
	}

	public void setDataNegativa(Date dataNegativa) {
		this.dataNegativa = dataNegativa;
	}

	public AghEspecialidades getEspecialidade() {
		return especialidade;
	}

	public void setEspecialidade(AghEspecialidades especialidade) {
		this.especialidade = especialidade;
	}

}