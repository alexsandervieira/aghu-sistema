package br.gov.mec.aghu.exames.solicitacao.action;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Inject;

import br.gov.mec.aghu.core.business.SelectionQualifier;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.utils.DateFormatUtil;
import br.gov.mec.aghu.dominio.DominioSimNaoRotina;
import br.gov.mec.aghu.dominio.DominioSolicitacaoExameLote;
import br.gov.mec.aghu.exames.solicitacao.business.ISolicitacaoExameFacade;
import br.gov.mec.aghu.exames.solicitacao.business.SelecionarRadioDefaultManager;
import br.gov.mec.aghu.exames.solicitacao.business.TipoCampoDataHoraISE;
import br.gov.mec.aghu.exames.solicitacao.vo.DataProgramadaVO;
import br.gov.mec.aghu.exames.solicitacao.vo.ItemSolicitacaoExameVO;
import br.gov.mec.aghu.exames.solicitacao.vo.LoteDefaultVO;
import br.gov.mec.aghu.exames.solicitacao.vo.SolicitacaoExameVO;
import br.gov.mec.aghu.exames.solicitacao.vo.TipoLoteVO;
import br.gov.mec.aghu.exames.solicitacao.vo.UnfExecutaSinonimoExameVO;
import br.gov.mec.aghu.model.AelExameHorarioColeta;
import br.gov.mec.aghu.model.AelPermissaoUnidSolic;
import br.gov.mec.aghu.model.AelSitItemSolicitacoes;



@SuppressWarnings("PMD.HierarquiaControllerIncorreta")
@SelectionQualifier
public class ListarExamesSendoSolicitadosLoteController extends ListarExamesSendoSolicitadosController {

	private static final long serialVersionUID = -4990868492102512373L;

	@EJB
	private ISolicitacaoExameFacade solicitacaoExameFacade;

	private DominioSolicitacaoExameLote radioTipoLote;
	private TipoLoteVO tipoLoteVO;
	private List<TipoLoteVO> listaLotes;
	private List<UnfExecutaSinonimoExameVO> listaExamesLote = null;
	private List<UnfExecutaSinonimoExameVO> listaExamesLoteSemPermissao = null;
	private Short tipoLoteSeq;

	@Inject
	private SolicitacaoExameController solicitacaoExameController;

	private Map<String, UnfExecutaSinonimoExameVO> exames = new HashMap<String, UnfExecutaSinonimoExameVO>();
	private String exameSiglaSelecionado = "";
	private boolean habilitarListExames = false;

	private Boolean checkUrgente = Boolean.FALSE;
	private Date dataProgr = new Date();
	private AelSitItemSolicitacoes situacao;
	private Boolean calendar = Boolean.TRUE;

	private Integer numeroAmostra;
	private Date intervaloHoras;
	private Integer intervaloDias;
		
	private Boolean exibirModalExamesSemPermissao;
	private String modalMessage;
	private String modalListaExames;

	private List<DataProgramadaVO> listaDatasHorasProgramadas;
	
	private ItemSolicitacaoExameVO itemSolicitacaoExameVo = new ItemSolicitacaoExameVO();
	
	private Boolean flagPermissaoSimNao = Boolean.FALSE;
	
	private Date dataHoraProgramada;
	
	public enum ListarExamesSendoSolicitadosLoteControllerException implements BusinessExceptionCode{
		
	}
	
	public void inicio(){
		numeroAmostra = Integer.valueOf(0);
		intervaloDias = Integer.valueOf(0);
		intervaloHoras = new Date(); 
		exibirModalExamesSemPermissao = Boolean.FALSE;
		modalMessage = "";
		modalListaExames = "";
		apresentarAbas();
		renderAbaPorLote(solicitacaoExameController.getSolicitacaoExame());
	}
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	
	public void renderAbaPorLote(SolicitacaoExameVO solicEx) {
		this.habilitarListExames=false;
		this.initController(solicEx);
		this.initRadios();
	}

	private void initRadios() {
		SelecionarRadioDefaultManager selecionarRadioDefault = new SelecionarRadioDefaultManager();

		LoteDefaultVO loteDefaultVO = selecionarRadioDefault.getRadioDefault(getExamesFacade(), getItemSolicitacaoExameVo().getSolicitacaoExameVO());
		this.setRadioTipoLote(loteDefaultVO.getTipoLoteDefault());

		valueChangeRadioTipoLote();//Carrega os lotes na inicialização

		setTipoLoteSeq(loteDefaultVO.getLoteDefault() == null ? null : loteDefaultVO.getLoteDefault().getSeq());

		if (getTipoLoteSeq() != null) {
			Integer seqAtendimento = null;
			if (this.getSolicitacaoExameVo() != null && this.getSolicitacaoExameVo().getAtendimento() != null && this.getSolicitacaoExameVo().getAtendimento().getSeq() != null){
				seqAtendimento = this.getSolicitacaoExameVo().getAtendimento().getSeq();
			}				
			listaExamesLote = this.solicitacaoExameFacade.pesquisaUnidadeExecutaSinonimoExameLote(getTipoLoteSeq(), seqAtendimento, solicitacaoExameController.isUsuarioSolicExameProtocoloEnfermagem(),solicitacaoExameController.isOrigemInternacao() );
		}		
		this.setExibirModalExamesSemPermissao(false);
		this.setModalMessage(null);
		this.setExibirModalExamesSemPermissao(false);
		this.setModalMessage(null);

	}

	public DominioSolicitacaoExameLote getRadioTipoLote() {
		return radioTipoLote;
	}

	public void setRadioTipoLote(DominioSolicitacaoExameLote radioTipoLote) {
		this.radioTipoLote = radioTipoLote;
	}

	public void valueChangeRadioTipoLote() {
		setTipoLoteSeq(null);
		initController();

		listaLotes = solicitacaoExameFacade.getDadosLote(this.getRadioTipoLote());
		clearExames();
	}

	public String getLabelComboTipoLote() {
		String labelComboTipoLote = "";
		switch (getRadioTipoLote()) {
		case E:
			labelComboTipoLote = this.getBundle().getString("LABEL_COMBO_TIPO_LOTE_ESPECIALIDADES");			
			break;
		case U:
			labelComboTipoLote = this.getBundle().getString("LABEL_COMBO_TIPO_LOTE_UNIDADES");			
			break;
		case G:
			labelComboTipoLote = this.getBundle().getString("LABEL_COMBO_TIPO_LOTE_GRUPOS");			
			break;
		default:
			break;
		}
		return labelComboTipoLote;
	}

	public String getTitleComboTipoLote() {
		String message = "";
		switch (getRadioTipoLote()) {
		case E:
			message = this.getBundle().getString("TITLE_COMBO_TIPO_LOTE_ESPECIALIDADES");			
			break;
		case U:
			message = this.getBundle().getString("TITLE_COMBO_TIPO_LOTE_UNIDADES");			
			break;
		case G:
			message = this.getBundle().getString("TITLE_COMBO_TIPO_LOTE_GRUPOS");			
			break;
		default:
			break;
		}
		return message;
	}

	public void reloadExames() {
		Boolean isSus;
		Short seqUnidade;
		clearExames();
		if (tipoLoteSeq != null && tipoLoteSeq != -1) {
			if(this.getSolicitacaoExameVo() != null){
				if(this.getSolicitacaoExameVo().getUnidadeFuncional() != null){
					seqUnidade = this.getSolicitacaoExameVo().getUnidadeFuncional().getSeq();
					isSus = this.solicitacaoExameFacade.verificaConvenioSus(this.getSolicitacaoExameVo().getAtendimento(), this.getSolicitacaoExameVo().getAtendimentoDiverso());
					Integer seqAtendimento = null;
					if (this.getSolicitacaoExameVo() != null && this.getSolicitacaoExameVo().getAtendimento() != null && this.getSolicitacaoExameVo().getAtendimento().getSeq() != null){
						seqAtendimento = this.getSolicitacaoExameVo().getAtendimento().getSeq();
					}				
					listaExamesLote = this.solicitacaoExameFacade.pesquisaUnidadeExecutaSinonimoExameLote(tipoLoteSeq,seqUnidade,isSus, seqAtendimento, solicitacaoExameController.isUsuarioSolicExameProtocoloEnfermagem(),solicitacaoExameController.isOrigemInternacao());
					listaExamesLoteSemPermissao = this.solicitacaoExameFacade.pesquisaUnidadeExecutaSinonimoExameLoteSemPermissoes(tipoLoteSeq,listaExamesLote, seqAtendimento, solicitacaoExameController.isUsuarioSolicExameProtocoloEnfermagem(),solicitacaoExameController.isOrigemInternacao());
					if(listaExamesLote.size() > 0){
						obterMessagemListaExamesSemPermissao();
						this.setExibirModalExamesSemPermissao(true);
					}else{
						this.setModalMessage(null);
						this.setModalListaExames(null);
						this.setExibirModalExamesSemPermissao(false);
					}
				}
			}	
		}
		
		List<ItemSolicitacaoExameVO> listaItemSolicitacaoExame = solicitacaoExameController.getListaItemSolicitacaoExame();
		List<UnfExecutaSinonimoExameVO> listaAuxiliar = new ArrayList<UnfExecutaSinonimoExameVO>(); 
		listaAuxiliar.addAll(listaExamesLote);
		
		if (listaItemSolicitacaoExame != null && !listaItemSolicitacaoExame.isEmpty()
				&& listaExamesLote!=null && !listaExamesLote.isEmpty()) {
			
			for (UnfExecutaSinonimoExameVO itemUnfExecutaSinonimoExameVO : listaAuxiliar) {
				for (ItemSolicitacaoExameVO itemSolicitacaoExameVO : listaItemSolicitacaoExame) {
					if (itemSolicitacaoExameVO.getUnfExecutaExame().getUnfExecutaExame().equals(itemUnfExecutaSinonimoExameVO.getUnfExecutaExame())) {
						listaExamesLote.remove(itemUnfExecutaSinonimoExameVO);
					}
				}
			}
		}

		if(listaExamesLote!=null && !listaExamesLote.isEmpty()){
			habilitarListExames = true;
		}else{
			habilitarListExames = false;
			
			if (solicitacaoExameController.isUsuarioSolicExameProtocoloEnfermagem() && getTipoLoteSeq() != null){
				apresentarMsgNegocio(Severity.INFO, this.getBundle().getString("USUARIO_SEM_PROTOCOLO_EXAMES_GRUPO"));
			}
		}
	}

	private void obterMessagemListaExamesSemPermissao() {
		
		String mensagem = "";
		String loteExame = "";		
		StringBuffer listaExamesSemPermissao = new StringBuffer();
		for (TipoLoteVO tipoLoteVO : this.getListaLotes()) {
			if(tipoLoteVO != null){
				if(tipoLoteVO.getLeuSeq().intValue() == tipoLoteSeq.intValue()){
					loteExame = tipoLoteVO.getDescricao();
				}
			}
		}		
		Integer qtdExames = 0;
		
		for (UnfExecutaSinonimoExameVO  unfExecutaSinonimoExameVO : listaExamesLoteSemPermissao) {
			boolean achou = false;
			for (UnfExecutaSinonimoExameVO vo : listaExamesLote) {
				if (vo.getDescricaoExameFormatada().equalsIgnoreCase(unfExecutaSinonimoExameVO.getDescricaoExameFormatada())){
					achou = true;
				}
			}
			if (!achou){
				listaExamesSemPermissao.append(unfExecutaSinonimoExameVO.getDescricaoExameFormatada());
				listaExamesSemPermissao.append("; \n ");
				qtdExames++;
			}
		}
		mensagem = this.getBundle().getString("LABEL_MODAL_EXAMES_SEM_PERMISSAO").replace("{0}", loteExame).
				replace("{1}", qtdExames.toString() != null?qtdExames.toString(): "").
				replace("{2}", getSolicitacaoExameVo().getUnidadeFuncional().getSeq() != null? getSolicitacaoExameVo().getUnidadeFuncional().getSeq().toString() + " - " +getSolicitacaoExameVo().getUnidadeFuncional().getDescricao(): ""); 
		this.setModalMessage(mensagem);
		this.setModalListaExames(listaExamesSemPermissao.toString());		
	}

	public void ocultarModalExamesSemPermissao(){
		setExibirModalExamesSemPermissao(Boolean.FALSE);
	}
	
	@Override
	protected List<ISECamposObrigatoriosValidator> getAbasValidators() {
		List<ISECamposObrigatoriosValidator> validators = new ArrayList<ISECamposObrigatoriosValidator>();

		if (getItemSolicitacaoExameVo().getMostrarAbaConcentO2()) {
			ISECamposObrigatoriosValidator itemSolicitacaoExameValidator = new AbaConcentO2LoteValidator(getItemSolicitacaoExameVo(), this.getBundle());
			validators.add(itemSolicitacaoExameValidator);
		}
		if (getItemSolicitacaoExameVo().getMostrarAbaIntervColeta()) {
			ISECamposObrigatoriosValidator itemSolicitacaoExameValidator = new AbaIntervColetaLoteValidator(getItemSolicitacaoExameVo(), this.getBundle());
			validators.add(itemSolicitacaoExameValidator);
		}
		if (getItemSolicitacaoExameVo().getMostrarAbaNoAmostras()) {
			ISECamposObrigatoriosValidator itemSolicitacaoExameValidator = new AbaNoAmostrasLoteValidator(getItemSolicitacaoExameVo(), this.getBundle());
			validators.add(itemSolicitacaoExameValidator);
		}
		if (getItemSolicitacaoExameVo().getMostrarAbaRegMatAnalise()) {
			ISECamposObrigatoriosValidator itemSolicitacaoExameValidator = new AbaRegMatAnaliseLoteValidator(getItemSolicitacaoExameVo(), this.getBundle());
			validators.add(itemSolicitacaoExameValidator);
		}
		if (getItemSolicitacaoExameVo().getMostrarAbaTipoTransporte()) {
			ISECamposObrigatoriosValidator itemSolicitacaoExameValidator = new AbaTipoTransporteLoteValidator(getItemSolicitacaoExameVo(), this.getBundle());
			validators.add(itemSolicitacaoExameValidator);
		}
		if (getItemSolicitacaoExameVo().getMostrarAbaQuestionario()) {
			ISECamposObrigatoriosValidator itemSolicitacaoExameValidator = new AbaQuestionarioValidator(getItemSolicitacaoExameVo(), this.getBundle(), 1);
			validators.add(itemSolicitacaoExameValidator);
		}
		if (getItemSolicitacaoExameVo().getMostrarAbaQuestionarioSismama()) {
			ItemSolicitacaoExameVO itemVO = new ItemSolicitacaoExameVO();
			itemVO.setQuestionarioSismama(getQuestionarioSismama());
			ISECamposObrigatoriosValidator itemSolicitacaoExameValidator = new AbaQuestionarioSismamaValidator(getItemSolicitacaoExameVo(),
					this.getBundle(), "1");
			validators.add(itemSolicitacaoExameValidator);
		}

		return validators;
	}

	public void adicionarItemSolicitacaoExameLote() throws BaseException, ParseException {
		ocultarModalExamesSemPermissao();
		
		if (Boolean.FALSE.equals(this.isExisteItemExameLoteSelecionado())) {
			apresentarMsgNegocio(Severity.ERROR, "ERRO_AO_ADICIONAR_EXAME_LOTE");
		}
		
		if(checkUrgente){
			checkUrgenteLote();
		}
		
		List<UnfExecutaSinonimoExameVO> listaAuxiliar = new ArrayList<UnfExecutaSinonimoExameVO>();
		List<UnfExecutaSinonimoExameVO> listaAuxiliar2 = new ArrayList<UnfExecutaSinonimoExameVO>();
		boolean adicionou = false;
		
		if (getUnfExecutaExame() != null && listaExamesLote != null) {
			listaAuxiliar.addAll(listaExamesLote);
			for (UnfExecutaSinonimoExameVO unfExeExameVO : listaExamesLote) {
				if (unfExeExameVO.isSelecionado()) {
					
					verificarPermissaoExameProgramado(unfExeExameVO);
					
					super.setUnfExecutaExame(unfExeExameVO, false);
					
					popularItemSolicitacaoExameVo(unfExeExameVO);
					adicionou = super.adicionarItemSolicitacaoExame();
					
					if(!getCalendar()){
						for (ItemSolicitacaoExameVO vo : solicitacaoExameController.getListaItemSolicitacaoExame()) {
							if(vo.getUnfExecutaExame().getSinonimoExameNome() != null){
								if(vo.getUnfExecutaExame().getSinonimoExameNome().equalsIgnoreCase(unfExeExameVO.getSinonimoExameNome())){
									vo.setDataProgramada(getDataHoraProgramada());
								}
							}
						} 
						super.getItemSolicitacaoExameVo().setDataProgramada(getDataHoraProgramada());
						getItemSolicitacaoExameVo().setDataProgramada(getDataHoraProgramada());
						unfExeExameVO.setDataProgr(getDataHoraProgramada());
					}else{
						for (ItemSolicitacaoExameVO vo : solicitacaoExameController.getListaItemSolicitacaoExame()) {
							if(vo.getUnfExecutaExame().getSinonimoExameNome() != null){
								if(vo.getUnfExecutaExame().getSinonimoExameNome().equalsIgnoreCase(unfExeExameVO.getSinonimoExameNome())){
									vo.setDataProgramada(getDataProgr());
								}	
							}
						}
						super.getItemSolicitacaoExameVo().setDataProgramada(getDataProgr());
						getItemSolicitacaoExameVo().setDataProgramada(getDataProgr());
						unfExeExameVO.setDataProgr(getDataProgr());
					}
					if(adicionou){//só exclui da lista de exames se adicionou com sucesso.
						listaAuxiliar2.add(unfExeExameVO);
					}
				}
			}
		}

		for (UnfExecutaSinonimoExameVO unfExeExameVO2 : listaAuxiliar2) {
			for (UnfExecutaSinonimoExameVO unfExeExameVO : listaExamesLote) {
				if(unfExeExameVO2.getUnfExecutaExame().equals(unfExeExameVO.getUnfExecutaExame())){
					listaAuxiliar.remove(unfExeExameVO);
				}
			}
		}
		if (!listaAuxiliar2.isEmpty()) {
			listaExamesLote.clear();
			listaExamesLote.addAll(listaAuxiliar);
		}

        desmarcaExameDaListaQueNaoCampoObrigatorioPreenchido();
        listaDatasHorasProgramadas.clear();
        apresentarAbas();
        inicio();
        setDataHoraProgramada(null);
        setDataProgr(new Date());
        setCalendar(Boolean.TRUE);
        setFlagPermissaoSimNao(Boolean.FALSE);
        getExames().clear();
	}

	private void verificarPermissaoExameProgramado(
			UnfExecutaSinonimoExameVO unfExeExameVO) throws ParseException {
		Integer emaManSeq = unfExeExameVO.getUnfExecutaExame().getId().getEmaManSeq();
		String sigla = unfExeExameVO.getUnfExecutaExame().getId().getEmaExaSigla();
		Short unfSeqSolicitante = solicitacaoExameController.getSolicitacaoExame().getUnidadeFuncional().getSeq();
		Short unfSeq = unfExeExameVO.getUnfExecutaExame().getId().getUnfSeq().getSeq();
		
		AelPermissaoUnidSolic aelPermissaoUnidSolic = buscarAelPermissaoUnidSolicPorEmaExaSiglaEmaManSeqUnfSeqUnfSeqSolicitante(
				emaManSeq, sigla, unfSeqSolicitante, unfSeq);
		
		if(aelPermissaoUnidSolic != null && 
		   aelPermissaoUnidSolic.getIndPermiteProgramarExames().equals(DominioSimNaoRotina.S)){
				boolean retorno = verificarHorarioExame(emaManSeq, sigla);
				if(retorno){
					return;
				}
		}
	}

	private void popularItemSolicitacaoExameVo(
			UnfExecutaSinonimoExameVO unfExeExameVO) throws BaseException {
		super.getItemSolicitacaoExameVo().setNumeroAmostra(unfExeExameVO.getNumeroAmostra());
		super.getItemSolicitacaoExameVo().setIntervaloHoras(unfExeExameVO.getIntervaloHoras());
		super.getItemSolicitacaoExameVo().setIntervaloDias(unfExeExameVO.getIntervaloDias());
		AelSitItemSolicitacoes situacaoExame = unfExeExameVO.getSituacao();
		
		if(situacaoExame==null){
		    situacaoExame = this.solicitacaoExameFacade.obterSituacaoExameSugestao(this.getSolicitacaoExameVo().getUnidadeFuncional(),
			this.getSolicitacaoExameVo().getAtendimento(), this.getSolicitacaoExameVo().getAtendimentoDiverso(), this.getItemSolicitacaoExameVo()
			.getUnfExecutaExame().getUnfExecutaExame(), obterUnidadeTrabalho(), this.getItemSolicitacaoExameVo(), getSolicitacaoExameVo());
		}
		
		super.getItemSolicitacaoExameVo().setSituacaoCodigo(situacaoExame);
		super.getItemSolicitacaoExameVo().setUrgente(getCheckUrgente());
		getItemSolicitacaoExameVo().setUrgente(getCheckUrgente());
		if(!getCalendar()){
			super.getItemSolicitacaoExameVo().setDataProgramada(getDataHoraProgramada());
			getItemSolicitacaoExameVo().setDataProgramada(getDataHoraProgramada());
		}else{
			super.getItemSolicitacaoExameVo().setDataProgramada(getDataProgr());
			getItemSolicitacaoExameVo().setDataProgramada(getDataProgr());
		}
		
	}

	private AelPermissaoUnidSolic buscarAelPermissaoUnidSolicPorEmaExaSiglaEmaManSeqUnfSeqUnfSeqSolicitante(
			Integer emaManSeq, String sigla, Short unfSeqSolicitante, Short unfSeq) {
		return solicitacaoExameFacade.buscarAelPermissaoUnidSolicPorEmaExaSiglaEmaManSeqUnfSeqUnfSeqSolicitante(sigla, emaManSeq, unfSeq, unfSeqSolicitante);
	}

	private boolean verificarHorarioExame(Integer emaManSeq, String sigla)
			throws ParseException {
		AelExameHorarioColeta aehc = solicitacaoExameFacade.recuperarHorarioColetas(sigla, emaManSeq);
		boolean retorno = false;
		
		if(aehc == null){
			retorno = true;
		} else {
			String horaInicial = DateFormatUtil.formataHoraMinuto(aehc.getHorarioInicial());
			String horaFinal = DateFormatUtil.formataHoraMinuto(aehc.getHorarioFinal());
			String horaProgramada = DateFormatUtil.formataHoraMinuto(itemSolicitacaoExameVo.getDataProgramada());
			
			SimpleDateFormat formato = new SimpleDateFormat("hh:mm");
			
			Date dataIncial = formato.parse(horaInicial); 
			Date dataFinal = formato.parse(horaFinal);
			Date dataProgramada = formato.parse(horaProgramada);
			
			if(!(dataProgramada.after(dataIncial) && dataProgramada.before(dataFinal))){
				apresentarMsgNegocio(Severity.ERROR, "Unidade não executa este exame neste horário.");
				retorno = true;
			}
			
		}
		return retorno;
	}

	private void apresentarAbas() {
		getItemSolicitacaoExameVo().setMostrarAbaTipoTransporte(Boolean.FALSE);
		getItemSolicitacaoExameVo().setMostrarAbaIntervColeta(Boolean.FALSE);
		getItemSolicitacaoExameVo().setMostrarAbaNoAmostras(Boolean.FALSE);
		getItemSolicitacaoExameVo().setMostrarAbaConcentO2(Boolean.FALSE);
		getItemSolicitacaoExameVo().setMostrarAbaRegMatAnalise(Boolean.FALSE);
		getItemSolicitacaoExameVo().setMostrarAbaRecomendacoes(Boolean.FALSE);
		getItemSolicitacaoExameVo().setMostrarAbaExamesOpcionais(Boolean.FALSE);
	}

    private void desmarcaExameDaListaQueNaoCampoObrigatorioPreenchido() {

        if(listaExamesLote != null){
            for (UnfExecutaSinonimoExameVO unfExeExameVO : this.listaExamesLote) {
                if (unfExeExameVO.isSelecionado()) {
                    unfExeExameVO.setSelecionado(false);
                }
            }
        }
    }
	
	private boolean isExisteItemExameLoteSelecionado() {
		if(listaExamesLote != null){
			for (UnfExecutaSinonimoExameVO unfExeExameVO : this.listaExamesLote) {
				if (unfExeExameVO.isSelecionado()) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	protected ISECamposObrigatoriosValidator getValidatorISE() {
		return new ItemSolicitacaoExameLoteValidator(getItemSolicitacaoExameVo(), this.getBundle());
	}

	private void clearExames() {
		listaExamesLote = new ArrayList<UnfExecutaSinonimoExameVO>();
		setUnfExecutaExame(null);
		initController();
	}


	@Override
	public String getAbaId(){
		return "ABA2_";
	}	


	public TipoLoteVO getTipoLoteVO() {
		return tipoLoteVO;
	}

	public void setTipoLoteVO(TipoLoteVO tipoLoteVO) {
		this.tipoLoteVO = tipoLoteVO;
	}

	public void setTipoLoteSeq(Short tipoLoteSeq) {
		this.tipoLoteSeq = tipoLoteSeq;
	}

	public Short getTipoLoteSeq() {
		return this.tipoLoteSeq;
	}

	public List<TipoLoteVO> getListaLotes() {
		return listaLotes;
	}

	public void setListaLotes(List<TipoLoteVO> listaLotes) {
		this.listaLotes = listaLotes;
	}

	public List<UnfExecutaSinonimoExameVO> getListaExamesLote() {
		return listaExamesLote;
	}

	public void setListaExamesLote(List<UnfExecutaSinonimoExameVO> listaExamesLote) {
		this.listaExamesLote = listaExamesLote;
	}

	public void selecionaItemExame(String codigoSoeSelecionado, UnfExecutaSinonimoExameVO vo) {
		super.setUnfExecutaExame(vo, true);
		this.setExameSiglaSelecionado(codigoSoeSelecionado);
		setFiltrosAbaSituacao(vo);
		
		if(this.exames.containsKey(codigoSoeSelecionado)){
			
			verificaSimNaoRotina();
			this.exames.remove(codigoSoeSelecionado);
			
		}else{
			this.exames.put(codigoSoeSelecionado, vo);
			ItemSolicitacaoExameVO ise = new ItemSolicitacaoExameVO();
			ise.setUnfExecutaExame(new UnfExecutaSinonimoExameVO());
			ise.setUnfExecutaExame(vo);
			
			DominioSimNaoRotina simNaoRotina = null;
			
			Integer emaManSeq = vo.getUnfExecutaExame().getId().getEmaManSeq();
			String sigla = vo.getUnfExecutaExame().getId().getEmaExaSigla();
			Short unfSeqSolicitante = solicitacaoExameController.getSolicitacaoExame().getUnidadeFuncional().getSeq();
			Short unfSeq = vo.getUnfExecutaExame().getId().getUnfSeq().getSeq();
			
			AelPermissaoUnidSolic aelPermissaoUnidSolic = buscarAelPermissaoUnidSolicPorEmaExaSiglaEmaManSeqUnfSeqUnfSeqSolicitante(
					emaManSeq, sigla, unfSeqSolicitante, unfSeq);
			
			if(aelPermissaoUnidSolic != null){
				simNaoRotina = aelPermissaoUnidSolic.getIndPermiteProgramarExames();
			}
			
			if(simNaoRotina.getDescricao().equalsIgnoreCase("rotina")){
				setFlagPermissaoSimNao(Boolean.TRUE);
			}
			
			if(getFlagPermissaoSimNao()){
				simNaoRotina = DominioSimNaoRotina.R;
			}
			
			switch (simNaoRotina) {
			case N:
				setCalendar(Boolean.TRUE);
				break;
			case S:
				setCalendar(Boolean.TRUE);
				break;
			case R:
				recuperarHorarioExamesProgramados(ise);
				break;
			}
		}
		verificarNenhumItemSelecionado();
		this.checkUrgente = false;
	}

	private void verificarNenhumItemSelecionado() {
		if(!listaExamesLote.isEmpty()){
			boolean selecionado = true;
			
			for (UnfExecutaSinonimoExameVO use : listaExamesLote) {
				if(use.isSelecionado()){
					selecionado = false;
					break;
				}
			} 
			if(selecionado){
				setCalendar(Boolean.TRUE);
				getItemSolicitacaoExameVo().setDataProgramada(new Date());
				setFlagPermissaoSimNao(Boolean.FALSE);
			}
		}
	}

	private void recuperarHorarioExamesProgramados(ItemSolicitacaoExameVO ise) {
		
		List<DataProgramadaVO> listaAux = doGetListaDatasHorasProgramadas(ise);
		List<DataProgramadaVO> listaTemp = new ArrayList<DataProgramadaVO>();
		
		verificarDataHoraRepetida(listaAux, listaTemp);
		
		if(getListaDataHoraProgramada() != null && getListaDataHoraProgramada().size() > 0){
			setCalendar(Boolean.FALSE);
		}
	}

	private void verificarDataHoraRepetida(List<DataProgramadaVO> listaAux,
			List<DataProgramadaVO> listaTemp) {
		if(getListaDatasHorasProgramadas().size() == 0){
			getListaDatasHorasProgramadas().addAll(listaAux);
		}else{
			for (DataProgramadaVO dataProgramadaVO : listaAux) {
				for (DataProgramadaVO dp : getListaDatasHorasProgramadas()) {
					if(dataProgramadaVO.getDate().compareTo(dp.getDate()) == 0 && listaTemp.contains(dp.getDate())){
						listaTemp.add(dp);
					}
				}
			}
			getListaDatasHorasProgramadas().removeAll(listaTemp);
		}
	}
	
	/**
	 * Carrega a lista de dt/hr de rotina para aquele exame, unidade executora e unidade solicitante.
	 */
	public List<DataProgramadaVO> doGetListaDatasHorasProgramadas(ItemSolicitacaoExameVO itemVO) {
		List<DataProgramadaVO> lista = null;
		if (itemVO != null) {
			try {
				lista = solicitacaoExameFacade.getHorariosRotina(itemVO, solicitacaoExameController.getSolicitacaoExame().getUnidadeFuncional());
			} catch (BaseException e) {
				apresentarExcecaoNegocio(e);
			}
		}

		return lista;
	}

	private void setFiltrosAbaSituacao(UnfExecutaSinonimoExameVO vo) {
		if(vo.isEditado()){
			setCheckUrgente(vo.getCheckUrgente());
			setDataProgr(vo.getDataProgr()!=null?vo.getDataProgr():new Date()); 
			setSituacao(vo.getSituacao());
			setCalendar(vo.getCalendar());
		}else{
			alterar();
		}
	}

	@Override
	public void setUnfExecutaExame(UnfExecutaSinonimoExameVO unfExecutaExame) {
		if (unfExecutaExame == null) {
			super.setUnfExecutaExame(null);
			super.posDeleteActionSbExames();
		}
	}

	public void validarExame(ValueChangeEvent event) {
		if (event.getNewValue() != null) {
			UnfExecutaSinonimoExameVO unfExecutaExame = (UnfExecutaSinonimoExameVO)event.getNewValue();
			super.setUnfExecutaExame(unfExecutaExame, true);
		}
	}

	public void alterar(){

		if (getUnfExecutaExame() != null && listaExamesLote != null) {
			for (UnfExecutaSinonimoExameVO unfExeExameVO : listaExamesLote) {
				if(unfExeExameVO.isSelecionado()){
					unfExeExameVO.setSituacao(situacao);
					unfExeExameVO.setCheckUrgente(checkUrgente);
					unfExeExameVO.setDataProgr(itemSolicitacaoExameVo.getDataProgramada());
					unfExeExameVO.setEditado(Boolean.TRUE);
					unfExeExameVO.setCalendar(getCalendar());
				}
			}
		}
	}
	
	public void inserirHorarioRotinaItemNaLista(){
		if (getUnfExecutaExame() != null && listaExamesLote != null) {
			for (UnfExecutaSinonimoExameVO unfExeExameVO : listaExamesLote) {
				if(unfExeExameVO.isSelecionado()){
					unfExeExameVO.setSituacao(situacao);
					unfExeExameVO.setCheckUrgente(checkUrgente);
					unfExeExameVO.setDataProgr(getDataHoraProgramada());
					unfExeExameVO.setEditado(Boolean.TRUE);
					unfExeExameVO.setCalendar(getCalendar());
				}
			}
		}
	}
	
	
	/**
	 * Método invocado quando o usuário marca/desmarca o checkbox "Urgente".
	 */
	public void checkUrgenteLote() {
		if (this.getItemSolicitacaoExameVo().getUnfExecutaExame() != null) {
			try {
				getItemSolicitacaoExameVo().setUrgente(getCheckUrgente());
				getItemSolicitacaoExameVo().setDataProgramada(new Date());
				AelSitItemSolicitacoes situacao = this.solicitacaoExameFacade.obterSituacaoExameSugestao(this.getSolicitacaoExameVo().getUnidadeFuncional(),
						this.getSolicitacaoExameVo().getAtendimento(), this.getSolicitacaoExameVo().getAtendimentoDiverso(), this.getItemSolicitacaoExameVo()
						.getUnfExecutaExame().getUnfExecutaExame(), obterUnidadeTrabalho(), this.getItemSolicitacaoExameVo(), getSolicitacaoExameVo());
				this.getItemSolicitacaoExameVo().setSituacaoCodigo(situacao);
				setSituacao(situacao);
				alterar();
				verificaSimNaoRotinaSelecionado();
			} catch (BaseException e) {
				apresentarExcecaoNegocio(e);
			}
		}
	}
	
	private void verificaSimNaoRotinaSelecionado() {
		
	DominioSimNaoRotina simNaoRotina = null;
		
		for (UnfExecutaSinonimoExameVO exameVO : this.exames.values()) {
			Integer emaManSeq = exameVO.getUnfExecutaExame().getId().getEmaManSeq();
			String sigla = exameVO.getUnfExecutaExame().getId().getEmaExaSigla();
			Short unfSeqSolicitante = solicitacaoExameController.getSolicitacaoExame().getUnidadeFuncional().getSeq();
			Short unfSeq = exameVO.getUnfExecutaExame().getId().getUnfSeq().getSeq();
			
			AelPermissaoUnidSolic aelPermissaoUnidSolic = buscarAelPermissaoUnidSolicPorEmaExaSiglaEmaManSeqUnfSeqUnfSeqSolicitante(
					emaManSeq, sigla, unfSeqSolicitante, unfSeq);
			
			if(aelPermissaoUnidSolic != null){
				simNaoRotina = aelPermissaoUnidSolic.getIndPermiteProgramarExames();
			}
		
			if(exameVO.isSelecionado()) {
				if(simNaoRotina.equals(DominioSimNaoRotina.R) && !checkUrgente) {
					setCalendar(Boolean.FALSE);
					break;
				} else {
					setCalendar(Boolean.TRUE);
				}
			}
		}
	}
	
	private void verificaSimNaoRotina() {
		
		DominioSimNaoRotina simNaoRotina = null;
		
		for (UnfExecutaSinonimoExameVO exameVO : this.exames.values()) {
			Integer emaManSeq = exameVO.getUnfExecutaExame().getId().getEmaManSeq();
			String sigla = exameVO.getUnfExecutaExame().getId().getEmaExaSigla();
			Short unfSeqSolicitante = solicitacaoExameController.getSolicitacaoExame().getUnidadeFuncional().getSeq();
			Short unfSeq = exameVO.getUnfExecutaExame().getId().getUnfSeq().getSeq();
			
			AelPermissaoUnidSolic aelPermissaoUnidSolic = buscarAelPermissaoUnidSolicPorEmaExaSiglaEmaManSeqUnfSeqUnfSeqSolicitante(
					emaManSeq, sigla, unfSeqSolicitante, unfSeq);
			
			if(aelPermissaoUnidSolic != null){
				simNaoRotina = aelPermissaoUnidSolic.getIndPermiteProgramarExames();
			}
			
			if(exameVO.isSelecionado()){
				if(simNaoRotina.equals(DominioSimNaoRotina.R)) {
					setCalendar(Boolean.FALSE);
					break;
				}else{
					setCalendar(Boolean.TRUE);
				}
			}
		}
		
	}

	public void reLoadDataHoraProgramadaLote() {
		if (this.getItemSolicitacaoExameVo().getUnfExecutaExame() != null) {
			try {
				this.desenhaTipoCampoDataLote();

				//Se estiver sendo colocado calendar, atualizar para data atual.
				if (getItemSolicitacaoExameVo().getCalendar()) {
					setDataProgr(new Date());
				}
			} catch (BaseException e) {
				apresentarExcecaoNegocio(e);
			}
		}
	}
	
	private void desenhaTipoCampoDataLote() throws BaseException {
		if (getItemSolicitacaoExameVo() != null && getSolicitacaoExameVo() != null) {
			Boolean calendar = Boolean.TRUE;
			calendar = (solicitacaoExameFacade.verificarCampoDataHora(getItemSolicitacaoExameVo(), getSolicitacaoExameVo().getUnidadeFuncional()) == TipoCampoDataHoraISE.CALENDAR);
			getItemSolicitacaoExameVo().setCalendar(calendar);
			setCalendar(calendar);
		}

		if (!getItemSolicitacaoExameVo().getCalendar()) {
			sugerirUltimoHorarioRotinaLote();
		}
	}

	private void sugerirUltimoHorarioRotinaLote() {
		if (getItemSolicitacaoExameVoSugestao() != null && !this.getItemSolicitacaoExameVo().getCalendar()) {
			setDataProgr(getItemSolicitacaoExameVoSugestao().getDataProgramada());
		}
	}
	
	public void zerarController() {
		radioTipoLote = null;
		tipoLoteVO = null;
		listaLotes = null;
		listaExamesLote = null;
		listaExamesLoteSemPermissao = null;
		tipoLoteSeq = null;

		exames = new HashMap<String, UnfExecutaSinonimoExameVO>();
		exameSiglaSelecionado = null;
		habilitarListExames = false;

		//filtros aba situação
		//#83554 - Por padrão deverá trazer o checkbox urgente como FALSE.
		checkUrgente = Boolean.FALSE;
		dataProgr = new Date();
		situacao = null;
		calendar = Boolean.TRUE;

		//#2253
		numeroAmostra = null;
		intervaloHoras = null;
		intervaloDias = null;
			
		//#31396 LABEL_MODAL_EXAMES_SEM_PERMISSAO
		exibirModalExamesSemPermissao = null;
		modalMessage = null;
		modalListaExames = null;
	}
	
	public Map<String, UnfExecutaSinonimoExameVO> getExames() {
		return exames;
	}

	public void setExames(Map<String, UnfExecutaSinonimoExameVO> exames) {
		this.exames = exames;
	}

	public String getExameSiglaSelecionado() {
		return exameSiglaSelecionado;
	}

	public void setExameSiglaSelecionado(String exameSiglaSelecionado) {
		this.exameSiglaSelecionado = exameSiglaSelecionado;
	}

	public boolean isHabilitarListExames() {
		return habilitarListExames;
	}

	public void setHabilitarListExames(boolean habilitarListExames) {
		this.habilitarListExames = habilitarListExames;
	}

	public Boolean getCheckUrgente() {
		return checkUrgente;
	}

	public void setCheckUrgente(Boolean checkUrgente) {
		this.checkUrgente = checkUrgente;
	}

	public Date getDataProgr() {
		return dataProgr;
	}

	public void setDataProgr(Date dataProgr) {
		this.dataProgr = dataProgr;
	}

	public AelSitItemSolicitacoes getSituacao() {
		return situacao;
	}

	public void setSituacao(AelSitItemSolicitacoes situacao) {
		this.situacao = situacao;
	}

	public Boolean getCalendar() {
		return calendar;
	}

	public void setCalendar(Boolean calendar) {
		this.calendar = calendar;
	}
	
	public Integer getNumeroAmostra() {
		return numeroAmostra;
	}

	public void setNumeroAmostra(Integer numeroAmostra) {
		this.numeroAmostra = numeroAmostra;
	}

	public Date getIntervaloHoras() {
		return intervaloHoras;
	}

	public void setIntervaloHoras(Date intervaloHoras) {
		this.intervaloHoras = intervaloHoras;
	}

	public Integer getIntervaloDias() {
		return intervaloDias;
	}

	public void setIntervaloDias(Integer intervaloDias) {
		this.intervaloDias = intervaloDias;
	}

	public String getModalMessage() {
		return modalMessage;
	}

	public void setModalMessage(String modalMessage) {
		this.modalMessage = modalMessage;
	}

	public String getModalListaExames() {
		return modalListaExames;
	}

	public void setModalListaExames(String modalListaExames) {
		this.modalListaExames = modalListaExames;
	}

	public Boolean getExibirModalExamesSemPermissao() {
		return exibirModalExamesSemPermissao;
	}

	public void setExibirModalExamesSemPermissao(
			Boolean exibirModalExamesSemPermissao) {
		this.exibirModalExamesSemPermissao = exibirModalExamesSemPermissao;
		if(!exibirModalExamesSemPermissao){
			this.modalMessage = null;
		}
	}
	public List<DataProgramadaVO> getListaDatasHorasProgramadas() {
		if(listaDatasHorasProgramadas == null){
			listaDatasHorasProgramadas = new ArrayList<DataProgramadaVO>();
		}
		return listaDatasHorasProgramadas;
	}
	public void setListaDatasHorasProgramadas(
			List<DataProgramadaVO> listaDatasHorasProgramadas) {
		this.listaDatasHorasProgramadas = listaDatasHorasProgramadas;
	}
	public ItemSolicitacaoExameVO getItemSolicitacaoExameVo() {
		return itemSolicitacaoExameVo;
	}
	public void setItemSolicitacaoExameVo(
			ItemSolicitacaoExameVO itemSolicitacaoExameVo) {
		this.itemSolicitacaoExameVo = itemSolicitacaoExameVo;
	}
	public Boolean getFlagPermissaoSimNao() {
		return flagPermissaoSimNao;
	}
	public void setFlagPermissaoSimNao(Boolean flagPermissaoSimNao) {
		this.flagPermissaoSimNao = flagPermissaoSimNao;
	}
	public Date getDataHoraProgramada() {
		return dataHoraProgramada;
	}
	public void setDataHoraProgramada(Date dataHoraProgramada) {
		this.dataHoraProgramada = dataHoraProgramada;
	}
}
