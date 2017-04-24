package br.gov.mec.aghu.ambulatorio.business;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.ambulatorio.dao.AacPermissoesAgendamentoConsultasDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.model.AacPermissaoAgendamentoConsultas;
import br.gov.mec.aghu.model.RapServidores;

@Stateless
public class ManterPermissoesAgendamentoConsultaON extends BaseBusiness {


	private static final Log LOG = LogFactory.getLog(ManterPermissoesAgendamentoConsultaON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@Inject
	private AacPermissoesAgendamentoConsultasDAO aacPermissoesAgendamentoConsultasDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 2147319286120307641L;
	
	public void inserirPermissoesAgendamentoConsulta(AacPermissaoAgendamentoConsultas entity) {
		aacPermissoesAgendamentoConsultasDAO.persistir(entity);
	}

	public void inserirPermissoesAgendamentoConsulta(
			RapServidores servidor, List<AacPermissaoAgendamentoConsultas> listPermissoesInserir) {
		List<AacPermissaoAgendamentoConsultas> listaRemover = aacPermissoesAgendamentoConsultasDAO.obterListaPermAgndConsPorServidorTipo(servidor,null);
		for(AacPermissaoAgendamentoConsultas permissaoRemover: listaRemover){
			aacPermissoesAgendamentoConsultasDAO.remover(permissaoRemover);
		}
		aacPermissoesAgendamentoConsultasDAO.flush();
		for(AacPermissaoAgendamentoConsultas permissao: listPermissoesInserir){
			permissao.setSeq(null);
			inserirPermissoesAgendamentoConsulta(permissao);
		}
		
	}

}
