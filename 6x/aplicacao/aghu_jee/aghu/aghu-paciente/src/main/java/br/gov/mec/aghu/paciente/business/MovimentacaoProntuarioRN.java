package br.gov.mec.aghu.paciente.business;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.casca.business.ICentralPendenciaFacade;
import br.gov.mec.aghu.casca.vo.PendenciaVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.dominio.DominioSituacaoMovimentoProntuario;
import br.gov.mec.aghu.dominio.DominioTipoEnvioProntuario;
import br.gov.mec.aghu.dominio.DominioTipoSolicitacaoProntuario;
import br.gov.mec.aghu.dominio.DominioTodosUltimo;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AipMovimentacaoProntuarios;
import br.gov.mec.aghu.model.AipPacienteProntuario;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.AipSolicitantesProntuario;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.dao.AipMovimentacaoProntuarioDAO;
import br.gov.mec.aghu.paciente.dao.AipPacienteProntuarioDAO;
import br.gov.mec.aghu.paciente.dao.AipSolicitantesProntuarioDAO;
import br.gov.mec.aghu.paciente.vo.SolicitanteVO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;

/**
 * RN que encapsula as regras de banco relativas a entidade
 * AipmovimentacaoProntuario.
 * 
 * @author gmneto
 * 
 */
@Stateless
public class MovimentacaoProntuarioRN extends BaseBusiness {

	private static final String SOLICITAÇÃO_DO_PRONTUARIO = "Solicitação do Prontuário: ";

	private enum MovimentacaoProntuarioRNExceptionCode implements BusinessExceptionCode {
		MSG_WARNING_SOLICITANTE_INEXISTENTE_ORIGEM_INTERNACAO;
	}
	
	@EJB
	private MovimentacaoProntuarioJournalON movimentacaoProntuarioJournalON;
	
	private static final Log LOG = LogFactory.getLog(MovimentacaoProntuarioRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private ICentralPendenciaFacade centralPendenciaFacade;
	
	@EJB
	private IAmbulatorioFacade ambulatorioFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@Inject
	private AipMovimentacaoProntuarioDAO aipMovimentacaoProntuarioDAO;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IAghuFacade aghuFacade;

	@EJB
	private ICascaFacade cascaFacade;

	@Inject
	private AipSolicitantesProntuarioDAO aipSolicitantesProntuarioDAO;

	@Inject
	private AipPacienteProntuarioDAO aipPacienteProntuarioDAO;
	/**
	 * 
	 */
	private static final long serialVersionUID = 2757389830538138867L;
	/**
	 * ORADB Procedure AIPP_GERA_MVOL_EVENT
	 * @param localAtual 
	 * @param aghUnidadesFuncionais 
	 * @param i 
	 * @param pConNumero
	 * @throws ApplicationBusinessException WW
	 *  
	 */	

	public void gerarMovimentacaoGeral(AipPacientes paciente, AipSolicitantesProntuario solicitante, Date dtMovimento, String strLocal, DominioSituacaoMovimentoProntuario situacao, String localAtual, AghUnidadesFuncionais aghUnidadesFuncionais, DominioTipoSolicitacaoProntuario tipoSolicitacao) throws ApplicationBusinessException {
		Short inicio = 0;
		Short fim = 0;
		if (solicitante != null) {
			if (solicitante.getVolumesManuseados().equals(DominioTodosUltimo.T)) {
				if (paciente != null && paciente.getVolumes() != null) {
					fim = paciente.getVolumes();
				}
			} else {
				if (paciente != null && paciente.getVolumes() != null) {
					inicio = paciente.getVolumes();
					fim = paciente.getVolumes();
				}
			}
			
			for (short x = inicio; x <= fim; x++) { 
				AipMovimentacaoProntuarios movProntuario = new AipMovimentacaoProntuarios();
				movProntuario.setVolumes(x);
				movProntuario.setTipoEnvio(DominioTodosUltimo.T.toString().equals(solicitante.getVolumesManuseados().toString()) ? DominioTipoEnvioProntuario.T : DominioTipoEnvioProntuario.U);
				movProntuario.setSituacao(situacao);
				movProntuario.setDataMovimento(dtMovimento);
				movProntuario.setDataRetirada(dtMovimento);
				movProntuario.setPaciente(paciente);
				movProntuario.setSolicitante(solicitante);
				movProntuario.setLocal(strLocal);
				movProntuario.setUnidadeSolicitante(aghUnidadesFuncionais);
				movProntuario.setIndTipoSolicitacao(tipoSolicitacao);
				verificarSePossuiOrigemCadastrada(paciente, movProntuario);
				if(StringUtils.isNotBlank(localAtual)){
					movProntuario.setLocalAtual(localAtual);
				}
				this.persistirAipMovimentacaoProntuario(movProntuario, null);
			}
		}
	}
	public void aippGeraMvolEvent(Integer pConNumero) throws ApplicationBusinessException {
		List<AacConsultas> listaConsultas = getAmbulatorioFacade().executarCursorConsulta(pConNumero);
		if (!listaConsultas.isEmpty()) {
			AacConsultas consulta = listaConsultas.get(0); //ver se pode retornar uma lista
			AghParametros parametroLimitePront = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_LIMIT_PRONT_VIRTUAL);
			
			/*	-- função retorna "S" para as consultas da zona 17 ou para pacientes identificados
				-- antes de 01/01/2006. Somente estes terão seus prontuários enviados ao ambu.
				-- Milena abril/2006 */
			if (consulta.getPaciente() != null && consulta.getPaciente().getProntuario() != null && 
					consulta.getPaciente().getProntuario() < parametroLimitePront.getVlrNumerico().longValue()) {
				//	-- para não gerar movimento para prontuários virtuais - Milena set/2005
				List<SolicitanteVO> listaSolicitanteVO = getAmbulatorioFacade().executaCursorSolicitante(
						consulta.getGradeAgendamenConsulta().getAacUnidFuncionalSala().getId().getUnfSeq(), 
						consulta.getGradeAgendamenConsulta().getAacUnidFuncionalSala().getId().getSala(), 
						consulta.getDtConsulta());
				
				Short inicio = 0;
				Short fim = 0;
				
				if (!listaSolicitanteVO.isEmpty()) {
					SolicitanteVO solicitanteVO = listaSolicitanteVO.get(0);
					if (solicitanteVO.getVolumesManuseados().equals(DominioTodosUltimo.T)) {
						if (consulta.getPaciente() != null && consulta.getPaciente().getVolumes() != null) {
							fim = consulta.getPaciente().getVolumes();
						}
					}
					else {
						if (consulta.getPaciente() != null && consulta.getPaciente().getVolumes() != null) {
							inicio = consulta.getPaciente().getVolumes();
							fim = consulta.getPaciente().getVolumes();
						}
					}
					
					for (short x = inicio; x <= fim; x++) { 
						AipMovimentacaoProntuarios movProntuario = new AipMovimentacaoProntuarios();
						movProntuario.setVolumes(x);
						if (DominioTodosUltimo.T.toString().equals(solicitanteVO.getVolumesManuseados().toString())) {
							movProntuario.setTipoEnvio(DominioTipoEnvioProntuario.T);
						} else {
							movProntuario.setTipoEnvio(DominioTipoEnvioProntuario.U);
						}
						movProntuario.setSituacao(DominioSituacaoMovimentoProntuario.Q); //TODO => rever essa function AACC_VER_UNF
						movProntuario.setDataMovimento(consulta.getDtConsulta());
						movProntuario.setDataRetirada(consulta.getDtConsulta());
						movProntuario.setPaciente(consulta.getPaciente());
						movProntuario.setSolicitante(getAipSolicitantesProntuarioDAO().obterPorChavePrimaria(solicitanteVO.getSeq()));
						if(consulta.getGradeAgendamenConsulta()!=null){
							movProntuario.setLocal(solicitanteVO.getZonaSala()+" "+consulta.getGradeAgendamenConsulta().getSeq()+" "+pConNumero);	
						} else {
							movProntuario.setLocal(solicitanteVO.getZonaSala()+" "+pConNumero);
						}
						verificarSePossuiOrigemCadastrada(consulta.getPaciente(), movProntuario);
						this.persistirAipMovimentacaoProntuario(movProntuario, null);
					}
				}
			}
		}
	}
	
	private void verificarSePossuiOrigemCadastrada(AipPacientes paciente,
			AipMovimentacaoProntuarios movProntuario) {
     	AipPacienteProntuario aipPacienteProntuario = getAipPacienteProntuarioDAO().obterPorChavePrimaria(paciente.getProntuario());
		if(aipPacienteProntuario != null && aipPacienteProntuario.getSamis() != null){
			movProntuario.setSamisOrigem(aipPacienteProntuario.getSamis());
			movProntuario.setLocalAtual(aipPacienteProntuario.getSamis().getDescricao());
		}
	}
		
	private AipPacienteProntuarioDAO getAipPacienteProntuarioDAO() {
		return aipPacienteProntuarioDAO;
	}

	/**
	 * Trigger executada antes de inserir uma movimentação de prontuários
	 * ORADB Trigger AIPT_MVP_BRI
	 * @param newMovPront
	 * @throws ApplicationBusinessException 
	 *  
	 */
	public void executarAntesInserirMovimentacaoProntuario(AipMovimentacaoProntuarios newMovPront) throws ApplicationBusinessException {
		   
		if ((newMovPront.getSituacao() == null || 
			newMovPront.getSituacao().equals(DominioSituacaoMovimentoProntuario.S) ||
			newMovPront.getSituacao().equals(DominioSituacaoMovimentoProntuario.R) ||
			newMovPront.getSituacao().equals(DominioSituacaoMovimentoProntuario.Q)) &&
			!newMovPront.getTipoEnvio().equals(DominioTipoEnvioProntuario.P)) {
			
			List<AipMovimentacaoProntuarios> lista = getAipMovimentacaoProntuarioDAO().pesquisarMovimentacaoPacienteProntuarioPorCodigoEVolume(
					newMovPront.getPaciente().getCodigo(), newMovPront.getVolumes(), DominioTipoEnvioProntuario.P);
			
			// Deixa o item com situacao = R por primeiro
			Collections.sort(lista, obterComparatorAipMovimentacaoProntuarios());  
			
			if (!lista.isEmpty()) { //se encontro resultados
				AipMovimentacaoProntuarios prontuarioPesquisa = lista.get(0);
				
				if (newMovPront.getSituacao().equals(DominioSituacaoMovimentoProntuario.R)) {
					if (prontuarioPesquisa.getSituacao().equals(DominioSituacaoMovimentoProntuario.R)) {
						newMovPront.setSituacao(DominioSituacaoMovimentoProntuario.R);
					}
				}
				else { 
					if (newMovPront.getSituacao().equals(DominioSituacaoMovimentoProntuario.Q)) {
						if (prontuarioPesquisa.getSituacao().equals(DominioSituacaoMovimentoProntuario.R) ||
							prontuarioPesquisa.getSituacao().equals(DominioSituacaoMovimentoProntuario.Q)) {
							newMovPront.setObservacoes("Local " + 
									prontuarioPesquisa.getLocal() + " " +
									DateUtil.dataToString(prontuarioPesquisa.getDataMovimento(), null) + 
									" Situação: " + prontuarioPesquisa.getSituacao());
							List<AacConsultas> consultaPesquisa = getAmbulatorioFacade().pesquisarAacConsultasPorCodigoEData(newMovPront.getPaciente().getCodigo(), prontuarioPesquisa.getDataMovimento());
							if (!consultaPesquisa.isEmpty()) {
								AacConsultas aacConsulta = consultaPesquisa.get(0);
								newMovPront.setObservacoes(newMovPront.getObservacoes() + ". Número=" + aacConsulta.getNumero());
							}
						}
					}
				}
			} 
		}
		
		if (newMovPront.getServidor() == null) {
			RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
			newMovPront.setServidor(servidorLogado);
		}
		
		//não precisa verificar pq foi criado domínio
		/*
		if (newMovPront.getTipoEnvio() != null) {
			if (!aipcCheckReferenceCode(newMovPront.getTipoEnvio().toString(), "TIPO_ENVIO_PRONTUARIO"))
				throw new ApplicationBusinessException(MovimentacaoProntuarioRNExceptionCode.AIP_00384);
		}
		
		if (newMovPront.getSituacao() != null) {
			if (!aipcCheckReferenceCode(newMovPront.getSituacao().toString(), "SIT_MOVIMENTO_PRONT"))
				throw new ApplicationBusinessException(MovimentacaoProntuarioRNExceptionCode.AIP_00386);
		}*/
		
		newMovPront.setCriadoEm(new Date());
	}	
	
	/**
	 * Comparator para deixar o item com situacao = 'R' por primeiro
	 * 
	 * @return
	 */
	protected Comparator<AipMovimentacaoProntuarios> obterComparatorAipMovimentacaoProntuarios() {
		Comparator<AipMovimentacaoProntuarios> result = null;

		result = new Comparator<AipMovimentacaoProntuarios>() {

			@Override
			public int compare(final AipMovimentacaoProntuarios o1, final AipMovimentacaoProntuarios o2) {

				int result = 1;

				if (o1.getSituacao().equals(DominioSituacaoMovimentoProntuario.R)) {
					result = -1;
				}
				
				return result;
			}
		};

		return result;
	}	

	/**
	 * ORADB Function AIPC_CHECK_REFERENCE_CODE
	 * 
	 * @param valor
	 * @param dominio
	 * @return
	 * 
	 * OBSERVAÇÃO IMPORTANTE: 
	 * Método foi comentado devido a remoção da model AipRefCode.
	 * Porém a implementação foi deixada aqui para fins de consulta por parte
	 * de algum desenvolvedor que algum dia tenha que dar alguma manutenção
	 * em alguma funcionalidade relacionada a isto. Mas se desejar, todo
	 * este comentários pode ser removido.
	 * 
	public boolean aipcCheckReferenceCode(String valor, String dominio) {
		return !getAipRefCodeDAO().pesquisarPorValorEDominio(valor, dominio).isEmpty();
	} */
	
	/**
	 * ORADB "AGH".AIPT_MVP_BRU
	 * @param movimentacao
	 */
	public void atualizarDataDevolucaoProntuario(AipMovimentacaoProntuarios newMovimentacaoProntuario,
			AipMovimentacaoProntuarios oldMovimentacaoProntuario) {
		
		if (CoreUtil.modificados(oldMovimentacaoProntuario.getSituacao(), newMovimentacaoProntuario.getSituacao())){
			if (DominioSituacaoMovimentoProntuario.D
					.equals(newMovimentacaoProntuario.getSituacao())
					&& !DominioSituacaoMovimentoProntuario.D
							.equals(oldMovimentacaoProntuario.getSituacao())) {
				
				newMovimentacaoProntuario.setDataDevolucao(new Date());
			}
			else if (DominioSituacaoMovimentoProntuario.D
					.equals(oldMovimentacaoProntuario.getSituacao())
					&& !DominioSituacaoMovimentoProntuario.D
							.equals(newMovimentacaoProntuario)) {
				
				newMovimentacaoProntuario.setDataDevolucao(null);
			}
		}
	}
	
	/**
	 * ORADB "AGH".AIPT_MVP_ARU
	 * 
	 * @param movimentacao
	 * @param usuarioLogado 
	 */
	public void gerarJournalUpdate(AipMovimentacaoProntuarios movimentacao, String usuarioLogado) {
		this.getMovimentacaoProntuarioJournalON()
				.observarPersistenciaMovimentacaoProntuario(movimentacao,
						DominioOperacoesJournal.UPD, usuarioLogado);
	}

	/**
	 * Metodo para persistir AipMovimentacaoProntuarios
	 * 
	 * @param newMovimentacaoProntuario
	 * @param oldMovimentacaoProntuario
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException 
	 */
	public void persistirAipMovimentacaoProntuario(AipMovimentacaoProntuarios newMovimentacaoProntuario,
			AipMovimentacaoProntuarios oldMovimentacaoProntuario) throws ApplicationBusinessException {
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		if (newMovimentacaoProntuario.getSeq() == null) {
			this.executarAntesInserirMovimentacaoProntuario(newMovimentacaoProntuario);
			this.getAipMovimentacaoProntuarioDAO().persistir(newMovimentacaoProntuario);
			this.getAipMovimentacaoProntuarioDAO().flush();
		} else {
			atualizarMovimentacaoProntuario(newMovimentacaoProntuario, oldMovimentacaoProntuario, servidorLogado);
		}
	}
	
	/**
	 * Método que executa as triggers de update de AipMovimentacaoProntuarios
	 * ORADB AIPT_MVP_ARU, AIPT_MVP_ASU e AIPT_MVP_BRU
	 * @param newMovimentacaoProntuario
	 * @param oldMovimentacaoProntuario
	 */
	public void atualizarMovimentacaoProntuario(AipMovimentacaoProntuarios newMovimentacaoProntuario,
			AipMovimentacaoProntuarios oldMovimentacaoProntuario, RapServidores servidorLogado){
		
		this.atualizarDataDevolucaoProntuario(newMovimentacaoProntuario, oldMovimentacaoProntuario);
		//Implementar AIPT_MVP_ASU 
		this.atualizarMovimentacaoProntuarioEnforceMvpRules(newMovimentacaoProntuario, oldMovimentacaoProntuario, servidorLogado);
		
		this.getAipMovimentacaoProntuarioDAO().merge(newMovimentacaoProntuario);
		this.getAipMovimentacaoProntuarioDAO().flush();
		this.gerarJournalUpdate(newMovimentacaoProntuario, servidorLogado.getUsuario());
	}
	
	/**
	 * ORADB AIPT_MVP_ASU, AIPP_ENFORCE_MVP_RULES
	 * @param newMovimentacaoProntuario
	 * @param oldMovimentacaoProntuario
	 * @param servidorLogado 
	 */
	public void atualizarMovimentacaoProntuarioEnforceMvpRules(
			AipMovimentacaoProntuarios newMovimentacaoProntuario,
			AipMovimentacaoProntuarios oldMovimentacaoProntuario, RapServidores servidorLogado) {
		
		if (CoreUtil.modificados(oldMovimentacaoProntuario.getSituacao(), newMovimentacaoProntuario.getSituacao())){
			this.atualizarObservacoesMovimentoRequerido(
					newMovimentacaoProntuario.getSeq(),
					newMovimentacaoProntuario.getPaciente(),
					newMovimentacaoProntuario.getVolumes(),
					newMovimentacaoProntuario.getLocal(), newMovimentacaoProntuario.getDataMovimento(),
					newMovimentacaoProntuario.getSituacao(), servidorLogado);
		}
		
		if (DominioSituacaoMovimentoProntuario.Q
				.equals(oldMovimentacaoProntuario.getSituacao())
				&& DominioSituacaoMovimentoProntuario.R
						.equals(newMovimentacaoProntuario.getSituacao())) {
			
			//AIPK_MVP_RN.RN_MVPP_ATU_MVTO_REQ(l_mvp_row_new.pac_codigo,l_mvp_row_new.volumes,l_mvp_row_new.seq);
			this.atualizarObservacoesMovimentoRetirado(newMovimentacaoProntuario.getSeq(),
					newMovimentacaoProntuario.getPaciente(),
					newMovimentacaoProntuario.getVolumes(), servidorLogado);
		}
		
	}
	
	/**
	 * Atualizar observações movimento requerido
	 * ORADB AIPK_MVP_RN.RN_MVPP_ATU_MVTO_REQ
	 * @param servidorLogado 
	 * @param newMovimentacaoProntuario
	 */
	private void atualizarObservacoesMovimentoRetirado(
			Integer seqMovimentacaoProntuario, AipPacientes paciente,
			Short volumes, RapServidores servidorLogado) {
		// TODO Auto-generated method stub
		
		List<AipMovimentacaoProntuarios> listaMovimentosRetirados = getAipMovimentacaoProntuarioDAO().
				pesquisarMovimentacaoPacienteVolumeRetirado(paciente.getCodigo(),
						volumes, seqMovimentacaoProntuario);
		
		if (!listaMovimentosRetirados.isEmpty()){
			for (AipMovimentacaoProntuarios movimentacao: listaMovimentosRetirados){
				AipMovimentacaoProntuarios movimentacaoOld = replicarMovimentacaoProntuarios(movimentacao);
				movimentacao.setSituacao(DominioSituacaoMovimentoProntuario.P);
				this.atualizarMovimentacaoProntuario(movimentacao, movimentacaoOld, servidorLogado);
			}
		}
	}

	/**
	 * Atualizar observações movimento requerido
	 * ORADB AIPK_MVP_RN.RN_MVPP_ATU_MVT_DUPL
	 * 
	 * @param paciente
	 * @param volumes
	 * @param local
	 * @param dataMovimento
	 * @param situacao
	 * @param servidorLogado 
	 */
	public void atualizarObservacoesMovimentoRequerido(
			Integer seqMovimentacaoProntuario, AipPacientes paciente,
			Short volumes, String local, Date dataMovimento,
			DominioSituacaoMovimentoProntuario situacao, RapServidores servidorLogado) {
		
		List<AipMovimentacaoProntuarios> listaMovimentosRequeridos = getAipMovimentacaoProntuarioDAO()
				.pesquisarMovimentacaoPacienteVolumeRequerido(paciente.getCodigo(),
						volumes, seqMovimentacaoProntuario);
		
		if (!listaMovimentosRequeridos.isEmpty()){
			for (AipMovimentacaoProntuarios movimentacao: listaMovimentosRequeridos){
				SimpleDateFormat formatador = new SimpleDateFormat("dd/MM HH:mm",  new Locale("pt", "BR"));
				final String strDataMovimento = formatador.format(dataMovimento);		
				StringBuffer observacoes = new StringBuffer();
				
				observacoes.append("Local: ")
				.append(local)
				.append(' ')
				.append(strDataMovimento)
				.append(" Situação: ")
				.append(situacao);
				
				//Obtém o objeto antigo
				AipMovimentacaoProntuarios movimentacaoOld = replicarMovimentacaoProntuarios(movimentacao);
				movimentacao.setObservacoes(observacoes.toString());
				this.atualizarMovimentacaoProntuario(movimentacao, movimentacaoOld, servidorLogado);
			}
		}
	}

	/**
	 * Replica o objeto AipMovimentacaoProntuarios
	 * @param movimentacaoOriginal
	 * @return
	 */
	public AipMovimentacaoProntuarios replicarMovimentacaoProntuarios(AipMovimentacaoProntuarios movimentacaoOriginal){
		
		AipMovimentacaoProntuarios movimentacaoOld = new AipMovimentacaoProntuarios();
		movimentacaoOld.setSeq(movimentacaoOriginal.getSeq());
		movimentacaoOld.setObservacoes(movimentacaoOriginal.getObservacoes());
		movimentacaoOld.setVolumes(movimentacaoOriginal.getVolumes());
		movimentacaoOld.setTipoEnvio(movimentacaoOriginal.getTipoEnvio());
		movimentacaoOld.setSituacao(movimentacaoOriginal.getSituacao());
		movimentacaoOld.setDataMovimento(movimentacaoOriginal.getDataMovimento());
		movimentacaoOld.setDataRetirada(movimentacaoOriginal.getDataRetirada());
		movimentacaoOld.setDataDevolucao(movimentacaoOriginal.getDataDevolucao());
		movimentacaoOld.setServidor(movimentacaoOriginal.getServidor());
		movimentacaoOld.setServidorRetirado(movimentacaoOriginal.getServidorRetirado());
		movimentacaoOld.setSolicitante(movimentacaoOriginal.getSolicitante());
		movimentacaoOld.setSolicitacao(movimentacaoOriginal.getSolicitacao());
		movimentacaoOld.setLocal(movimentacaoOriginal.getLocal());
		movimentacaoOld.setCriadoEm(movimentacaoOriginal.getCriadoEm());
		movimentacaoOld.setDataCadastroOrigemProntuario(movimentacaoOriginal.getDataCadastroOrigemProntuario());
		movimentacaoOld.setPaciente(movimentacaoOriginal.getPaciente());
		
		return movimentacaoOld;

	}
	
	protected IAmbulatorioFacade getAmbulatorioFacade() {
		return this.ambulatorioFacade;
	}
	
	protected AipSolicitantesProntuarioDAO getAipSolicitantesProntuarioDAO() {
		return aipSolicitantesProntuarioDAO;
	}
	
	protected AipMovimentacaoProntuarioDAO getAipMovimentacaoProntuarioDAO() {
		return aipMovimentacaoProntuarioDAO;
	}
		
	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return this.registroColaboradorFacade;
	}

	protected MovimentacaoProntuarioJournalON getMovimentacaoProntuarioJournalON() {
		return movimentacaoProntuarioJournalON;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
	public void gerarPendenciasSolicitacaoArquivoClinico() {
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		boolean isPerfilArquivoClinico = false;
		String usuario = servidorLogado != null ? servidorLogado.getUsuario() : null;

		removerPendencias();
		
		String perfilArquivoClinico;
		try {
			perfilArquivoClinico = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_PERFIL_ARQUIVO_CLINICO).getVlrTexto();
		} catch (ApplicationBusinessException e) {
			LOG.error(null,e);
			return;
		}

		Set<String> set = cascaFacade.obterNomePerfisPorUsuario(usuario);

		for (String perfilUsuario : set) {
			if (perfilUsuario.equalsIgnoreCase(perfilArquivoClinico)) {
				isPerfilArquivoClinico = true;
			}
		}
		
		if (isPerfilArquivoClinico){
			List<AipMovimentacaoProntuarios> listaMovimentosRetirados = getAipMovimentacaoProntuarioDAO().
					pesquisarMovimentacaoPontuariosPendentes();
			
			if (!listaMovimentosRetirados.isEmpty()){
				for (AipMovimentacaoProntuarios movimentacao: listaMovimentosRetirados){
					if(movimentacao.getSolicitante() != null && movimentacao.getSolicitante().isMensagemSamisCheckBox()){
						StringBuilder mensagem = new StringBuilder(55);
						mensagem.append(SOLICITAÇÃO_DO_PRONTUARIO)
						.append(movimentacao.getPaciente().getProntuarioFormatado())
						.append(" para desarquivamento, Solicitante: ")
						.append(movimentacao.getLocal())
						.append(", Data: ")
						.append(DateUtil.obterDataFormatada(movimentacao.getDataMovimento(), "dd/MM/yyyy HH:mm"));
						try {
							centralPendenciaFacade.adicionarPendenciaAcao(mensagem.toString(),
									"/paciente/prontuario/movimentaProntuario.xhtml?pacCodigo=" + movimentacao.getPaciente().getCodigo(), "Solicitações de Prontuário",
									servidorLogado, false);
						} catch (ApplicationBusinessException e) {
							logError(e);
						}
					}
				}
			}
		}
		
	}

	private void removerPendencias() {
		try {
			String pendencia = SOLICITAÇÃO_DO_PRONTUARIO;
			for (PendenciaVO pendenciaVO : centralPendenciaFacade.getListaPendencias()) {
				if (pendenciaVO.getMensagem().startsWith(pendencia)) {
					centralPendenciaFacade.excluirPendencia(pendenciaVO.getSeqCaixaPostal());
				}
			}
		} catch (ApplicationBusinessException e) {
			LOG.error(null, e);
		}
	}

}
