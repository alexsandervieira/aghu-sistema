package br.gov.mec.aghu.ambulatorio.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AacPermissaoAgendamentoConsultas;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;

public class AacPermissoesAgendamentoConsultasDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AacPermissaoAgendamentoConsultas> {

	private static final long serialVersionUID = -3291847931629049361L;

	public List<AacPermissaoAgendamentoConsultas> pesquisarPermissoesAgendamentoConsulta(Short vinculoServidor, Integer matriculaServidor, String nomeServidor) {
		DetachedCriteria criteria = montaCriteriaPesquisarPermissoesAgendamentoConsultas(
				vinculoServidor, matriculaServidor, nomeServidor);
		criteria.addOrder(Order.asc("PF." + RapPessoasFisicas.Fields.NOME.toString()));
		return executeCriteria(criteria);
	}

	private DetachedCriteria montaCriteriaPesquisarPermissoesAgendamentoConsultas(
			Short vinculoServidor, Integer matriculaServidor,
			String nomeServidor) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AacPermissaoAgendamentoConsultas.class);

		criteria.createAlias(AacPermissaoAgendamentoConsultas.Fields.SERVIDOR.toString(), "SER");
		criteria.createAlias("SER." + RapServidores.Fields.PESSOA_FISICA.toString(), "PF");
		if(vinculoServidor != null){
			criteria.add(Restrictions.eq("SER." + RapServidores.Fields.VIN_CODIGO.toString(), vinculoServidor));
		}
		if(matriculaServidor != null){
			criteria.add(Restrictions.eq("SER." + RapServidores.Fields.MATRICULA.toString(), matriculaServidor));
		}
		if(StringUtils.isNotBlank(nomeServidor)){
			criteria.add(Restrictions.ilike("PF." + RapPessoasFisicas.Fields.NOME.toString(), nomeServidor, MatchMode.ANYWHERE));
		}
		return criteria;
	}

	public List<AacPermissaoAgendamentoConsultas> obterListaPermAgndConsPorServidorTipo(
			RapServidores servidor, String tipo) {

		DetachedCriteria criteria = DetachedCriteria.forClass(AacPermissaoAgendamentoConsultas.class);

		criteria.createAlias(AacPermissaoAgendamentoConsultas.Fields.SERVIDOR.toString(), "SER");
		criteria.createAlias("SER." + RapServidores.Fields.PESSOA_FISICA.toString(), "PF");

		criteria.add(Restrictions.eq(AacPermissaoAgendamentoConsultas.Fields.SERVIDOR.toString(), servidor));

		if("ESP".equals(tipo)){
			criteria.createAlias(AacPermissaoAgendamentoConsultas.Fields.ESPECIALIDADE.toString(), "ESP");
			criteria.add(Restrictions.isNotNull(AacPermissaoAgendamentoConsultas.Fields.ESPECIALIDADE.toString()));
		} else if("EQP".equals(tipo)){
			criteria.createAlias(AacPermissaoAgendamentoConsultas.Fields.EQUIPE.toString(), "EQP");
			criteria.add(Restrictions.isNotNull(AacPermissaoAgendamentoConsultas.Fields.EQUIPE.toString()));
		} else if("GRD".equals(tipo)){
			criteria.createAlias(AacPermissaoAgendamentoConsultas.Fields.GRADE.toString(), "GRD");
			criteria.add(Restrictions.isNotNull(AacPermissaoAgendamentoConsultas.Fields.GRADE.toString()));
		} else if("UNF".equals(tipo)){
			criteria.createAlias(AacPermissaoAgendamentoConsultas.Fields.UNIDADE_FUNCIONAL.toString(), "UNF");
			criteria.add(Restrictions.isNotNull(AacPermissaoAgendamentoConsultas.Fields.UNIDADE_FUNCIONAL.toString()));
		} else if("PROF".equals(tipo)){
			criteria.createAlias(AacPermissaoAgendamentoConsultas.Fields.PROFISSIONAL.toString(), "PROF");
			criteria.createAlias("PROF." + RapServidores.Fields.PESSOA_FISICA.toString(), "PF_PROF");
			criteria.add(Restrictions.isNotNull(AacPermissaoAgendamentoConsultas.Fields.PROFISSIONAL.toString()));
		}

		return executeCriteria(criteria);
	}

	public List<Integer> obterListaIdsPermAgndConsPorServidorTipo(
			RapServidores servidor, String tipo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AacPermissaoAgendamentoConsultas.class);

		criteria.add(Restrictions.eq(AacPermissaoAgendamentoConsultas.Fields.SERVIDOR.toString(), servidor));

		if("ESP".equals(tipo)){
			criteria.add(Restrictions.isNotNull(AacPermissaoAgendamentoConsultas.Fields.ESPECIALIDADE.toString()));
			criteria.setProjection(Projections.property(AacPermissaoAgendamentoConsultas.Fields.SEQ_ESPECIALIDADE.toString()));
		} else if("EQP".equals(tipo)){
			criteria.add(Restrictions.isNotNull(AacPermissaoAgendamentoConsultas.Fields.EQUIPE.toString()));
			criteria.setProjection(Projections.property(AacPermissaoAgendamentoConsultas.Fields.SEQ_EQUIPE.toString()));
		} else if("GRD".equals(tipo)){
			criteria.add(Restrictions.isNotNull(AacPermissaoAgendamentoConsultas.Fields.GRADE.toString()));
			criteria.setProjection(Projections.property(AacPermissaoAgendamentoConsultas.Fields.SEQ_GRADE.toString()));
		} else if("UNF".equals(tipo)){
			criteria.createAlias(AacPermissaoAgendamentoConsultas.Fields.UNIDADE_FUNCIONAL.toString(), "UNF");
			criteria.setProjection(Projections.property(AacPermissaoAgendamentoConsultas.Fields.SEQ_UNF.toString()));
		} 

		return executeCriteria(criteria);
	}

	public List<RapServidoresId> obterListaProfPermAgndConsPorServidor(
			RapServidores servidor) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AacPermissaoAgendamentoConsultas.class);

		criteria.add(Restrictions.eq(AacPermissaoAgendamentoConsultas.Fields.SERVIDOR.toString(), servidor));
		criteria.add(Restrictions.isNotNull(AacPermissaoAgendamentoConsultas.Fields.PROFISSIONAL.toString()));
		criteria.setProjection(Projections.property(AacPermissaoAgendamentoConsultas.Fields.ID_PROFISSIONAL.toString()));

		return executeCriteria(criteria);
	}

	public AacPermissaoAgendamentoConsultas obterAacPermissaoAgendConsultaPorServidorGrade(RapServidores servidor, Integer grade){
		DetachedCriteria criteria = DetachedCriteria.forClass(AacPermissaoAgendamentoConsultas.class);

		criteria.add(Restrictions.eq(AacPermissaoAgendamentoConsultas.Fields.SERVIDOR.toString(), servidor));
		criteria.add(Restrictions.eq(AacPermissaoAgendamentoConsultas.Fields.SEQ_GRADE.toString(), grade));
		
		return (AacPermissaoAgendamentoConsultas) executeCriteriaUniqueResult(criteria);
	}
}
