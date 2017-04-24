package br.gov.mec.aghu.internacao.action;

import java.io.IOException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;
import br.gov.mec.aghu.dominio.DominioPeriodoDiasMeses;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.internacao.vo.AghUnidadesFuncionaisVO;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;

import com.itextpdf.text.DocumentException;


/**
 * @author rodrigo.jornooki
 */
public class RelatorioSumarioAltaPendenteController extends ActionReport implements ActionPaginator {

		
	/**
	 * 
	 */
	private static final long serialVersionUID = 2760614605917367046L;

	private static final Log LOG = LogFactory.getLog(RelatorioSumarioAltaPendenteController.class);

	private enum RelatorioSumarioAltaPendeteControllerExceptionCode implements BusinessExceptionCode {
		MENSAGEM_RELATORIO_VAZIO;
	}

	private static final String RELATORIO_SUMARIO_ALTA_PENDENTE = "internacao-relatorioSumarioAltaPendente";
	
	private static final String VISUALIZAR_RELATORIO_SUMARIO_ALTA_PENDENTE = "internacao-visualizarRelatorioSumarioAltaPendente";
	
	private static final String AAC_00145 = "AAC_00145";

	private static final String PAGE_SUMARIO_ALTA = "prescricaomedica-manterSumarioAlta";

	private Date dataInicial;
	
	private Date dataFinal;
	
	private AghUnidadesFuncionaisVO aghUnidadesFuncionaisQuartoVO;	
	
	@EJB
	private IInternacaoFacade internacaoFacade; 
	
	@Inject
	private SistemaImpressao sistemaImpressao;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;
	
	@Inject @Paginator
	private DynamicDataModel<AinInternacao> dataModel;
	private List<AinInternacao> colecao;

	private DominioPeriodoDiasMeses periodo;

	private Integer atdSeq;	
	
	@PostConstruct
	public void inicio() {
		begin(conversation);
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
		
		if (!validadarData()){
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
	
		if (!validadarData()){
			return null;
		}
		try {
			carregarColecao();
			return VISUALIZAR_RELATORIO_SUMARIO_ALTA_PENDENTE;
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}

	}
	
	public void ajustarDatasConformePeriodo() {
		if (this.periodo == null) {
			dataFinal = null;
			dataInicial = null;
		} else {
			dataFinal = Calendar.getInstance().getTime();
			dataInicial = new Date(dataFinal.getTime() - periodo.getValorEmMilisegundos());
		}
	}

	public String voltar() {
		return RELATORIO_SUMARIO_ALTA_PENDENTE;
	}
	
	public StreamedContent getRenderPdf() throws IOException, JRException, SystemException, DocumentException, ApplicationBusinessException {	
		DocumentoJasper documento = gerarDocumento();
		return this.criarStreamedContentPdf(documento.getPdfByteArray(false));		
		
	}	
		
	public void limpar() {
		this.periodo = null;
		this.dataFinal = null;
		this.dataInicial = null;
		this.aghUnidadesFuncionaisQuartoVO = null;
		this.dataModel.limparPesquisa();	
	}
	
		
	public List<AghUnidadesFuncionaisVO> pesquisarUnidadeFuncionalVOPorCodigoEDescricao(String parametro) {
		return this.cadastrosBasicosInternacaoFacade.pesquisarUnidadeFuncionalVOPorCodigoEDescricao(parametro);
	}

	private void carregarColecao() throws ApplicationBusinessException{
		colecao = internacaoFacade.pesquisarPacientesComSumarioAltaPendente(this.dataInicial, this.dataFinal, 
				this.aghUnidadesFuncionaisQuartoVO != null ? this.aghUnidadesFuncionaisQuartoVO.getSeq() : null);
		if(colecao == null || colecao.isEmpty()){
			throw new ApplicationBusinessException(RelatorioSumarioAltaPendeteControllerExceptionCode.MENSAGEM_RELATORIO_VAZIO); 
		}
	}
	
	public String darAltaPaciente(){
		String retorno = RELATORIO_SUMARIO_ALTA_PENDENTE;
		try {
			if (atdSeq != null) {
				this.prescricaoMedicaFacade	.realizarConsistenciasSumarioAlta(atdSeq);
				retorno = PAGE_SUMARIO_ALTA;
			}
		} catch (ApplicationBusinessException e) {
			this.apresentarExcecaoNegocio(e);
		}
		return retorno;
	}

	@Override
	public List<AinInternacao> recuperarColecao() throws ApplicationBusinessException {
		return colecao;
	}
	
	@Override
	public String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/internacao/report/relatorioPacientesSumarioAltaPendente.jasper";
	}
	
	@Override
	public Map<String, Object> recuperarParametros() {

		Map<String, Object> params = new HashMap<String, Object>();
		Date dataAtual = new Date();
		SimpleDateFormat sdf_1 = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		
		try {
			AghParametros hospital = parametroFacade.obterAghParametro(AghuParametrosEnum.P_HOSPITAL_RAZAO_SOCIAL);
			params.put("hospitalLocal", hospital.getVlrTexto());
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		params.put("dataAtual", sdf_1.format(dataAtual));
		params.put("nomeRelatorio", "Relatório de sumários de altas pendentes");
		
		return params;
	}

	public void pesquisar() {
		
		if (!validadarData()){
			return;
		}
		
		this.dataModel.reiniciarPaginator();		
	}
	
	@Override
	public List<AinInternacao> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return internacaoFacade.pesquisarPacientesComSumarioAltaPendenteList(firstResult, maxResult, orderProperty, asc,this.dataInicial, this.dataFinal, 
				this.aghUnidadesFuncionaisQuartoVO != null ? this.aghUnidadesFuncionaisQuartoVO.getSeq() : null);
		
	}
	
	@Override
	public Long recuperarCount() {
		return internacaoFacade.pesquisarPacientesComSumarioAltaPendenteCount(this.dataInicial, this.dataFinal, 
				this.aghUnidadesFuncionaisQuartoVO != null ? this.aghUnidadesFuncionaisQuartoVO.getSeq() : null);
	}
	
	private Boolean validadarData() {
		if (dataFinal!=null && dataFinal.before(dataInicial)){
			apresentarMsgNegocio(Severity.ERROR, AAC_00145);
			return false;
		}else{
			return true;
		}
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
	
	public DominioPeriodoDiasMeses getPeriodo() {
		return periodo;
	}

	public void setPeriodo(DominioPeriodoDiasMeses periodo) {
		this.periodo = periodo;
	}

	public AghUnidadesFuncionaisVO getAghUnidadesFuncionaisQuartoVO() {
		return aghUnidadesFuncionaisQuartoVO;
	}

	public void setAghUnidadesFuncionaisQuartoVO(AghUnidadesFuncionaisVO aghUnidadesFuncionaisQuartoVO) {
		this.aghUnidadesFuncionaisQuartoVO = aghUnidadesFuncionaisQuartoVO;
	}
	
	public DynamicDataModel<AinInternacao> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<AinInternacao> dataModel) {
		this.dataModel = dataModel;
	}

	public Integer getAtdSeq() {
		return atdSeq;
	}

	public void setAtdSeq(Integer atdSeq) {
		this.atdSeq = atdSeq;
	}
	
}