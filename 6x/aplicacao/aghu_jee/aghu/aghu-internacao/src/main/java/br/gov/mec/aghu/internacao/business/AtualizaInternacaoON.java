package br.gov.mec.aghu.internacao.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.dominio.DominioTransacaoAltaPaciente;
import br.gov.mec.aghu.model.AinCidsInternacao;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.AinResponsaveisPaciente;

@Stateless
public class AtualizaInternacaoON extends BaseBusiness {

	private static final long serialVersionUID = -4681247280908209986L;
    
	private static final Log LOG = LogFactory.getLog(AtualizaInternacaoON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	private enum AtualizaInternacaoONExceptionCode implements BusinessExceptionCode {
    AIN_00706;

	    public void throwException(final Throwable cause, final Object... params) throws ApplicationBusinessException {
		    if (cause instanceof ApplicationBusinessException) {
		     	throw (ApplicationBusinessException) cause;
		    }
		    throw new ApplicationBusinessException(this, cause, params);
	     }

    }
	
	public DominioTransacaoAltaPaciente defineTransacao(final String oldTamCodigo, final String newTamCodigo,
            final AinInternacao internacaoOld,
            final AinInternacao internacao) throws ApplicationBusinessException {
				DominioTransacaoAltaPaciente transacao = null;
				try {
				transacao = validaSeEstornoAltaAlteracao(internacao.getDthrAltaMedica(),internacaoOld.getDtSaidaPaciente(),internacao.getDtSaidaPaciente(),oldTamCodigo,newTamCodigo,internacaoOld.getDthrAltaMedica(),transacao);			
				transacao = validaSeInternacaoAtualizada(internacaoOld.getTipoCaracterInternacao().getCodigo(), 
				             validaTipoCaracterInternacao(internacao),
				             validaOrigemEvento(internacaoOld),
				             validaOrigemEvento(internacao),
						     validaInstituicaoHospitalar(internacaoOld),
						     validaInstituicaoHospitalar(internacao),
							 internacaoOld.getMedicoExterno() ,internacao.getMedicoExterno() ,
							 internacaoOld.getDthrInternacao(),internacao.getDthrInternacao(), 
							 internacaoOld.getIndDifClasse(),internacao.getIndDifClasse(), 
							 internacaoOld.getEnvProntUnidInt(),internacao.getEnvProntUnidInt(), 
							 internacaoOld.getJustificativaAltDel(),internacao.getJustificativaAltDel(),
							 validaItemProcedimentoHospitalarSeq(internacaoOld),
				             validaItemProcedimentoHospitalarPhoSeq(internacaoOld),
				             validaItemProcedimentoHospitalarSeq(internacao),
				             validaItemProcedimentoHospitalarPhoSeq(internacao),
							 transacao);
				transacao = validaSeInternacaoAtualizadaCids(validaCidsInternacao(internacaoOld), 
				                     validaCidsInternacao(internacao),
				                     transacao);
				transacao = validaSeInternacaoAtualizadaResponsavel(internacaoOld.getResponsaveisPaciente() == null ? null :internacaoOld.getResponsaveisPaciente(), 
				                            internacao.getResponsaveisPaciente() == null ? null :internacao.getResponsaveisPaciente(), 
				                            transacao);
				} catch (final Exception e) {
				logError(e.getMessage(), e);
				AtualizaInternacaoONExceptionCode.AIN_00706.throwException(e);
				}
				return transacao;
     }
	private List<AinCidsInternacao> validaCidsInternacao(final AinInternacao internacao) {
		return (internacao.getCidsInternacao() == null || internacao.getCidsInternacao().isEmpty()) ? null :new ArrayList<>(internacao.getCidsInternacao());
	}

	private Short validaItemProcedimentoHospitalarPhoSeq(final AinInternacao internacao) {
		return internacao.getItemProcedimentoHospitalar() == null ? null :internacao.getItemProcedimentoHospitalar().getPhoSeq();
	}

	private Integer validaItemProcedimentoHospitalarSeq(final AinInternacao internacao) {
		return internacao.getItemProcedimentoHospitalar() == null ? null :internacao.getItemProcedimentoHospitalar().getSeq();
	}

	private Integer validaInstituicaoHospitalar(final AinInternacao internacao) {
		return internacao.getInstituicaoHospitalar() == null ? null : internacao.getInstituicaoHospitalar().getSeq();
	}

	private Short validaOrigemEvento(final AinInternacao internacao) {
		return internacao.getOrigemEvento() == null ? null : internacao.getOrigemEvento().getSeq();
	}

	private Integer validaTipoCaracterInternacao(final AinInternacao internacao) {
		return internacao.getTipoCaracterInternacao() == null ? null : internacao.getTipoCaracterInternacao().getCodigo();
	}
	
	private DominioTransacaoAltaPaciente validaSeEstornoAltaAlteracao(final Date newDthrAltaMedica,final Date oldDtSaidaPaciente, 
			                                                          final Date newDtSaidaPaciente,final String oldTamCodigo, 
			                                                          final String newTamCodigo,final Date oldDthrAltaMedica,
                                                                      DominioTransacaoAltaPaciente transacao) {
		if(transacao == null){				
			if (newDthrAltaMedica != null && oldDtSaidaPaciente == null && newDtSaidaPaciente != null) {
				transacao = DominioTransacaoAltaPaciente.PROCESSA_ALTA;
			}else if ((oldTamCodigo != null && newTamCodigo == null && oldDthrAltaMedica != null && newDthrAltaMedica == null) || (oldTamCodigo != null && newTamCodigo != null && oldDthrAltaMedica != null && newDthrAltaMedica != null && oldDtSaidaPaciente != null && newDtSaidaPaciente == null)) {
				transacao = DominioTransacaoAltaPaciente.ESTORNA_ALTA;
			}else if ((oldDthrAltaMedica != null && newDthrAltaMedica != null && !oldDthrAltaMedica.equals(newDthrAltaMedica)) || (oldTamCodigo != null && newTamCodigo != null && !oldTamCodigo.equals(newTamCodigo)) || (oldDtSaidaPaciente != null && newDtSaidaPaciente != null && !oldDtSaidaPaciente.equals(newDtSaidaPaciente))) {
				transacao = DominioTransacaoAltaPaciente.ALTERA_ALTA;
			}
		}
		return transacao;
	}
	private DominioTransacaoAltaPaciente validaSeInternacaoAtualizadaResponsavel(final List<AinResponsaveisPaciente> oldRespPacientes,final List<AinResponsaveisPaciente> newRespPacientes,DominioTransacaoAltaPaciente transacao){
		if(transacao == null){
			if(oldRespPacientes != null && newRespPacientes != null){
				if(oldRespPacientes.size() != newRespPacientes.size()){
					transacao = DominioTransacaoAltaPaciente.INTERNACAO_ATUALIZADA;
				}else{
					AinResponsaveisPaciente oldRespPaciente = null;
					for (AinResponsaveisPaciente respPacientes : newRespPacientes) {
						oldRespPaciente = this.obterRespPacientesListaComparacao(oldRespPacientes, respPacientes.getSeq() == null ? null : respPacientes.getSeq());
						if(oldRespPaciente == null){
							transacao = DominioTransacaoAltaPaciente.INTERNACAO_ATUALIZADA;
						}else if(this.vaidaSeMudancaRespPacInternacao(respPacientes, oldRespPaciente)){
							transacao = DominioTransacaoAltaPaciente.INTERNACAO_ATUALIZADA;
						}
					}
				}
			}
		}
		return transacao;
	}
	private boolean vaidaSeMudancaRespPacInternacao(AinResponsaveisPaciente newRespPac,AinResponsaveisPaciente oldRespPac){
		if(!newRespPac.getSeq().equals(oldRespPac.getSeq())){
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}
	private AinResponsaveisPaciente obterRespPacientesListaComparacao(final List<AinResponsaveisPaciente> lista,Integer seqRespPac){
		if(seqRespPac != null && lista != null){		
			for (AinResponsaveisPaciente responsaveisPaciente : lista) {
				if(seqRespPac.equals(responsaveisPaciente.getSeq())){
					return responsaveisPaciente;
				}
			}
		}
		return null;
	}
	private DominioTransacaoAltaPaciente validaSeInternacaoAtualizadaCids(final List<AinCidsInternacao> oldCids, final List<AinCidsInternacao> newCids, DominioTransacaoAltaPaciente transacao) {
		if(transacao == null){
			if(oldCids != null && newCids != null && (oldCids.size() != newCids.size())){
					transacao = DominioTransacaoAltaPaciente.INTERNACAO_ATUALIZADA;
				}else{
					
					if(newCids != null){
						AinCidsInternacao oldCid = null;
						for(AinCidsInternacao cid : newCids){
							oldCid = this.obterCidListaComparacao(oldCids, cid.getId().getCidSeq());
							if(oldCid == null){						
								transacao = DominioTransacaoAltaPaciente.INTERNACAO_ATUALIZADA;
							}else if(this.vaidaSeMudancaCidsInternacao(cid, oldCid)){
								transacao = DominioTransacaoAltaPaciente.INTERNACAO_ATUALIZADA;
							}	
						}
					}
				}
			}
		return transacao;
	}
	private boolean vaidaSeMudancaCidsInternacao(AinCidsInternacao newCid,AinCidsInternacao oldCid){
		if(!newCid.getId().getCidSeq().equals(oldCid.getId().getCidSeq()) || !newCid.getId().getIntSeq().equals(oldCid.getId().getIntSeq()) || !newCid.getPrioridadeCid().equals(oldCid.getPrioridadeCid())){
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}
	private AinCidsInternacao obterCidListaComparacao(final List<AinCidsInternacao> lista,Integer seqCid){
		if (lista != null) {
			for (AinCidsInternacao cidsInternacao : lista) {
				if(seqCid.equals(cidsInternacao.getCid().getSeq())){
					return cidsInternacao;
				}
			}
		}
		return null;
	}
	private DominioTransacaoAltaPaciente validaSeInternacaoAtualizada(final Integer oldCarater,final Integer newCarater, final Short oldOrigemEvento, final Short newOrigemEvento,
	        final Integer oldHospitalOrigemPesq, final Integer newHospitalOrigemPesq,final Integer oldMedicoExternoPesq, final Integer newMedicoExternoPesq, final Date oldDthrInternacao,
	        final Date newDthrInternacao, final Boolean oldIndDifClasse, final Boolean newIndDifClasse,final Boolean oldIndEnvProntUnidInt, final Boolean newIndEnvProntUnidInt,
	        final String oldJustificativaAltDel, final String newJustificativaAltDel,final Integer oldIphSeq, final Short oldIphPhoSeq, 
	        final Integer newIphSeq, final Short newIphPhoSeq,DominioTransacaoAltaPaciente transacao) {
		if(transacao == null){
			if((!Objects.equals(oldCarater, newCarater)) || (!Objects.equals(oldCarater, newCarater)) || (!Objects.equals(oldOrigemEvento, newOrigemEvento)) || (!Objects.equals(oldHospitalOrigemPesq, newHospitalOrigemPesq)) 
			|| (!Objects.equals(oldMedicoExternoPesq, newMedicoExternoPesq)) || (oldDthrInternacao.compareTo(newDthrInternacao) != 0) || (!Objects.equals(oldIndDifClasse, newIndDifClasse)) || (!Objects.equals(oldIndEnvProntUnidInt, newIndEnvProntUnidInt))
					|| (!Objects.equals(oldJustificativaAltDel, newJustificativaAltDel))){
				transacao = DominioTransacaoAltaPaciente.INTERNACAO_ATUALIZADA;
			}else{
				if(oldIphSeq != null && oldIphPhoSeq != null && newIphSeq != null && newIphPhoSeq != null){				
					if(!oldIphSeq.equals(newIphSeq) || !oldIphPhoSeq.equals(newIphPhoSeq)){
						transacao = DominioTransacaoAltaPaciente.INTERNACAO_ATUALIZADA;
					}
				}
			}
		}
		return transacao;
	}

	
	
		
}