package br.gov.mec.aghu.internacao.business;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.internacao.dao.AinMovimentoInternacaoDAO;
import br.gov.mec.aghu.model.AinMovimentosInternacao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;

@Stateless
public class MovimentoInternacaoRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(MovimentoInternacaoRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AinMovimentoInternacaoDAO ainMovimentoInternacaoDAO;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3115006777145911990L;

	public enum MovimentoInternacaoRNExceptionCode implements BusinessExceptionCode {
		RAP_00175;
	}
	
	/**
	 * ORADB AINT_MVI_BRI
	 * 
	 * @param movimentoInternacao
	 * @throws ApplicationBusinessException
	 */
	public void validarMovimentoInternacao(
			AinMovimentosInternacao movimentoInternacao) throws ApplicationBusinessException {
		
		if (movimentoInternacao.getDthrLancamento() == null) {
			movimentoInternacao.setDthrLancamento(new Date());
		}  else {
            List<AinMovimentosInternacao> listaMovimentosInternacao = this.getAinMovimentoInternacaoDAO().pesquisarPorInternacaoSeqOrderDtHrLanc(movimentoInternacao.getInternacao().getSeq());
			
			if (listaMovimentosInternacao != null) {
				
				ajustaDataHoraLancamento(movimentoInternacao, listaMovimentosInternacao);
			}
			
		}
		
		if (movimentoInternacao.getServidorGerado() == null) {
			RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
			
			if (servidorLogado != null) {
				movimentoInternacao.setServidorGerado(servidorLogado);
			} else {
				throw new ApplicationBusinessException(MovimentoInternacaoRNExceptionCode.RAP_00175);
			}
		}
	}
	private void ajustaDataHoraLancamento(AinMovimentosInternacao movimentoInternacao,
			List<AinMovimentosInternacao> listaMovimentosInternacao) {
		boolean ajustarNovamente = Boolean.FALSE;
		for(AinMovimentosInternacao auxMovimentoInternacao : listaMovimentosInternacao){		
			if (movimentoInternacao.getDthrLancamento().compareTo(auxMovimentoInternacao.getDthrLancamento()) == 0) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(auxMovimentoInternacao.getDthrLancamento());
				cal.add(Calendar.MINUTE, 1);
				
				movimentoInternacao.setDthrLancamento(cal.getTime());
				ajustarNovamente = Boolean.TRUE;
				break;
			}
		}
		if(ajustarNovamente){			
			ajustaDataHoraLancamento(movimentoInternacao, listaMovimentosInternacao);
		}
		
	}
	protected AinMovimentoInternacaoDAO getAinMovimentoInternacaoDAO(){
		return ainMovimentoInternacaoDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
