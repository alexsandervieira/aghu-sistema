package br.gov.mec.aghu.ambulatorio.action;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.ambulatorio.vo.VAacSiglaUnfSalaVO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.commons.WebUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AacGradeAgendamenConsultas;
import br.gov.mec.aghu.model.AacPermissaoAgendamentoConsultas;
import br.gov.mec.aghu.model.AghEquipes;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;

public class ManterPermissoesAgendamentoConsultaController extends ActionController {

	private static final long serialVersionUID = -5319478735294453198L;

	private static final String PAGE_MANTER_PERMISSOES_AGENDAMENTO_CONSULTA_LIST = "manterPermissoesAgendamentoConsultaList";
	
	@EJB
	private IAmbulatorioFacade ambulatorioFacade;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

    @EJB
    private IAghuFacade aghuFacade;

    @EJB
    private IParametroFacade parametroFacade;

    @Inject
    private ManterPermissoesAgendamentoConsultaPaginatorController manterPermissoesAgendamentoConsultaPaginatorController;
    
	private AacPermissaoAgendamentoConsultas permissaoAgendamentoConsultas;
	private AacPermissaoAgendamentoConsultas parametroSelecionado;
    private Integer seqGrade;
    private AghEspecialidades especialidade;
    private AghEquipes equipe;
    private RapServidores profissional;
	private VAacSiglaUnfSalaVO zona;
	private String labelZona;
	private String titleZona;
	private List<AacPermissaoAgendamentoConsultas> listPermissoesEspecialidade;
	private List<AacPermissaoAgendamentoConsultas> listPermissoesEquipe;
	private List<AacPermissaoAgendamentoConsultas> listPermissoesGrade;
	private List<AacPermissaoAgendamentoConsultas> listPermissoesZona;
	private List<AacPermissaoAgendamentoConsultas> listPermissoesProfissional;
	private boolean emEdicao;
	private boolean confirmaVoltar = false;
	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	public void iniciar(){

		this.confirmaVoltar = false;

		if(permissaoAgendamentoConsultas == null){
			permissaoAgendamentoConsultas = new AacPermissaoAgendamentoConsultas();
		}
	
		carregarParametros();
		carregarListas();
	}
	
	public void carregarListas() {
		listPermissoesEspecialidade = ambulatorioFacade.obterListaPermAgndConsPorServidorTipo(permissaoAgendamentoConsultas.getServidor(), "ESP");
		listPermissoesEquipe = ambulatorioFacade.obterListaPermAgndConsPorServidorTipo(permissaoAgendamentoConsultas.getServidor(), "EQP");
		listPermissoesGrade = ambulatorioFacade.obterListaPermAgndConsPorServidorTipo(permissaoAgendamentoConsultas.getServidor(), "GRD");
		listPermissoesZona = ambulatorioFacade.obterListaPermAgndConsPorServidorTipo(permissaoAgendamentoConsultas.getServidor(), "UNF");
		listPermissoesProfissional = ambulatorioFacade.obterListaPermAgndConsPorServidorTipo(permissaoAgendamentoConsultas.getServidor(), "PROF");
		
		this.emEdicao = (listPermissoesEspecialidade != null && !listPermissoesEspecialidade.isEmpty()) ||
				(listPermissoesEquipe != null && !listPermissoesEquipe.isEmpty()) ||
				(listPermissoesGrade != null && !listPermissoesGrade.isEmpty()) ||
				(listPermissoesZona != null && !listPermissoesZona.isEmpty()) ||
				(listPermissoesProfissional != null && !listPermissoesProfissional.isEmpty());
	}

	/**
	 * Busca servidor pelo vínculo e matrícula.
	 */
	public List<RapServidores> buscarServidor(String param) {
		return registroColaboradorFacade.pesquisarServidorVinculoAtivoEProgramadoAtual(param);
	}

	public void confirmar() {
		try {
			if(!validaCamposObrigatorios()){
				apresentarMsgNegocio(Severity.ERROR, "MSG_ERRO_PERMISSAO_AGENDAMENTO_CAMPOS_OBRIGATORIOS");
				return;
			}
			List<AacPermissaoAgendamentoConsultas> listPermissoesInserir = new ArrayList<AacPermissaoAgendamentoConsultas>();
			listPermissoesInserir.addAll(listPermissoesEspecialidade);
			listPermissoesInserir.addAll(listPermissoesEquipe);
			listPermissoesInserir.addAll(listPermissoesGrade);
			listPermissoesInserir.addAll(listPermissoesZona);
			listPermissoesInserir.addAll(listPermissoesProfissional);
			this.ambulatorioFacade.persistirPermissoesAgendamentoConsultas(permissaoAgendamentoConsultas.getServidor(),
					listPermissoesInserir);
			carregarListas();
			this.confirmaVoltar = false;
			apresentarMsgNegocio(Severity.INFO, "MSG_PERMISSAO_AGENDAMENTO_CONSULTA_GRAVADO_SUCESSO");
			
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

    private boolean validaCamposObrigatorios() {

		return permissaoAgendamentoConsultas.getServidor() != null &&
				((listPermissoesEspecialidade != null && !listPermissoesEspecialidade.isEmpty()) ||
				(listPermissoesEquipe != null && !listPermissoesEquipe.isEmpty()) ||
				(listPermissoesGrade != null && !listPermissoesGrade.isEmpty()) ||
				(listPermissoesZona != null && !listPermissoesZona.isEmpty()) ||
				(listPermissoesProfissional != null && !listPermissoesProfissional.isEmpty()));
	}

	public List<AghEspecialidades> obterEspecialidade(String parametro) {
   		return aghuFacade.pesquisarEspecialidadesAtivas((String) parametro);
    }

    public List<AghEquipes> obterEquipe(String parametro) {
	return aghuFacade.getListaEquipes((String) parametro);
    }

	public List<RapServidores> obterProfissionaisPorEquipe(String parametro) {
		return registroColaboradorFacade.listarServidoresComPessoaFisicaPorNome(parametro);
	}

	public List<VAacSiglaUnfSalaVO> obterZona(String objPesquisa) throws BaseException  {
		if (objPesquisa!=null && objPesquisa instanceof String){
			objPesquisa = ((String) objPesquisa).trim();
		}
		return ambulatorioFacade.pesquisarTodasZonas(objPesquisa);
	}

	public void adicionarEspecialidade(){
		if(especialidade == null){
			apresentarMsgNegocio(Severity.ERROR, "MSG_ERRO_PERMISSAO_AGENDAMENTO_CAMPO_ESPECIALIDADE_OBRIGATORIO");
			return;
		}
		AacPermissaoAgendamentoConsultas permissao = new AacPermissaoAgendamentoConsultas();
		permissao.setCriadoEm(new Date());
		permissao.setEspecialidade(especialidade);
		permissao.setIndSituacao(DominioSituacao.A);
		permissao.setServidor(permissaoAgendamentoConsultas.getServidor());
		listPermissoesEspecialidade.add(permissao);
		especialidade = null;
		this.confirmaVoltar = true;
	}

	public void adicionarEquipe(){
		if(equipe == null){
			apresentarMsgNegocio(Severity.ERROR, "MSG_ERRO_PERMISSAO_AGENDAMENTO_CAMPO_EQUIPE_OBRIGATORIO");
			return;
		}
		AacPermissaoAgendamentoConsultas permissao = new AacPermissaoAgendamentoConsultas();
		permissao.setCriadoEm(new Date());
		permissao.setEquipe(equipe);
		permissao.setIndSituacao(DominioSituacao.A);
		permissao.setServidor(permissaoAgendamentoConsultas.getServidor());
		listPermissoesEquipe.add(permissao);
		equipe = null;
		this.confirmaVoltar = true;
	}

	public void adicionarGrade(){
		if(seqGrade == null){
			apresentarMsgNegocio(Severity.ERROR, "MSG_ERRO_PERMISSAO_AGENDAMENTO_CAMPO_GRADE_OBRIGATORIO");
			return;
		}
		AacPermissaoAgendamentoConsultas permissao = new AacPermissaoAgendamentoConsultas();
		permissao.setCriadoEm(new Date());
		AacGradeAgendamenConsultas grade = ambulatorioFacade.obterGrade(seqGrade);
		if(grade == null){
			apresentarMsgNegocio(Severity.ERROR, "MSG_ERRO_GRADE_NAO_EXISTE");
			return;
		}
		permissao.setGrade(grade);
		permissao.setIndSituacao(DominioSituacao.A);
		permissao.setServidor(permissaoAgendamentoConsultas.getServidor());
		listPermissoesGrade.add(permissao);
		seqGrade = null;
		this.confirmaVoltar = true;
	}

	public void adicionarZona(){
		if(zona == null){
			apresentarMsgNegocio(Severity.ERROR, "MSG_ERRO_PERMISSAO_AGENDAMENTO_CAMPO_UNF_OBRIGATORIO");
			return;
		}
		AacPermissaoAgendamentoConsultas permissao = new AacPermissaoAgendamentoConsultas();
		permissao.setCriadoEm(new Date());
		AghUnidadesFuncionais unidadeFuncional = aghuFacade.obterUnidadeFuncional(zona.getUnfSeq());
		permissao.setUnidadeFuncional(unidadeFuncional);
		permissao.setIndSituacao(DominioSituacao.A);
		permissao.setServidor(permissaoAgendamentoConsultas.getServidor());
		listPermissoesZona.add(permissao);
		zona = null;
		this.confirmaVoltar = true;
	}

	public void adicionarProfissional(){
		if(profissional == null){
			apresentarMsgNegocio(Severity.ERROR, "MSG_ERRO_PERMISSAO_AGENDAMENTO_CAMPO_PROFISSIONAL_OBRIGATORIO");
			return;
		}
		AacPermissaoAgendamentoConsultas permissao = new AacPermissaoAgendamentoConsultas();
		permissao.setCriadoEm(new Date());
		permissao.setProfissional(profissional);
		permissao.setIndSituacao(DominioSituacao.A);
		permissao.setServidor(permissaoAgendamentoConsultas.getServidor());
		listPermissoesProfissional.add(permissao);
		profissional = null;
		this.confirmaVoltar = true;
	}

	public void removerPermissaoEspecialidade() throws ApplicationBusinessException{
		if(parametroSelecionado != null){
			listPermissoesEspecialidade.remove(parametroSelecionado);
			parametroSelecionado = null;
			this.confirmaVoltar = true;
		}
	}

	public void removerPermissaoEquipe() throws ApplicationBusinessException{
		if(parametroSelecionado != null){
			listPermissoesEquipe.remove(parametroSelecionado);
			parametroSelecionado = null;
			this.confirmaVoltar = true;
		}
	}

	public void removerPermissaoGrade() throws ApplicationBusinessException{
		if(parametroSelecionado != null){
			listPermissoesGrade.remove(parametroSelecionado);
			parametroSelecionado = null;
			this.confirmaVoltar = true;
		}
	}

	public void removerPermissaoZona() throws ApplicationBusinessException{
		if(parametroSelecionado != null){
			listPermissoesZona.remove(parametroSelecionado);
			parametroSelecionado = null;
			this.confirmaVoltar = true;
		}
	}

	public void removerPermissaoProfissional() throws ApplicationBusinessException{
		if(parametroSelecionado != null){
			listPermissoesProfissional.remove(parametroSelecionado);
			parametroSelecionado = null;
			this.confirmaVoltar = true;
		}
	}

	public String cancelar() {
		this.permissaoAgendamentoConsultas = null;
		this.emEdicao = false;
		this.manterPermissoesAgendamentoConsultaPaginatorController.pesquisar();
		return PAGE_MANTER_PERMISSOES_AGENDAMENTO_CONSULTA_LIST;
	}

	public String verificaPendenciasVoltar() {
		if (isConfirmaVoltar()) {
			openDialog("modalConfirmacaoPendenciaWG");
			return null;
		}
		return cancelar();
	}

	public void ativarMensagemConfirmacaoVoltar(){
		this.confirmaVoltar = true;
		carregarListas();
	}
	
	private void carregarParametros() {
		try {
			this.labelZona = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_LABEL_ZONA).getVlrTexto();

			if (this.labelZona==null){
				this.labelZona="Zona";
			}
			this.titleZona = WebUtil.initLocalizedMessage("TITLE_ZONA_PERMISSAO_AGENDAMENTO_CONSULTA", null, this.labelZona);
			
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public AacPermissaoAgendamentoConsultas getPermissaoAgendamentoConsultas() {
		return permissaoAgendamentoConsultas;
	}

	public void setPermissaoAgendamentoConsultas(
			AacPermissaoAgendamentoConsultas permissaoAgendamentoConsultas) {
		this.permissaoAgendamentoConsultas = permissaoAgendamentoConsultas;
	}

	public Integer getSeqGrade() {
		return seqGrade;
	}

	public void setSeqGrade(Integer seqGrade) {
		this.seqGrade = seqGrade;
	}

	public AghEspecialidades getEspecialidade() {
		return especialidade;
	}

	public void setEspecialidade(AghEspecialidades especialidade) {
		this.especialidade = especialidade;
	}

	public AghEquipes getEquipe() {
		return equipe;
	}

	public void setEquipe(AghEquipes equipe) {
		this.equipe = equipe;
	}

	public RapServidores getProfissional() {
		return profissional;
	}

	public void setProfissional(RapServidores profissional) {
		this.profissional = profissional;
	}

	public VAacSiglaUnfSalaVO getZona() {
		return zona;
	}

	public void setZona(VAacSiglaUnfSalaVO zona) {
		this.zona = zona;
	}

	public String getLabelZona() {
		return labelZona;
	}

	public void setLabelZona(String labelZona) {
		this.labelZona = labelZona;
	}

	public String getTitleZona() {
		return titleZona;
	}

	public void setTitleZona(String titleZona) {
		this.titleZona = titleZona;
	}

	public List<AacPermissaoAgendamentoConsultas> getListPermissoesEspecialidade() {
		return listPermissoesEspecialidade;
	}

	public void setListPermissoesEspecialidade(
			List<AacPermissaoAgendamentoConsultas> listPermissoesEspecialidade) {
		this.listPermissoesEspecialidade = listPermissoesEspecialidade;
	}

	public List<AacPermissaoAgendamentoConsultas> getListPermissoesEquipe() {
		return listPermissoesEquipe;
	}

	public void setListPermissoesEquipe(
			List<AacPermissaoAgendamentoConsultas> listPermissoesEquipe) {
		this.listPermissoesEquipe = listPermissoesEquipe;
	}

	public List<AacPermissaoAgendamentoConsultas> getListPermissoesGrade() {
		return listPermissoesGrade;
	}

	public void setListPermissoesGrade(
			List<AacPermissaoAgendamentoConsultas> listPermissoesGrade) {
		this.listPermissoesGrade = listPermissoesGrade;
	}

	public List<AacPermissaoAgendamentoConsultas> getListPermissoesZona() {
		return listPermissoesZona;
	}

	public void setListPermissoesZona(
			List<AacPermissaoAgendamentoConsultas> listPermissoesZona) {
		this.listPermissoesZona = listPermissoesZona;
	}

	public List<AacPermissaoAgendamentoConsultas> getListPermissoesProfissional() {
		return listPermissoesProfissional;
	}

	public void setListPermissoesProfissional(
			List<AacPermissaoAgendamentoConsultas> listPermissoesProfissional) {
		this.listPermissoesProfissional = listPermissoesProfissional;
	}

	public AacPermissaoAgendamentoConsultas getParametroSelecionado() {
		return parametroSelecionado;
	}

	public void setParametroSelecionado(
			AacPermissaoAgendamentoConsultas parametroSelecionado) {
		this.parametroSelecionado = parametroSelecionado;
	}

	public boolean isEmEdicao() {
		return emEdicao;
	}

	public void setEmEdicao(boolean emEdicao) {
		this.emEdicao = emEdicao;
	}

	public boolean isConfirmaVoltar() {
		return confirmaVoltar;
	}

	public void setConfirmaVoltar(boolean confirmaVoltar) {
		this.confirmaVoltar = confirmaVoltar;
	}

}
