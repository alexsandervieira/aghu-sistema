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
import br.gov.mec.aghu.business.IAghuFacade;
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
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.internacao.vo.AghUnidadesFuncionaisVO;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.AinTipoCaracteristicaLeito;

import com.itextpdf.text.DocumentException;


/**
 * @author rodrigo.jornooki
 */
public class RelatorioTipoCaracteristicaLeitoController extends ActionReport implements ActionPaginator {

	private static final long serialVersionUID = 3967522972046913795L;	
	private static final Log LOG = LogFactory.getLog(RelatorioTipoCaracteristicaLeitoController.class);

	private enum RelatorioTipoCaracteristicaLeitoControllerExceptionCode implements BusinessExceptionCode {
		MENSAGEM_RELATORIO_VAZIO;
	}

	private static final String RELATORIO_CARAC_LEITO = "internacao-relatorioTipoCaracteristicaLeito";
	
	private static final String VISUALIZAR_RELATORIO_CARAC_LEITO = "internacao-visualizarRelatorioTipoCaracteristicaLeito";
	
	private static final String AAC_00145 = "AAC_00145";
	
	private Date dataInicial;
	
	private Date dataFinal;
	
	private String fileName;
	
	private StreamedContent streamedContent;
	
	private AghUnidadesFuncionaisVO aghUnidadesFuncionaisQuartoVO;	
	
	private AinTipoCaracteristicaLeito ainTipoCaracteristicaLeito;
	
	private AghEspecialidades especialidade;
	
	@EJB
	private IInternacaoFacade internacaoFacade; 
	
	@Inject
	private SistemaImpressao sistemaImpressao;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;
	
	
	@Inject @Paginator
	private DynamicDataModel<AinInternacao> dataModel;
	private List<AinInternacao> colecao;	
	
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
			return VISUALIZAR_RELATORIO_CARAC_LEITO;
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}

	}

	public String voltar() {
		return RELATORIO_CARAC_LEITO;
	}
	
	public StreamedContent getRenderPdf() throws IOException, JRException, SystemException, DocumentException, ApplicationBusinessException {	
		DocumentoJasper documento = gerarDocumento();
		return this.criarStreamedContentPdf(documento.getPdfByteArray(false));		
		
	}	
	
	public void gerarArquivo() throws NumberFormatException, IOException{
		if (!validadarData()){
			return;
		}

		Short unfSeq = null;
		if (this.aghUnidadesFuncionaisQuartoVO != null) {
			unfSeq = aghUnidadesFuncionaisQuartoVO.getSeq(); 
		}
		
		try {
			File file = internacaoFacade.geraArquivoCaracteristicasLeito(this.dataInicial, this.dataFinal, this.especialidade, unfSeq, this.ainTipoCaracteristicaLeito);
			if(file!= null){
				
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
	
	public void limpar() {
		this.dataFinal = null;
		this.dataInicial = null;
		this.ainTipoCaracteristicaLeito = null;
		this.especialidade = null;
		this.aghUnidadesFuncionaisQuartoVO = null;
		this.dataModel.limparPesquisa();	
	}
	
	public List<AghEspecialidades> listarEspecialidadesPorSigla(String paramPesquisa) {
		return this.aghuFacade.listarPorSiglaAtivas(paramPesquisa);
	}
	
	public List<AinTipoCaracteristicaLeito> pesquisarTiposCaracteristicasPorCodigoOuDescricao(String parametro) {	
		return this.cadastrosBasicosInternacaoFacade.pesquisarTiposCaracteristicasPorCodigoOuDescricao(parametro);
	}
	
	public List<AghUnidadesFuncionaisVO> pesquisarUnidadeFuncionalVOPorCodigoEDescricao(String parametro) {
		return this.cadastrosBasicosInternacaoFacade.pesquisarUnidadeFuncionalVOPorCodigoEDescricao(parametro);
	}

	private void carregarColecao() throws ApplicationBusinessException{
		colecao = internacaoFacade.pesquisarTipoCaracteristicasLeitos(this.dataInicial, this.dataFinal, 
				this.aghUnidadesFuncionaisQuartoVO != null ? this.aghUnidadesFuncionaisQuartoVO.getSeq() : null, this.ainTipoCaracteristicaLeito, this.especialidade);
		if(colecao == null || colecao.isEmpty()){
			throw new ApplicationBusinessException(RelatorioTipoCaracteristicaLeitoControllerExceptionCode.MENSAGEM_RELATORIO_VAZIO); 
		}
	}

	@Override
	public List<AinInternacao> recuperarColecao() throws ApplicationBusinessException {
		return colecao;
	}
	
	@Override
	public String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/internacao/report/relatorioTipoCaracteristicasLeito.jasper";
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
		params.put("nomeRelatorio", "Tipo de Característica de Leito");
		
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
		return internacaoFacade.pesquisaCaracteristicaList(firstResult, maxResult, orderProperty, asc,this.dataInicial, this.dataFinal, 
				this.aghUnidadesFuncionaisQuartoVO != null ? this.aghUnidadesFuncionaisQuartoVO.getSeq() : null, this.ainTipoCaracteristicaLeito, this.especialidade);
		
	}
	
	@Override
	public Long recuperarCount() {
		return internacaoFacade.pesquisaCaracteristicaListCount(this.dataInicial, this.dataFinal, 
				this.aghUnidadesFuncionaisQuartoVO != null ? this.aghUnidadesFuncionaisQuartoVO.getSeq() : null, this.ainTipoCaracteristicaLeito, this.especialidade);
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
	
	public AghUnidadesFuncionaisVO getAghUnidadesFuncionaisQuartoVO() {
		return aghUnidadesFuncionaisQuartoVO;
	}

	public void setAghUnidadesFuncionaisQuartoVO(
			AghUnidadesFuncionaisVO aghUnidadesFuncionaisQuartoVO) {
		this.aghUnidadesFuncionaisQuartoVO = aghUnidadesFuncionaisQuartoVO;
	}

	public AinTipoCaracteristicaLeito getAinTipoCaracteristicaLeito() {
		return ainTipoCaracteristicaLeito;
	}

	public void setAinTipoCaracteristicaLeito(AinTipoCaracteristicaLeito ainTipoCaracteristicaLeito) {
		this.ainTipoCaracteristicaLeito = ainTipoCaracteristicaLeito;
	}
	
	public AghEspecialidades getEspecialidade() {
		return especialidade;
	}

	public void setEspecialidade(AghEspecialidades especialidade) {
		this.especialidade = especialidade;
	}
	
	public StreamedContent getStreamedContent() {
		return streamedContent;
	}

	public void setStreamedContent(StreamedContent streamedContent) {
		this.streamedContent = streamedContent;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	public DynamicDataModel<AinInternacao> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<AinInternacao> dataModel) {
		this.dataModel = dataModel;
	}
	
}