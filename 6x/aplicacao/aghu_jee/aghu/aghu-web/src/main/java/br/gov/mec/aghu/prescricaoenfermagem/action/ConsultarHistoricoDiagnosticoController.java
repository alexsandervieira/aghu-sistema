package br.gov.mec.aghu.prescricaoenfermagem.action;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.prescricaoenfermagem.IPrescricaoEnfermagemFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.dominio.DominioPeriodoRegistroControlePaciente;
import br.gov.mec.aghu.dominio.DominioSituacaoHistPrescDiagnosticos;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.EpeDiagnostico;
import br.gov.mec.aghu.model.EpeHistoricoPrescDiagnosticos;
import br.gov.mec.aghu.model.EpePrescricaoEnfermagem;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.prescricaomedica.vo.BuscaConselhoProfissionalServidorVO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.vo.RapServidoresVO;

/**
 * 
 * @author rodrigo.jornooki
 *
 */

public class ConsultarHistoricoDiagnosticoController extends ActionController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1780957824568673735L;


	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	private static final Log LOG = LogFactory.getLog(ConsultarHistoricoDiagnosticoController.class);

	private static final String MANTER_PRESCRICAO_ENFERMAGEM = "prescricaoenfermagem-manterPrescricaoEnfermagem";

	private static final String CONSULTAR_HISTORICO_DIAGNOSTICO = "prescricaoenfermagem-consutarHistoricoDiagnostico";
	
	private static final String PRESCRICAOENFERMAGEM_LISTA_PACIENTES_ENFERMAGEM = "/pages/prescricaoenfermagem/listaPacientesEnfermagem.xhtml";
	
	private static final String AAC_00145 = "AAC_00145";

	@EJB
	private IPrescricaoEnfermagemFacade prescricaoEnfermagemFacade;
	
	@EJB
	private IPacienteFacade pacienteFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	private AipPacientes paciente;
	
	private EpeDiagnostico diagnostico;
	
	private Integer codigoPaciente;
	
	private Integer prontuario;
	
	private Integer atendimentoSeq;
	
	private Date dataInicial;
	
	private Date dataFinal;
	
	private DominioPeriodoRegistroControlePaciente periodo;
	
	private RapServidoresVO profissional;
	
	private List<EpeHistoricoPrescDiagnosticos> historicoDiagnosticos;
	
	private String cameFrom;
	
	private EpePrescricaoEnfermagem prescricaoEnfermagem;

	private String abaCorrente;

	private String voltarPara;

	
	public void iniciar() {
	 
		if (codigoPaciente != null) {
			this.paciente = this.pacienteFacade.obterPaciente(this.codigoPaciente);
		}else if (prontuario != null) {
			this.paciente = this.pacienteFacade.obterPacientePorProntuario(prontuario);
		} 
	}
	
	public String consultaHistoricoDiagnostico() {
		return CONSULTAR_HISTORICO_DIAGNOSTICO;
	}
	
	
	public String obterSituacaoProfissional(EpeHistoricoPrescDiagnosticos historico){
		
		StringBuilder situacaoProfissional = new StringBuilder(100);
		
		if(historico.getSituacao().equals(DominioSituacaoHistPrescDiagnosticos.C) || 
				historico.getSituacao().equals(DominioSituacaoHistPrescDiagnosticos.I)){ 
			
			situacaoProfissional.append(historico.getSituacao().getDescricao())
			.append(" por ");
		}
		return situacaoProfissional.toString();
	}
	
	public String obterProfissional(EpeHistoricoPrescDiagnosticos historico){
		
		BuscaConselhoProfissionalServidorVO vo = null;
		
		try {
			vo = prescricaoMedicaFacade.buscaConselhoProfissionalServidorVO(historico.getServidor().getId().getMatricula(),
					historico.getServidor().getId().getVinCodigo());
		} catch (ApplicationBusinessException e) {
			LOG.info("Erro ao obter profissional");
		}
		
		StringBuilder profissionalHist = new StringBuilder(100);
		
	
		profissionalHist.append(historico.getServidor().getPessoaFisica().getNome());
		
		if(vo != null && vo.getSiglaConselho() != null){
			profissionalHist.append(" - ")
			.append(vo.getSiglaConselho())
			.append(vo.getNumeroRegistroConselho());
		}
		
		return profissionalHist.toString();
	}
	
	public void pesquisar() throws ApplicationBusinessException{
		
		if (this.dataInicial == null) {
			apresentarMsgNegocio(Severity.ERROR, "Data e Hora Inicial: Campo obrigatório, digite um valor.");
			return;
		}
		if (!validadarData()){
			return;
		}
		
		List<DominioSituacaoHistPrescDiagnosticos> listSituacao = new ArrayList<DominioSituacaoHistPrescDiagnosticos>();
		
		if(abaCorrente.equals("0")){
			listSituacao.add(DominioSituacaoHistPrescDiagnosticos.I); 
			listSituacao.add(DominioSituacaoHistPrescDiagnosticos.C); 
		} else if(abaCorrente.equals("1")){
			listSituacao.add(DominioSituacaoHistPrescDiagnosticos.E); 
		} if(abaCorrente.equals("2")){
			listSituacao.add(DominioSituacaoHistPrescDiagnosticos.X); 
		}
		
		this.historicoDiagnosticos = prescricaoEnfermagemFacade.pesquisarHistoricoDiagnostico(this.atendimentoSeq, listSituacao, this.diagnostico, 
				this.profissional, this.dataInicial, this.dataFinal);
	}
		
	private Boolean validadarData() {
		if (dataFinal!=null && dataFinal.before(dataInicial)){
			apresentarMsgNegocio(Severity.ERROR, AAC_00145);
			return false;
		}else{
			return true;
		}
	}
	
	public void limpar(){
		this.diagnostico = null;
		this.profissional = null;
		this.dataInicial = null;
		this.dataFinal = null;
		this.historicoDiagnosticos = null;
		this.periodo = null;
		
	}
	
	public String cancelarEdicao(){
		if(this.cameFrom.equalsIgnoreCase("listaPacientesEnfermagem")) {
			limpar();
			voltarPara = PRESCRICAOENFERMAGEM_LISTA_PACIENTES_ENFERMAGEM;
		}else if (this.cameFrom.equalsIgnoreCase("prescricaoenfermagem-manterPrescricaoEnfermagem")){
			limpar();
			voltarPara = MANTER_PRESCRICAO_ENFERMAGEM;
		}
		return voltarPara;
	}
	
	public List<EpeDiagnostico> pesquisarDiagnosticos(String filtro) {
		return this.getPrescricaoEnfermagemFacade().pesquisarDiagnosticosPorAtendimento(filtro, this.atendimentoSeq);
	}
	
	
	public String redirecionarManterPrescricaoEnfermagem() {
		return MANTER_PRESCRICAO_ENFERMAGEM;
	}
	
	public List<RapServidoresVO> pesquisarRapServidoresPorAtendimento(String parametro) {
		return registroColaboradorFacade.pesquisarRapServidoresPorAtendimento(parametro, this.atendimentoSeq);
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
	
	public AipPacientes getPaciente() {
		return paciente;
	}

	public void setPaciente(AipPacientes paciente) {
		this.paciente = paciente;
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
	
	public String getAbaCorrente() {
		return abaCorrente;
	}

	public void setAbaCorrente(String abaCorrente) {
		this.abaCorrente = abaCorrente;
	}

	public DominioPeriodoRegistroControlePaciente getPeriodo() {
		return periodo;
	}

	public void setPeriodo(DominioPeriodoRegistroControlePaciente periodo) {
		this.periodo = periodo;
	}

	public Integer getCodigoPaciente() {
		return codigoPaciente;
	}

	public void setCodigoPaciente(Integer codigoPaciente) {
		this.codigoPaciente = codigoPaciente;
	}

	public IPrescricaoEnfermagemFacade getPrescricaoEnfermagemFacade() {
		return prescricaoEnfermagemFacade;
	}

	public void setPrescricaoEnfermagemFacade(IPrescricaoEnfermagemFacade prescricaoEnfermagemFacade) {
		this.prescricaoEnfermagemFacade = prescricaoEnfermagemFacade;
	}

	
	public RapServidoresVO getProfissional() {
		return profissional;
	}
	
	public void setProfissional(RapServidoresVO profissional) {
		this.profissional = profissional;
	}
	
	
	public EpeDiagnostico getDiagnostico() {
		return diagnostico;
	}

	public void setDiagnostico(EpeDiagnostico diagnostico) {
		this.diagnostico = diagnostico;
	}

	public Integer getAtendimentoSeq() {
		return atendimentoSeq;
	}

	public void setAtendimentoSeq(Integer atendimentoSeq) {
		this.atendimentoSeq = atendimentoSeq;
	}

	public List<EpeHistoricoPrescDiagnosticos> getHistoricoDiagnosticos() {
		return historicoDiagnosticos;
	}

	public void setHistoricoDiagnosticos(List<EpeHistoricoPrescDiagnosticos> historicoDiagnosticos) {
		this.historicoDiagnosticos = historicoDiagnosticos;
	}

	public String getCameFrom() {
		return cameFrom;
	}

	public void setCameFrom(String cameFrom) {
		this.cameFrom = cameFrom;
	}

	public EpePrescricaoEnfermagem getPrescricaoEnfermagem() {
		return prescricaoEnfermagem;
	}

	public void setPrescricaoEnfermagem(EpePrescricaoEnfermagem prescricaoEnfermagem) {
		this.prescricaoEnfermagem = prescricaoEnfermagem;
	}

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}
	
}