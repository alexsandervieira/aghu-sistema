package br.gov.mec.aghu.business.prescricaoenfermagem;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.business.ISchedulerFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.locator.ServiceLocator;
import br.gov.mec.aghu.core.utils.DateFormatUtil;
import br.gov.mec.aghu.dominio.DominioSituacaoJobDetail;
import br.gov.mec.aghu.dominio.DominioTipoEmissaoSumario;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghJobDetail;
import br.gov.mec.aghu.model.AghParametros;

@Stateless
public class ManterSumarioSchedulerRN extends BaseBusiness{

private static final Log LOG = LogFactory.getLog(ManterSumarioSchedulerRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private IParametroFacade parametroFacade;

@EJB
private IAghuFacade aghuFacade;

@EJB
private ISchedulerFacade schedulerFacade;
		
	private static final long serialVersionUID = -4684036820515323001L;

	/**
	 * Método que gera dados para sumário de prescrição enfermagem
	 * @param cron
	 * @param dataInicio
	 * @param dataFim
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException
	 */
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public void gerarDadosSumarioPrescricaoEnfermagem(String cron, Date dataInicio, Date dataFim ,String nomeProcessoQuartz) throws ApplicationBusinessException {
		AghJobDetail job = null;
		job = obterProcessoQuartz(nomeProcessoQuartz, job);
		
		dataInicio = validarDataInicio(dataInicio);
		dataFim = validarDataFim(dataFim);

		List<String> emailParaList = new ArrayList<String>();
		obtemValidaParametroEmail(emailParaList);
		
		AghParametros enfermagemAtivo = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_AGHU_ENFERMAGEM_ATIVO);
		
		if(enfermagemAtivo.getVlrNumerico().equals(BigDecimal.ONE)){
		
			apresentaLogDtInicioDtFim(dataInicio, dataFim, nomeProcessoQuartz, job);
			
			List<AghAtendimentos> atendimentos = getAghuFacade().buscaAtendimentosSumarioPrescricaoEnfermacao(dataInicio, dataFim);
			
			List<AghAtendimentos> atendimentosAbortados = new ArrayList<>();
			
			apresentaLogAtendimentosAProcessar(nomeProcessoQuartz, job, atendimentos);
			
			processaAtendimentos(job, atendimentos, atendimentosAbortados);
			
			apresentaMsgSucesso(nomeProcessoQuartz, job);
			
			listarAtendimentosAbortados(nomeProcessoQuartz, job, atendimentosAbortados);			
			}
		}
	private void processaAtendimentos(AghJobDetail job, List<AghAtendimentos> atendimentos,
			List<AghAtendimentos> atendimentosAbortados) {
		for (AghAtendimentos atendimento : atendimentos){
			try {
				IPrescricaoEnfermagemFacade ejbPrescricaoEnfermagem = ServiceLocator.getBean(IPrescricaoEnfermagemFacade.class, "aghu-prescricaoenfermagem");
				// controle transacional por EJB e requires_new				
				ejbPrescricaoEnfermagem.geraDadosSumarioPrescricaoEnfermagem(atendimento.getSeq(), DominioTipoEmissaoSumario.I);
			}catch (Exception ex) {
				LOG.error(ex.getMessage(), ex);
				atendimentosAbortados.add(atendimento);
				
				apresentaLogError(job, ex);
			}
		}
	}
	private void apresentaLogError(AghJobDetail job, Exception ex) {
		if (job != null) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			PrintStream ps = new PrintStream(baos);
			ex.printStackTrace(ps);
			getSchedulerFacade().adicionarLog(job
					, ex.getMessage() + "\n" + baos.toString()
			);
		}
	}
//		/this.getAghuFacade().enviaEmail(emailDe.getVlrTexto(), emailParaList, null, "Geração dos dados do sumário de prescrição enfermagem", conteudoEmail);


	private void apresentaMsgSucesso(String nomeProcessoQuartz, AghJobDetail job) {
		if (job != null) {
			String mensagem = "Quartz Task de "+nomeProcessoQuartz+" - finalizou com: SUCESSO";
			getSchedulerFacade().finalizarExecucao(job, DominioSituacaoJobDetail.C, mensagem);
			
		}
	}


	private void apresentaLogAtendimentosAProcessar(String nomeProcessoQuartz, AghJobDetail job,
			List<AghAtendimentos> atendimentos) {
		if (job != null) {
			getSchedulerFacade().adicionarLog(job, "Tarefa: "+nomeProcessoQuartz+" - total de atendimentos: " + atendimentos.size());
			if(atendimentos != null && atendimentos.size() > 0){					
				getSchedulerFacade().adicionarLog(job, "Tarefa: "+nomeProcessoQuartz+" - atendimentos: " + atendimentos);
			}
		}
	}


	private void apresentaLogDtInicioDtFim(Date dataInicio, Date dataFim, String nomeProcessoQuartz, AghJobDetail job) {
		if (job != null) {
			getSchedulerFacade().adicionarLog(job, "Tarefa: "+nomeProcessoQuartz
					+" - Dt ini: " + DateFormatUtil.formataTimeStamp(dataInicio) 
					+ " Dt Fim: " + DateFormatUtil.formataTimeStamp(dataFim)
			);
		}
	}


	private void obtemValidaParametroEmail(List<String> emailParaList) throws ApplicationBusinessException {
		AghParametros emailGeraSumario = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_AGHU_EMAIL_GERA_SUMARIO);
		
		if (emailGeraSumario != null && emailGeraSumario.getVlrTexto() != null) {
			StringTokenizer emailPara =  new StringTokenizer(this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_AGHU_EMAIL_GERA_SUMARIO).getVlrTexto(),";");
	
			while (emailPara.hasMoreTokens()) {
				emailParaList.add(emailPara.nextToken().trim().toLowerCase());
			}
		}
	}


	private Date validarDataFim(Date dataFim) throws ApplicationBusinessException {
		if (dataFim == null) {
			dataFim = getDataFim(getDiasAtras());
		}
		return dataFim;
	}


	private Date validarDataInicio(Date dataInicio) throws ApplicationBusinessException {
		if (dataInicio == null) {
			dataInicio = getDataInicio(getDiasAtras()+(getDiferencaDiasAtras() <= 0 ? 0 :getDiferencaDiasAtras()));
		}
		return dataInicio;
	}


	private AghJobDetail obterProcessoQuartz(String nomeProcessoQuartz, AghJobDetail job) {
		if (nomeProcessoQuartz != null) {
			job = getSchedulerFacade().obterAghJobDetailPorNome(nomeProcessoQuartz);
			if (job != null) {
				boolean iniciouExecucao = getSchedulerFacade().iniciarExecucao(job);
				if (!iniciouExecucao) {
					LOG.info("gerarDadosSumarioPrescricaoEnfermagem nao conseguiu iniciar execucao do jobDetail!");
				}
			}
		}
		return job;
	}


	private void listarAtendimentosAbortados(String nomeProcessoQuartz, AghJobDetail job, List<AghAtendimentos> atendimentosAbortados) {
	    if (job != null) {
			if(atendimentosAbortados != null && atendimentosAbortados.size() > 0){				
				StringBuilder mensagemAbortados = new StringBuilder(30);
				mensagemAbortados.append("Quartz Task de ").append(nomeProcessoQuartz).append(" - Atendimentos abortados: ");
				for(AghAtendimentos atend : atendimentosAbortados){
					mensagemAbortados.append("Atendimentos: seq =").append(atend.getSeq());
				}
				getSchedulerFacade().adicionarLog(job,mensagemAbortados.toString());
			}
	    }
	}
	
	
	private Date getDataFim(Integer diasAtras) {
		Calendar dataFim = Calendar.getInstance();
		dataFim.add(Calendar.DATE, - diasAtras);
		dataFim.set(Calendar.HOUR_OF_DAY, 23);
		dataFim.set(Calendar.MINUTE, 59);
		dataFim.set(Calendar.SECOND, 59);
		dataFim.set(Calendar.MILLISECOND, 999);
		
		return dataFim.getTime();
	}

	private Date getDataInicio(Integer diasAtras) {
		Calendar dataInicio = Calendar.getInstance();
		dataInicio.add(Calendar.DATE, - diasAtras);
		dataInicio.set(Calendar.HOUR_OF_DAY, 0);
		dataInicio.set(Calendar.MINUTE, 0);
		dataInicio.set(Calendar.SECOND, 0);
		dataInicio.set(Calendar.MILLISECOND, 0);
		
		return dataInicio.getTime();
	}

	private Integer getDiasAtras() throws ApplicationBusinessException {
		return getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_AGHU_DIAS_ATRAS_GERA_DADOS_SUMARIO_ALTA).getVlrNumerico().intValue();
	}
	
	private Integer getDiferencaDiasAtras() throws ApplicationBusinessException {
		return getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_AGHU_DIFERENCA_DIAS_ATRAS_GERA_DADOS_SUMARIO_ALTA).getVlrNumerico().intValue();
	}
	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	protected ISchedulerFacade getSchedulerFacade() {
		return schedulerFacade;
	}
	
}
