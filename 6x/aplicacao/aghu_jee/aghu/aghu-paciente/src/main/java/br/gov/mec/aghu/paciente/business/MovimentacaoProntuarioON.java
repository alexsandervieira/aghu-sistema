package br.gov.mec.aghu.paciente.business;

import static br.gov.mec.aghu.model.AipPacientes.VALOR_MAXIMO_PRONTUARIO;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.ambulatorio.dao.AacConsultasDAO;
import br.gov.mec.aghu.ambulatorio.dao.AacPeriodoReferenciaDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcCirurgiasDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.dominio.DominioSituacaoMovimentoProntuario;
import br.gov.mec.aghu.dominio.DominioTipoSolicitacaoProntuario;
import br.gov.mec.aghu.internacao.dao.AinInternacaoDAO;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.AacPeriodoReferencia;
import br.gov.mec.aghu.model.AghSamis;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.AipMovimentacaoProntuarioJn;
import br.gov.mec.aghu.model.AipMovimentacaoProntuarios;
import br.gov.mec.aghu.model.AipPacienteProntuario;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.AipSolicitantesProntuario;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.business.exception.PacienteExceptionCode;
import br.gov.mec.aghu.paciente.cadastrosbasicos.business.ICadastrosBasicosPacienteFacade;
import br.gov.mec.aghu.paciente.dao.AipMovimentacaoProntuarioDAO;
import br.gov.mec.aghu.paciente.dao.AipMovimentacaoProntuarioJnDAO;
import br.gov.mec.aghu.paciente.dao.AipPacienteProntuarioDAO;
import br.gov.mec.aghu.paciente.dao.AipPacientesDAO;
import br.gov.mec.aghu.paciente.dao.AipSolicitantesProntuarioDAO;
import br.gov.mec.aghu.paciente.vo.AipMovimentacaoProntuariosVO;

@Stateless
public class MovimentacaoProntuarioON extends BaseBusiness{

	private static final Log LOG = LogFactory.getLog(MovimentacaoProntuarioON.class);
	@Inject
	private AipMovimentacaoProntuarioJnDAO aipMovimentacaoProntuarioJnDAO;
	@EJB
	private ICadastrosBasicosPacienteFacade adastrosBasicosPacienteFacade;
	@Inject
	private AipPacienteProntuarioDAO aipPacienteProntuarioDAO;
	@Inject
	private AipMovimentacaoProntuarioDAO aipMovimentacaoProntuarioDAO;
	@Inject
	private AipSolicitantesProntuarioDAO aipSolicitantesProntuarioDAO;
	@Inject
	private AipPacientesDAO aipPacientesDAO;
	@EJB
	private IAmbulatorioFacade ambulatorioFacade;
	@EJB
	private IPacienteFacade pacienteFacade;
	@EJB
	private MovimentacaoProntuarioRN movimentacaoProntuarioRN;
	@Inject
	private AacConsultasDAO aacConsultasDAO;
	@Inject
	private AinInternacaoDAO ainInternacaoDAO;
	@Inject
	private MbcCirurgiasDAO mbcCirurgiasDAO;
	@Inject
	private AacPeriodoReferenciaDAO aacPeriodoReferenciaDAO;
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	private enum MovimentacaoProntuarioONCode implements BusinessExceptionCode {
		ERRO_ORIGEM_NAO_DEFINIDA
		, MSG_WARNING_SOLICITANTE_INEXISTENTE_ORIGEM_INTERNACAO
		, ERRO_PRONTUARIO_VIRTUAL_SAMIS
		, PACIENTE_SEM_PRONTUARIO_SAMIS
		, ERRO_CONSULTA_DIA_OU_DIA_SEGUINTE
		, MSG_WARNING_SOLICITANTE_INEXISTENTE_ORIGEM_CIRURGIA
		, MSG_WARNING_SOLICITANTE_INEXISTENTE_ORIGEM_CONSULTA
		, ERRO_CONSULTA_DIA_APOS_FINALIZAR_DIARIA
		, MSG_WARNING_SOLICITANTE_INEXISTENTE_ORIGEM_DIARIA;
	}
	
	private static final long serialVersionUID = -5407736502420434915L;
	
	public List<AipPacientes> pesquisaPacientesMovimentacaoProntuario(
			Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc, Integer codigoPaciente, Integer prontuario, String nomePesquisaPaciente) {
		return getAipMovimentacaoProntuarioDAO().pesquisa(firstResult, maxResults,
				orderProperty, asc, codigoPaciente, prontuario, nomePesquisaPaciente);
	}

	public List<AipSolicitantesProntuario> pesquisarUnidadesSolicitantesPorCodigoOuSigla(
			final Object objPesquisa) {
		
		final String strPesquisa = (String) objPesquisa;

		if (StringUtils.isNotBlank(strPesquisa) && CoreUtil.isNumeroInteger(strPesquisa)) {
			final List<AipSolicitantesProntuario> list = this.getAipSolicitantesProntuariosDAO().pesquisarUnidadeSolicitantePorCodigoESigla(
					Short.valueOf(strPesquisa), null);

			if (list.size() > 0) {
				return list;
			}
		} else {
			final List<AipSolicitantesProntuario> list = this.getAipSolicitantesProntuariosDAO().pesquisarUnidadeSolicitantePorCodigoESigla(null, strPesquisa);

			if (list.size() > 0) {
				return list;
			}
		}

		return null;
	}

	public void persistirVinculoMovimentacaoOrigemProntuario(
			List<AipMovimentacaoProntuariosVO> listaItensSelecionados, AghSamis origemProntuario,
			RapServidores servidorLogado) throws ApplicationBusinessException{
		if (origemProntuario == null) {
			throw new ApplicationBusinessException(MovimentacaoProntuarioONCode.ERRO_ORIGEM_NAO_DEFINIDA);
		}
		for (AipMovimentacaoProntuariosVO itemSelecionado : listaItensSelecionados) {
			validaSeProntuarioArquivado(itemSelecionado);
			popularAipMovimentacaoProntuarioJN(servidorLogado, itemSelecionado);
			alterarVinculoOrigemProntuario(origemProntuario, itemSelecionado);
		}
	}

	private void alterarVinculoOrigemProntuario(AghSamis origemProntuario,
			AipMovimentacaoProntuariosVO itemSelecionado)
			throws ApplicationBusinessException {
		AipMovimentacaoProntuarios aipMovimentacaoProntuarios = getAipMovimentacaoProntuarioDAO().listarMovimentacoesProntuariosPorCodigoPaciente(itemSelecionado.getPacCodigo()).get(0);
		if(aipMovimentacaoProntuarios.getLocalAtual() == null || (aipMovimentacaoProntuarios.getSamisOrigem() != null) && (aipMovimentacaoProntuarios.getLocalAtual().equals(aipMovimentacaoProntuarios.getSamisOrigem().getDescricao()))){
			aipMovimentacaoProntuarios.setLocalAtual(origemProntuario.getDescricao());
		}
		aipMovimentacaoProntuarios.setSamisOrigem(origemProntuario);
		this.alterarMovimentacao(aipMovimentacaoProntuarios);
		
		AipPacienteProntuario aipPacienteProntuario = getAipPacienteProntuarioDAO().obterPorChavePrimaria(itemSelecionado.getProntuario());
		aipPacienteProntuario.setSamis(origemProntuario);
		this.alterarPacienteProntuario(aipPacienteProntuario);
	}

	private void alterarPacienteProntuario(AipPacienteProntuario aipPacienteProntuario) throws ApplicationBusinessException {
		this.getAipPacienteProntuarioDAO().atualizar(aipPacienteProntuario);
		
	}

	private void validaSeProntuarioArquivado(
			AipMovimentacaoProntuariosVO itemSelecionado)
			throws ApplicationBusinessException {
		if( (itemSelecionado.getOrigemProntuario() != null) && (itemSelecionado.getLocalAtual() != null) && ! ( itemSelecionado.getOrigemProntuario().equals(itemSelecionado.getLocalAtual() ) )){
			throw new ApplicationBusinessException(PacienteExceptionCode.MENSAGEM_PRONTUARIO_NAO_ARQUIVADO);
		}
	}

	private void popularAipMovimentacaoProntuarioJN(RapServidores servidorLogado, AipMovimentacaoProntuariosVO itemSelecionado) 
			throws ApplicationBusinessException {
		AipMovimentacaoProntuarioJn aipMovimentacaoProntuarioJn = new AipMovimentacaoProntuarioJn();
		if(itemSelecionado.getCodigoSolicitante() == null){
			throw new ApplicationBusinessException(PacienteExceptionCode.MENSAGEM_COD_SOLICITANTE_PRONTUARIO_OBRIGATORIO);
		}
		AipSolicitantesProntuario solicitanteProntuario = getCadastrosBasicosPacienteFacade().obterSolicitanteProntuario(itemSelecionado.getCodigoSolicitante());
		aipMovimentacaoProntuarioJn.setSeq(itemSelecionado.getSeq());
		aipMovimentacaoProntuarioJn.setObservacoes(itemSelecionado.getObservacoes());
		aipMovimentacaoProntuarioJn.setVolumes(itemSelecionado.getVolumes());
		aipMovimentacaoProntuarioJn.setTipoEnvio(itemSelecionado.getTipoEnvio());
		aipMovimentacaoProntuarioJn.setSituacao(itemSelecionado.getSituacao());
		aipMovimentacaoProntuarioJn.setDataMovimento(itemSelecionado.getDataMovimentacao());
		aipMovimentacaoProntuarioJn.setDataRetirada(itemSelecionado.getDataRetirada());
		aipMovimentacaoProntuarioJn.setDataDevolucao(itemSelecionado.getDataDevolucao());
		aipMovimentacaoProntuarioJn.setPacCodigo(itemSelecionado.getPacCodigo());
		aipMovimentacaoProntuarioJn.setSerMatricula(itemSelecionado.getServidorMatricula());
		aipMovimentacaoProntuarioJn.setSerVinCodigo(itemSelecionado.getSerVinCodigo());
		aipMovimentacaoProntuarioJn.setSerMatriculaRetirado(itemSelecionado.getSerMatriculaRetirado());
		aipMovimentacaoProntuarioJn.setSerVinCodigoRetirado(itemSelecionado.getSerVinCodigoRetirado());
		aipMovimentacaoProntuarioJn.setSopSeq(solicitanteProntuario.getSeq());
		aipMovimentacaoProntuarioJn.setSlpCodigo(itemSelecionado.getSlpCodigo());
		aipMovimentacaoProntuarioJn.setLocal(itemSelecionado.getLocalPrimeiraMovimentacao());
		aipMovimentacaoProntuarioJn.setLocalAtual(itemSelecionado.getLocalAtual());
		aipMovimentacaoProntuarioJn.setSamisSeq(itemSelecionado.getSeqOrigemProntuario());
		aipMovimentacaoProntuarioJn.setDataCadastroOrigem(itemSelecionado.getDataCadastroOrigemProntuario());
		aipMovimentacaoProntuarioJn.setNomeUsuario(servidorLogado.getUsuario());
		aipMovimentacaoProntuarioJn.setOperacao(DominioOperacoesJournal.UPD);
		this.persistirHistoricoMovimentacaoProntuarioJN(aipMovimentacaoProntuarioJn);
		
	}

	private void persistirHistoricoMovimentacaoProntuarioJN(AipMovimentacaoProntuarioJn aipMovimentacaoProntuarioJn){
		getAipMovimentacaoProntuarioJNDAO().persistir(aipMovimentacaoProntuarioJn);
	}

	private void alterarMovimentacao(AipMovimentacaoProntuarios aipMovimentacaoProntuarios) throws ApplicationBusinessException{
		this.getAipMovimentacaoProntuarioDAO().atualizar(aipMovimentacaoProntuarios);
	}

	public List<AipMovimentacaoProntuariosVO> pesquisaMovimentacoesDeProntuarios(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, AipPacientes paciente,
			AghSamis origemProntuariosPesquisa, AghUnidadesFuncionais unidadeSolicitantePesquisa, 
			DominioSituacaoMovimentoProntuario situacao, Date dataMovimentacaoConsulta) throws ApplicationBusinessException {
		List<AipMovimentacaoProntuariosVO> listaMovimetacoesProntuario = getAipMovimentacaoProntuarioDAO().
				pesquisaMovimentacoesDeProntuarios(firstResult, maxResult, 
				orderProperty, asc, paciente, origemProntuariosPesquisa, unidadeSolicitantePesquisa, 
				situacao, dataMovimentacaoConsulta);
		if ((listaMovimetacoesProntuario.isEmpty()) && (paciente != null)) {
			listaMovimetacoesProntuario = aipPacientesDAO.pesquisaMovimentacoesDeProntuariosPaciente(paciente);
		}
		return listaMovimetacoesProntuario;
	}

	public Long pesquisaMovimentacoesDeProntuariosCount(
			AipPacientes paciente, 
			AghSamis origemProntuariosPesquisa, AghUnidadesFuncionais unidadeSolicitantePesquisa, 
			DominioSituacaoMovimentoProntuario situacao, Date dataMovimentacaoConsulta) {
		Long valorCountPesquisa = getAipMovimentacaoProntuarioDAO().pesquisaMovimentacoesDeProntuariosCount(paciente, 
				origemProntuariosPesquisa, unidadeSolicitantePesquisa, situacao, dataMovimentacaoConsulta);
		if ((valorCountPesquisa == null || valorCountPesquisa == 0) && (paciente != null)) {
			valorCountPesquisa = aipPacientesDAO.pesquisaMovimentacoesDeProntuariosPacienteCount(paciente.getCodigo(), paciente.getProntuario(), 
					paciente.getNome());
		}
		return valorCountPesquisa; 
	}
	
	public List<AipMovimentacaoProntuariosVO> pesquisarTodasMovimentacoesParaSelecionarTodas(
			AipPacientes paciente, 
			AghSamis origemProntuariosPesquisa, AghUnidadesFuncionais unidadeSolicitantePesquisa, 
			DominioSituacaoMovimentoProntuario situacao, Date dataMovimentacaoConsulta) throws ApplicationBusinessException {
		return getAipMovimentacaoProntuarioDAO().pesquisarTodasMovimentacoesParaSelecionarTodas(paciente, 
				origemProntuariosPesquisa, unidadeSolicitantePesquisa, situacao, dataMovimentacaoConsulta);
	}

	public void persistirVinculoMovimentacaoUnidadeSolicitante(
			List<AipMovimentacaoProntuariosVO> listaItensSelecionados,
			AghUnidadesFuncionais unidadeSolicitanteAlteracao,
			RapServidores servidorLogado, DominioSituacaoMovimentoProntuario situacao) throws ApplicationBusinessException{
		
		validaMovimentoSimultaneoDeDoisProntuarioIguais(listaItensSelecionados);
		
		for (AipMovimentacaoProntuariosVO itemSelecionado : listaItensSelecionados) {
			
			validaSeProntuarioPossuiOrigem(itemSelecionado);
			validaSituacaoDoProntuario(itemSelecionado);
			
			popularAipMovimentacaoProntuarioJN(servidorLogado, itemSelecionado);
			
			AipMovimentacaoProntuarios aipMovimentacaoProntuarios = getAipMovimentacaoProntuarioDAO().listarMovimentacoesProntuariosPorCodigoPaciente(itemSelecionado.getPacCodigo()).get(0);
			aipMovimentacaoProntuarios.setLocalAtual(unidadeSolicitanteAlteracao.getDescricao());
			aipMovimentacaoProntuarios.setSituacao(situacao);
			this.alterarMovimentacao(aipMovimentacaoProntuarios);
		}
	}

	private void validaMovimentoSimultaneoDeDoisProntuarioIguais(
			List<AipMovimentacaoProntuariosVO> listaItensSelecionados)
			throws ApplicationBusinessException {
		for (AipMovimentacaoProntuariosVO itemSelecionado1 : listaItensSelecionados) {
			for (AipMovimentacaoProntuariosVO itemSelecionado2 : listaItensSelecionados) {
				if(itemSelecionado1.getPacCodigo().equals(itemSelecionado2.getPacCodigo()) && !(itemSelecionado1.getLocalPrimeiraMovimentacao().equals(itemSelecionado2.getLocalPrimeiraMovimentacao())) ){
					throw new ApplicationBusinessException(PacienteExceptionCode.MENSAGEM_MOV_PRONT_PARA_LOCAIS_DISTINTOS);
				}
			}
		}
	}

	private void validaSituacaoDoProntuario(
			AipMovimentacaoProntuariosVO itemSelecionado)
			throws ApplicationBusinessException {
		if( !(itemSelecionado.getSituacao().equals(DominioSituacaoMovimentoProntuario.R)) && !(itemSelecionado.getSituacao().equals(DominioSituacaoMovimentoProntuario.Q))   ){
			throw new ApplicationBusinessException(PacienteExceptionCode.MENSAGEM_SITUACAO_PRONTUARIO_NAO_PERMITE_MOVIMENTO);
		}
	}

	private void validaSeProntuarioPossuiOrigem(
			AipMovimentacaoProntuariosVO itemSelecionado)
			throws ApplicationBusinessException {
		if( itemSelecionado.getSeqOrigemProntuario() == null  ){
			throw new ApplicationBusinessException(PacienteExceptionCode.MENSAGEM_PRONTUARIO_SEM_ORIGEM);
		}
	}

	public void persistirDevolucaoDeProntuario(
			List<AipMovimentacaoProntuariosVO> listaItensSelecionados,
			RapServidores servidorLogado) throws ApplicationBusinessException{
		for (AipMovimentacaoProntuariosVO itemSelecionado : listaItensSelecionados) {
			if( itemSelecionado.getOrigemProntuario() == null || ( itemSelecionado.getOrigemProntuario().equals(itemSelecionado.getLocalAtual() ) )){
				throw new ApplicationBusinessException(PacienteExceptionCode.MENSAGEM_PRONTUARIO_NAO_PODE_SER_DEVOLVIDO);
			}
			popularAipMovimentacaoProntuarioJN(servidorLogado, itemSelecionado);
			
			AipMovimentacaoProntuarios aipMovimentacaoProntuarios = getAipMovimentacaoProntuarioDAO().listarMovimentacoesProntuariosPorCodigoPaciente(itemSelecionado.getPacCodigo()).get(0);
			aipMovimentacaoProntuarios.setLocalAtual(aipMovimentacaoProntuarios.getSamisOrigem().getDescricao());
			aipMovimentacaoProntuarios.setSituacao(DominioSituacaoMovimentoProntuario.D);
			aipMovimentacaoProntuarios.setDataDevolucao(new Date());
			this.alterarMovimentacao(aipMovimentacaoProntuarios);
		}
	}
	
	
	public List<AipSolicitantesProntuario> verificaLocalParaMovimentacao(
			List<AipMovimentacaoProntuariosVO> listaItensSelecionados,
			 Object param) throws ApplicationBusinessException {
		
		if(listaItensSelecionados != null && listaItensSelecionados.size() > 0){
		    AipMovimentacaoProntuariosVO itemSelecionado = listaItensSelecionados.get(0);    
		    
			if((itemSelecionado.getOrigemProntuario() == null) 
			    ||(   itemSelecionado.getLocalAtual().equals(itemSelecionado.getOrigemProntuario()) 
			       && itemSelecionado.getSituacao().getDescricao().equals(DominioSituacaoMovimentoProntuario.Q.getDescricao()))){					
					   
				        String localPrimeiraMovimentacaoConcatenada = itemSelecionado.getLocalPrimeiraMovimentacao();
					    Object localPrimeiraMovimentacao = localPrimeiraMovimentacaoConcatenada.substring(localPrimeiraMovimentacaoConcatenada.indexOf('/')+1, localPrimeiraMovimentacaoConcatenada.lastIndexOf('/'));
					    return pesquisarUnidadesSolicitantesPorCodigoOuSigla(localPrimeiraMovimentacao);
			}else{
				        return pesquisarUnidadesSolicitantesPorCodigoOuSigla(param);
			}
		}
		return null;
	}

	public void requererProntuariosCirurgia(MbcCirurgias cirurgia) throws ApplicationBusinessException {
		
		mbcCirurgiasDAO.refresh(cirurgia);
		AipSolicitantesProntuario solicitante =  getAipSolicitantesProntuariosDAO().pesquisarSolicitanteProntuarioPorUnidadeFuncional((cirurgia.getUnidadeFuncional().getSeq()));
		
		if (solicitante != null){
			validaSePacientePossuiProntuario(cirurgia.getPaciente());
			validaSePacientePossuiProntuarioVirtual(cirurgia.getPaciente());

			List<AipMovimentacaoProntuarios> listProntuariosMovimentados = this.getPacienteFacade().listarMovimentacoesProntuariosPorCodigoPaciente(cirurgia.getPaciente().getCodigo());
			String strLocal = cirurgia.getUnidadeFuncional().getDescricao() + "/" + cirurgia.getSalaCirurgica().getNome();
			if(listProntuariosMovimentados == null || listProntuariosMovimentados.isEmpty()){
				this.getMovimentacaoProntuarioRN().gerarMovimentacaoGeral(cirurgia.getPaciente(), solicitante, new Date(),
						strLocal, DominioSituacaoMovimentoProntuario.Q, null,
						cirurgia.getUnidadeFuncional(), DominioTipoSolicitacaoProntuario.C);
			}
			else{
				requererProntuariosAindaNaoRequeridos(cirurgia.getPaciente(), solicitante, new Date(), strLocal,
						listProntuariosMovimentados, DominioSituacaoMovimentoProntuario.Q,
						cirurgia.getUnidadeFuncional(), DominioTipoSolicitacaoProntuario.C);
			}
		} else {
			throw new ApplicationBusinessException(MovimentacaoProntuarioONCode.MSG_WARNING_SOLICITANTE_INEXISTENTE_ORIGEM_CIRURGIA);
		}
		
	}
	

	public void requererProntuarios(AinInternacao internacao) 
			throws ApplicationBusinessException {
		
		getAinInternacaoDAO().refresh(internacao);
		// validar se existe solicitante
		AipSolicitantesProntuario solicitante =  getAipSolicitantesProntuariosDAO().pesquisarSolicitantesProntuarioPorOrigemEventos(internacao.getOrigemEvento().getSeq());
		// Regra 3.2 da issue #84038
		if (solicitante != null){
			validaSePacientePossuiProntuario(internacao.getPaciente());
			validaSePacientePossuiProntuarioVirtual(internacao.getPaciente());

			List<AipMovimentacaoProntuarios> listProntuariosMovimentados = this.getPacienteFacade().listarMovimentacoesProntuariosPorCodigoPaciente(internacao.getPaciente().getCodigo());
			String strLocal = internacao.getOrigemEvento().getDescricao() + "/" + internacao.getAtendimento().getUnidadeFuncional().getSigla() + "/" + internacao.getAtendimento().getDescricaoLocalizacao(false);
			if(listProntuariosMovimentados == null || listProntuariosMovimentados.isEmpty()){
				this.getMovimentacaoProntuarioRN().gerarMovimentacaoGeral(internacao.getPaciente(), solicitante, new Date(),
						strLocal, DominioSituacaoMovimentoProntuario.Q, null,
						internacao.getAtendimento().getUnidadeFuncional(), DominioTipoSolicitacaoProntuario.I);
			}
			else{
				requererProntuariosAindaNaoRequeridos(internacao.getPaciente(), solicitante, new Date(), strLocal,
						listProntuariosMovimentados, DominioSituacaoMovimentoProntuario.Q,
						internacao.getAtendimento().getUnidadeFuncional(), DominioTipoSolicitacaoProntuario.I);
			}
		} else {
			throw new ApplicationBusinessException(MovimentacaoProntuarioONCode.MSG_WARNING_SOLICITANTE_INEXISTENTE_ORIGEM_INTERNACAO);
		}
	}

	public void requererProntuariosConsultas(AacConsultas consulta, Boolean exibeMsgSolicitanteInexistente) throws ApplicationBusinessException{

		getAacConsultasDAO().refresh(consulta);

		// validar se existe solicitante
		AipSolicitantesProntuario solicitante =  getAipSolicitantesProntuariosDAO().pesquisarSolicitanteProntuarioPorUnidadeFuncional(consulta.getGradeAgendamenConsulta().getUnidadeFuncional().getSeq());
		// Regra 3.2 da issue #84038
		if (solicitante != null){
			List<AacPeriodoReferencia> listaPeriodoReferencia = this.getAacPeriodoReferenciaDAO().pesquisarPeriodoReferencia();
			Date dataReferencia = getDataReferencia(listaPeriodoReferencia);
			Date dataConsultaInicio = getDataConsulta(consulta);
			Date dataReferenciaInicio = getDataInicioReferencia(dataReferencia);
			Date dataAtualInicio = DateUtil.truncaData(new Date());
			
			validaSePacientePossuiProntuario(consulta.getPaciente());
			validaSePacientePossuiProntuarioVirtual(consulta.getPaciente());
			validaConsultaDiaOuDiaSeguinte(exibeMsgSolicitanteInexistente, dataConsultaInicio, dataReferenciaInicio, dataAtualInicio);

			List<AipMovimentacaoProntuarios> listProntuariosMovimentados = this.getPacienteFacade().listarMovimentacoesProntuariosPorCodigoPaciente(consulta.getPaciente().getCodigo());

			String strLocal = getAmbulatorioFacade().defineTurno(consulta.getDtConsulta())
			+ "/" + consulta.getGradeAgendamenConsulta().getUnidadeFuncional().getSigla() + "/"
			+ consulta.getGradeAgendamenConsulta().getAacUnidFuncionalSala().getId().getSala()
			+ " " + consulta.getGradeAgendamenConsulta().getSeq() + " " + consulta.getNumero();

			if(listProntuariosMovimentados == null || listProntuariosMovimentados.isEmpty()){
				this.getMovimentacaoProntuarioRN().gerarMovimentacaoGeral(consulta.getPaciente(), solicitante, new Date(),
						strLocal, DominioSituacaoMovimentoProntuario.Q, null,
						consulta.getGradeAgendamenConsulta().getUnidadeFuncional(), DominioTipoSolicitacaoProntuario.A);
			}
			else{
				requererProntuariosAindaNaoRequeridos(consulta.getPaciente(), solicitante, new Date(), strLocal,
						listProntuariosMovimentados, DominioSituacaoMovimentoProntuario.Q,
						consulta.getGradeAgendamenConsulta().getUnidadeFuncional(), DominioTipoSolicitacaoProntuario.A);
			}
		} else if(exibeMsgSolicitanteInexistente){
			throw new ApplicationBusinessException(MovimentacaoProntuarioONCode.MSG_WARNING_SOLICITANTE_INEXISTENTE_ORIGEM_CONSULTA);
		} 
	}

	private void requererProntuariosAindaNaoRequeridos(AipPacientes paciente, AipSolicitantesProntuario solicitante, Date dtMovimento, String strLocal,
			List<AipMovimentacaoProntuarios> listProntuariosMovimentados, DominioSituacaoMovimentoProntuario dominioSituacaoProntuario, AghUnidadesFuncionais aghUnidadesFuncionais, DominioTipoSolicitacaoProntuario tipoSolicitacao) throws ApplicationBusinessException {
		String localAtual = null;
		Boolean prontuarioRetirado = Boolean.FALSE;
		for (AipMovimentacaoProntuarios aipMovimentacaoProntuarios : listProntuariosMovimentados) {
			if(StringUtils.isNotBlank(aipMovimentacaoProntuarios.getLocal())){
				//Caso o prontuário ja esteja solicitado pelo mesmo solicitante, não realiza nova solicitação mas exibe mensagem de solicitação com sucesso.
				if(aipMovimentacaoProntuarios.getLocal().equals(strLocal)
						&& (aipMovimentacaoProntuarios.getSituacao().equals(dominioSituacaoProntuario)
								|| aipMovimentacaoProntuarios.getSituacao().equals(DominioSituacaoMovimentoProntuario.N))){
					return;
				}// Regra 3.3 da issue #84038
				else if (!aipMovimentacaoProntuarios.getLocal().equals(aipMovimentacaoProntuarios.getLocalAtual()) && aipMovimentacaoProntuarios.getSituacao().equals(DominioSituacaoMovimentoProntuario.R)){
					prontuarioRetirado = Boolean.TRUE;
					localAtual = aipMovimentacaoProntuarios.getLocalAtual();
				} 
			}
		}
		if(prontuarioRetirado){
			// Não Localizado
			this.getMovimentacaoProntuarioRN().gerarMovimentacaoGeral(paciente, solicitante, dtMovimento, strLocal,
					DominioSituacaoMovimentoProntuario.N, localAtual, aghUnidadesFuncionais, tipoSolicitacao);
		} else {
			// Requerido
			this.getMovimentacaoProntuarioRN().gerarMovimentacaoGeral(paciente, solicitante, dtMovimento, strLocal,
					dominioSituacaoProntuario, null, aghUnidadesFuncionais, tipoSolicitacao);
		}
	}
	
//	private void validaRequerimentoProntuario(AipPacientes paciente,
//			Boolean exibeMsgProntuarioJaMovimentado, Date dataInicio,
//			Date dataReferenciaInicio, Date dataAtualInicio, Boolean blnConsulta)
//			throws ApplicationBusinessException {
//		validaSePacientePossuiProntuario(paciente);
//		validaSePacientePossuiProntuarioVirtual(paciente);
//		// É marcação de consulta
//		if (blnConsulta){
//			validaConsultaDiaOuDiaSeguinte(exibeMsgProntuarioJaMovimentado, dataInicio, dataReferenciaInicio, dataAtualInicio);
//		}
//	}
	
	private void validaSePacientePossuiProntuarioVirtual(AipPacientes paciente)
			throws ApplicationBusinessException {
		if(paciente!=null && paciente.getProntuario()!=null && paciente.getProntuario()>VALOR_MAXIMO_PRONTUARIO){
			throw new ApplicationBusinessException(MovimentacaoProntuarioONCode.ERRO_PRONTUARIO_VIRTUAL_SAMIS);
		}
	}

	private void validaSePacientePossuiProntuario(AipPacientes paciente)
			throws ApplicationBusinessException {
		if(paciente==null || paciente.getProntuario()==null){
			throw new ApplicationBusinessException(MovimentacaoProntuarioONCode.PACIENTE_SEM_PRONTUARIO_SAMIS);
		}
	}
	
	private void validaConsultaDiaOuDiaSeguinte(
			Boolean exibeMsgProntuarioJaMovimentado, Date dataConsultaInicio,
			Date dataReferenciaInicio, Date dataAtualInicio)
			throws ApplicationBusinessException {
		if(exibeMsgProntuarioJaMovimentado && DateUtil.validaDataMenor(dataConsultaInicio, dataAtualInicio)){
			throw new ApplicationBusinessException(MovimentacaoProntuarioONCode.ERRO_CONSULTA_DIA_OU_DIA_SEGUINTE);
		} else if (exibeMsgProntuarioJaMovimentado && DateUtil.validaDataMaior(dataConsultaInicio,dataReferenciaInicio)){
			throw new ApplicationBusinessException(MovimentacaoProntuarioONCode.ERRO_CONSULTA_DIA_APOS_FINALIZAR_DIARIA);
		}
	}
		
	private Date getDataInicioReferencia(Date dataReferencia) {
		Date dataReferenciaInicio = null;
		if(dataReferencia!=null){
			dataReferenciaInicio = DateUtil.truncaData(dataReferencia); 
		}
		return dataReferenciaInicio;
	}

	private Date getDataReferencia(List<AacPeriodoReferencia> listaPeriodoReferencia) {
		Date dataReferencia = null;
		if(listaPeriodoReferencia!=null && listaPeriodoReferencia.size()>0){
			AacPeriodoReferencia periodoReferencia = listaPeriodoReferencia.get(0);
			dataReferencia = periodoReferencia.getDtReferencia();
		}
		return dataReferencia;
	}
	
	private Date getDataConsulta(AacConsultas consulta) {
		Date dataConsulta = consulta.getDtConsulta();
		Date dataConsultaInicio = getDataInicioReferencia(dataConsulta);
		return dataConsultaInicio;
	}

	private AipSolicitantesProntuarioDAO getAipSolicitantesProntuariosDAO() {
		return aipSolicitantesProntuarioDAO;
	}
	
	private AipMovimentacaoProntuarioDAO getAipMovimentacaoProntuarioDAO(){
		return aipMovimentacaoProntuarioDAO;
	}
	
	private AipPacienteProntuarioDAO getAipPacienteProntuarioDAO(){
		return aipPacienteProntuarioDAO;
	}
	
	private ICadastrosBasicosPacienteFacade getCadastrosBasicosPacienteFacade() {
		return adastrosBasicosPacienteFacade;
	}
	
	private AipMovimentacaoProntuarioJnDAO getAipMovimentacaoProntuarioJNDAO(){
		return aipMovimentacaoProntuarioJnDAO;
	}

	protected IAmbulatorioFacade getAmbulatorioFacade() {
		return this.ambulatorioFacade;
	}

	public AacPeriodoReferenciaDAO getAacPeriodoReferenciaDAO() {
		return aacPeriodoReferenciaDAO;
	}

	public AacConsultasDAO getAacConsultasDAO() {
		return aacConsultasDAO;
	}

	public AinInternacaoDAO getAinInternacaoDAO() {
		return ainInternacaoDAO;
	}

	protected IPacienteFacade getPacienteFacade() {
		return pacienteFacade;
	}
	
	protected MovimentacaoProntuarioRN getMovimentacaoProntuarioRN() {
		return movimentacaoProntuarioRN;
	}

}
