package br.gov.mec.aghu.business.jobs.manual;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.jobs.AghuJob;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.locator.ServiceLocator;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.dominio.DominioOpcaoEncerramentoAmbulatorio;
import br.gov.mec.aghu.dominio.DominioSituacaoJobDetail;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.AghJobDetail;

public class ProgramarEncerramentoJob extends AghuJob {
	
	public static final String MODULO = "MODULO";
	public static final String PREVIA = "PREVIA";
	public static final String CPE_DT_FIM = "CPE_DT_FIM";
	public static final String NOME_MICROCOMPUTADOR = "NOME_MICROCOMPUTADOR";
	
	private static final Log LOG = LogFactory.getLog(ProgramarEncerramentoJob.class);
	
	private IFaturamentoFacade faturamentoFacade = ServiceLocator.getBean(IFaturamentoFacade.class, "aghu-faturamento");
	private IParametroFacade parametroFacade = ServiceLocator.getBean(IParametroFacade.class, "aghu-configuracao");
	
	
	@Override
	protected void doExecutarProcessoNegocio(AghJobDetail job, JobExecutionContext jobExecutionContext) throws ApplicationBusinessException {
		final String QUEBRA_LINHA = "\n"; 
		final String STR_BR = "<br />";
		
		String remetente = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_EMAIL_ENVIO).getVlrTexto();
		final String destinatariosParam = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_EMAIL_FATURAMENTO_AMBULATORIO).getVlrTexto(); 

		
		if(destinatariosParam == null || remetente == null) {
			LOG.error("Não foi enviado e-mails pois o servidor esta desconfigurado. Verifiqe os parametros P_AGHU_EMAIL_ENVIO"
					+ "	e P_EMAIL_FATURAMENTO_AMBULATORIO. Também verifique atributo mail_session_host do app-parameters.properties");
			getSchedulerFacade().adicionarLog(job, "Não foi enviado e-mails pois o servidor esta desconfigurado.");
		}
		
		List<String> destinatarios = null;
		if(destinatariosParam != null) {
			destinatarios = Arrays.asList(destinatariosParam.split(";"));
		}
		
		LOG.info("Quartz Task de Encerramento/Prévia do Ambulatório: " + new Date() + " [" + job.getNomeProcesso() + "]");
		
		DominioSituacaoJobDetail situacao = null;
		String log = null;
		String fim = null;
		
		JobDataMap map = jobExecutionContext.getJobDetail().getJobDataMap();
		DominioOpcaoEncerramentoAmbulatorio modulo = DominioOpcaoEncerramentoAmbulatorio.getInstance((String)map.get(ProgramarEncerramentoJob.MODULO));
		Boolean previa = (Boolean) map.get(ProgramarEncerramentoJob.PREVIA);
		Date cpeDtFim = (Date) map.get(ProgramarEncerramentoJob.CPE_DT_FIM);
		String nomeMicrocomputador = (String) map.get(ProgramarEncerramentoJob.NOME_MICROCOMPUTADOR);
		
		try {
			final Date dataFimVinculoServidor = new Date();
			faturamentoFacade.rnFatpExecFatNew(modulo, previa, cpeDtFim, job, nomeMicrocomputador, dataFimVinculoServidor);
			LOG.info("metodo: faturamentoFacade.rnFatpExecFatNew(modulo, previa, cpeDtFim)");
			
			fim = "..............FIM FATURAMENTO AMBULATORIO " + DateUtil.obterDataFormatadaHoraMinutoSegundo(dataFimVinculoServidor)+" ..............";
			situacao = DominioSituacaoJobDetail.C;
		} catch (Exception e) {
			LOG.error(e.getMessage());
			
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			String stringException = sw.toString();								
			
			log = QUEBRA_LINHA + QUEBRA_LINHA + "##### AGHU - Exception - erro ao tentar executar faturamento do ambulatório " + DateUtil.obterDataFormatadaHoraMinutoSegundo(new Date()) + QUEBRA_LINHA + QUEBRA_LINHA
				   + stringException;			
			
			situacao = DominioSituacaoJobDetail.F;			
		}

		try {
			if(destinatarios != null) {
				if (DominioSituacaoJobDetail.F.equals(situacao)) { //se falhou
					faturamentoFacade.enviaEmailResultadoEncerramentoAmbulatorio("ERRO Executando: " + (previa ? " Prévia " : " Encerramento ")  + STR_BR + STR_BR + log, remetente, destinatarios);
				}
				else { //se houve sucesso
					faturamentoFacade.enviaEmailResultadoEncerramentoAmbulatorio(fim + STR_BR + " Executou: " + (previa ? " Prévia " : " Encerramento "), remetente, destinatarios);
				}
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
		}
		
	}
	
}
