package br.gov.mec.aghu.ambulatorio.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.dao.AacConsultasDAO;
import br.gov.mec.aghu.ambulatorio.vo.RelatorioControleFrequenciaVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.AghParametros;

@Stateless
public class RelatorioControleFrequenciaRN extends BaseBusiness{


	/**
	 * 
	 */
	private static final long serialVersionUID = -6194586713198193379L;

	@Inject
	private AacConsultasDAO aacConsultasDAO;
	
	@EJB
	private IFaturamentoFacade faturamentoFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	private static final Log LOG = LogFactory.getLog(RelatorioControleFrequenciaRN.class);

	@Override
	protected Log getLogger() {
		return LOG;
	}

	public List<RelatorioControleFrequenciaVO> pesquisarControleFrequencia() {
		//parâmetros: lista de pacientes selecionados, nroProntuario, nroConsulta
		
		
		// TODO Auto-generated method stub
		//chamar todas as consultas e montar somente 1
		return null;
	}
	
	/**
	 *ORADB: CF_TTR_CODIGOFORMULA
	 * #42801
	 * 
	 * @param Integer nroProntuario
	 * @return String codigoProcedimento || prefixoTransplante
	 */
	public String obterCodigoFormulaPaciente(Integer nroProntuario){
		String codigo = null, nomeTransplante = "TRANSPLANTE DE ";
		
		codigo = faturamentoFacade.pesquisarCodigoFormulaPaciente(nroProntuario);
		
		if (codigo != null) {
			return nomeTransplante + codigo;
		} else {
			return null;
		}
	}
	
	/**
	 * ORADB: CF_DATA_REFFORMULA
	 * #42801 
	 * 
	 * @return String mesAnoReferencia
	 */
	public String obterMesAnoAtual(Integer nroConsulta){
		AacConsultas aacConsultas= aacConsultasDAO.obterConsulta(nroConsulta);
		return (DateUtil.obterDataFormatada(aacConsultas.getDtConsulta(), "MMMM'/'YYYY")).toUpperCase();
	}
	/**
	 *ORADB: CF_1FORMULA
	 *#42801
	 * 
	 * @param Integer nroConsulta
	 * @return String dataLocal
	 * @throws ApplicationBusinessException 
	 */
	public String obterDataLocalFormula(Integer nroConsulta) throws ApplicationBusinessException{
		AacConsultas aacConsultas= aacConsultasDAO.obterConsulta(nroConsulta);
		String cidadeAssinatura = obtemCidadeAssinaturaLaudoAPACAmbulatorio();
		return cidadeAssinatura .concat(DateUtil.obterDataFormatada(aacConsultas.getDtConsulta(), " dd ' de ' MMMM ' de ' YYYY"));
	}

	/**
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public String obtemCidadeAssinaturaLaudoAPACAmbulatorio()  throws ApplicationBusinessException {
		AghParametros cidadeAssinatura = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_CIDADE_ASSINATURA_LAUDO_APAC_AMBULATORIO);
		if (cidadeAssinatura != null && cidadeAssinatura.getVlrTexto() != null){
			return cidadeAssinatura.getVlrTexto();
		}
		return "";	
	}
}
