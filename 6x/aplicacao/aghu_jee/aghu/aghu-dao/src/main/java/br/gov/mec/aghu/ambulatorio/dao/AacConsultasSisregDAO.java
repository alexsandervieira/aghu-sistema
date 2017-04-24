package br.gov.mec.aghu.ambulatorio.dao;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AacConsultasSisreg;

public class AacConsultasSisregDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AacConsultasSisreg> {
		
	private static final long serialVersionUID = 2089394693984132195L;

	public List<AacConsultasSisreg> pesquisarConsultasSisreg(){
		DetachedCriteria criteria = DetachedCriteria.forClass(AacConsultasSisreg.class);
		return executeCriteria(criteria);
	}
	
	public List<AacConsultasSisreg> pesquisarConsultasSisregNaoImportadas(){
		DetachedCriteria criteria = DetachedCriteria.forClass(AacConsultasSisreg.class);
		criteria.add(Restrictions.eq(AacConsultasSisreg.Fields.IND_MARCADO.toString(),false));
		return executeCriteria(criteria);
	}
	
	public void limparConsultasSisregNaoImportadas(){
		final StringBuffer hql = new StringBuffer(120);
		hql.append(" delete");
		hql.append(" from AacConsultasSisreg");
		hql.append(" where ").append(AacConsultasSisreg.Fields.SEQ);
		hql.append(" in(")
		    .append(" select seq from AacConsultasSisreg)");
		
		final Query query = createHibernateQuery(hql.toString());
		query.executeUpdate();
	}
	public void unirTransacao(){
		joinTransaction();
	}
}