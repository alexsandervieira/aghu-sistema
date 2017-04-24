package br.gov.mec.aghu.internacao.action;

import java.util.List;

import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.internacao.pesquisa.action.PesquisaSolicitacaoInternacaoPaginatorController;
import br.gov.mec.aghu.internacao.solicitacao.business.ISolicitacaoInternacaoFacade;
import br.gov.mec.aghu.model.AinSolicitacoesInternacao;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FatVlrItemProcedHospComps;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;


			 	
public class NegativaAtendimentoSolicitacaoInternacaoController extends ActionController {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -9059689219166832043L;
	
	private static final String REDIRECT_PESQUISA_SOLICITACAO_INTERNACAO = "internacao-pesquisaSolicitacaoInternacao";
	
	private AinSolicitacoesInternacao negarSolicitacaoInternacao;
	
	private AipPacientes paciente;
	
	private FatVlrItemProcedHospComps fatVlrItemProcedHospComps;
	
	private Integer codPaciente;

	private Integer seqSolicitacaoInternacao;
	
	@EJB
	private IPacienteFacade pacienteFacade;
	
	@EJB
	private ISolicitacaoInternacaoFacade solicitacaoInternacaoFacade;

	@Inject
	private PesquisaSolicitacaoInternacaoPaginatorController pesquisaSolicitacaoInternacaoPaginatorController;


	public void inicio() {
		
		this.negarSolicitacaoInternacao = solicitacaoInternacaoFacade.obterAinSolicitacoesInternacao(seqSolicitacaoInternacao);
		this.paciente = pacienteFacade.obterPacientePorCodigo(codPaciente);
	}
	
	public String gravar(){
		if (this.fatVlrItemProcedHospComps != null){
			this.negarSolicitacaoInternacao.setProcedimento(fatVlrItemProcedHospComps.getFatItensProcedHospitalar());  
		}
		this.pesquisaSolicitacaoInternacaoPaginatorController.reiniciarPaginator();
		try {
			this.solicitacaoInternacaoFacade.negarSolicitacaoInternacao(this.negarSolicitacaoInternacao);
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_NEGACAO_INTERNACAO");
			return REDIRECT_PESQUISA_SOLICITACAO_INTERNACAO;
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);			
			return null;
		}
	}
	
	public List<FatVlrItemProcedHospComps> pesquisarSsm(String descricaoSsm) throws ApplicationBusinessException {
		return solicitacaoInternacaoFacade.pesquisarFatVlrItemProcedHospComps(descricaoSsm, this.getPaciente(), null);
	}
	
	public String cancelar(){
		return REDIRECT_PESQUISA_SOLICITACAO_INTERNACAO;
	}
	
	
	public AinSolicitacoesInternacao getSolicitacaoInternacao() {
		return negarSolicitacaoInternacao;
	}


	public void setSolicitacaoInternacao(AinSolicitacoesInternacao negarSolicitacaoInternacao) {
		this.negarSolicitacaoInternacao = negarSolicitacaoInternacao;
	}

	public Integer getSeqSolicitacaoInternacao() {
		return seqSolicitacaoInternacao;
	}

	public void setSeqSolicitacaoInternacao(Integer seqSolicitacaoInternacao) {
		this.seqSolicitacaoInternacao = seqSolicitacaoInternacao;
	}

	public AinSolicitacoesInternacao getNegarSolicitacaoInternacao() {
		return negarSolicitacaoInternacao;
	}

	public void setNegarSolicitacaoInternacao(AinSolicitacoesInternacao negarSolicitacaoInternacao) {
		this.negarSolicitacaoInternacao = negarSolicitacaoInternacao;
	}

	public AipPacientes getPaciente() {
		return paciente;
	}

	public void setPaciente(AipPacientes paciente) {
		this.paciente = paciente;
	}

	public Integer getCodPaciente() {
		return codPaciente;
	}

	public void setCodPaciente(Integer codPaciente) {
		this.codPaciente = codPaciente;
	}

	public FatVlrItemProcedHospComps getFatVlrItemProcedHospComps() {
		return fatVlrItemProcedHospComps;
	}

	public void setFatVlrItemProcedHospComps(FatVlrItemProcedHospComps fatVlrItemProcedHospComps) {
		this.fatVlrItemProcedHospComps = fatVlrItemProcedHospComps;
	}
	
}
