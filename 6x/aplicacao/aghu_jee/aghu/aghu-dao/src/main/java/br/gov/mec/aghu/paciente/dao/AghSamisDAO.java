package br.gov.mec.aghu.paciente.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AghSamis;


public class AghSamisDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AghSamis>{

	private static final long serialVersionUID = 2459100709485370094L;

	public Long pesquisaCount(Integer codigoPesquisaOrigemProntuario,
			String descricaoPesquisa, DominioSituacao situacaoOrigemProntuario) {
		DetachedCriteria criteria = createPesquisaCriteria(codigoPesquisaOrigemProntuario, descricaoPesquisa, situacaoOrigemProntuario, null);
		return executeCriteriaCount(criteria);
	}
	
	public Long pesquisaCount(Integer codigoPesquisaOrigemProntuario,
			String descricaoPesquisa, DominioSituacao situacaoOrigemProntuario, Boolean origemPadrao) {
		DetachedCriteria criteria = createPesquisaCriteria(codigoPesquisaOrigemProntuario, descricaoPesquisa, situacaoOrigemProntuario, origemPadrao);
		return executeCriteriaCount(criteria);
	}

	public Boolean existeOrigemPadrão(Short codigoPesquisaOrigemProntuario) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghSamis.class);
		if(codigoPesquisaOrigemProntuario != null){
			criteria.add(Restrictions.ne(AghSamis.Fields.CODIGO.toString(), codigoPesquisaOrigemProntuario));
		}
		criteria.add(Restrictions.eq(AghSamis.Fields.IND_ATIVO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq(AghSamis.Fields.ORIGEM_PADRAO.toString(), Boolean.TRUE));

		return executeCriteriaExists(criteria);
	}

	private DetachedCriteria createPesquisaCriteria(
			Integer codigoPesquisaOrigemProntuario, String descricaoPesquisa,
			DominioSituacao situacaoOrigemProntuario, Boolean origemPadrao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghSamis.class);
		if (codigoPesquisaOrigemProntuario != null) {
			criteria.add(Restrictions.eq(AghSamis.Fields.CODIGO.toString(), codigoPesquisaOrigemProntuario.shortValue()));
		}
		if (StringUtils.isNotBlank(descricaoPesquisa)) {
			criteria.add(Restrictions.ilike(AghSamis.Fields.DESCRICAO.toString(), descricaoPesquisa, MatchMode.ANYWHERE));
		}
		if (situacaoOrigemProntuario != null) {
			criteria.add(Restrictions.eq(AghSamis.Fields.IND_ATIVO.toString(), situacaoOrigemProntuario));
		}
		if (origemPadrao != null) {
			criteria.add(Restrictions.eq(AghSamis.Fields.ORIGEM_PADRAO.toString(), origemPadrao));
		}
		
		return criteria;
	}

	public List<AghSamis> pesquisa(Integer firstResult, Integer maxResults,
			String orderProperty, boolean asc, Integer codigo,
			String descricao, DominioSituacao situacao) {
		DetachedCriteria criteria = createPesquisaCriteria(codigo, descricao, situacao, null);

		return executeCriteria(criteria, firstResult, maxResults, orderProperty, asc);
	}
	
	public List<AghSamis> pesquisaOrigemProntuarioPorCodigoOuDescricao(Integer codigoPesquisaOrigemProntuario, String descricaoPesquisa) {
		DetachedCriteria criteria = createPesquisaCriteria(codigoPesquisaOrigemProntuario, descricaoPesquisa, DominioSituacao.A, null);
		return executeCriteria(criteria);
	}	
	
	public AghSamis buscarOrigemPadrão() {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghSamis.class);
		
		criteria.add(Restrictions.eq(AghSamis.Fields.IND_ATIVO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq(AghSamis.Fields.ORIGEM_PADRAO.toString(), Boolean.TRUE));

		return (AghSamis) executeCriteriaUniqueResult(criteria);
	}

}