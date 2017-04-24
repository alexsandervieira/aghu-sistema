package br.gov.mec.aghu.ambulatorio.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.AacFormaAgendamento;
import br.gov.mec.aghu.model.AacTipoConsultaSisreg;

public class AacTipoConsultaSisregDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AacConsultas> {

	private static final long serialVersionUID = -4805709007387887480L;

	public List<AacTipoConsultaSisreg> obterTipoConsultaSisregPorSeq(){
		DetachedCriteria criteria = DetachedCriteria.forClass(AacTipoConsultaSisreg.class);
		return  executeCriteria(criteria);
	}
	
	
	public AacFormaAgendamento listarFormasAgendamentosSisreg(Short condicaoAtendimento, Short tipoAgendamento, Short convenioPadrao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AacFormaAgendamento.class);
		
		criteria.add(Restrictions.eq(AacFormaAgendamento.Fields.CAA_SEQ.toString(), condicaoAtendimento));
		criteria.add(Restrictions.eq(AacFormaAgendamento.Fields.TAG_SEQ.toString(), tipoAgendamento));
		criteria.add(Restrictions.eq(AacFormaAgendamento.Fields.PGD_SEQ.toString(), convenioPadrao));
	    	    
		return (AacFormaAgendamento)executeCriteriaUniqueResult(criteria);

		
	}
}
