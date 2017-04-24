package br.gov.mec.aghu.prescricaomedica.dao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.dominio.DominioIndPendenteAmbulatorio;
import br.gov.mec.aghu.model.MpmAnamneses;
import br.gov.mec.aghu.model.MpmEvolucoes;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapQualificacao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.vo.EvolucaoPrescricaoVO;

public class MpmEvolucoesDAO  extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MpmEvolucoes> {
	
	/**
	* 
	*/
	private static final long serialVersionUID = 4720476303686436422L;
	
	public List<MpmEvolucoes> obterEvolucoesAnamnese(MpmAnamneses anamnese,
	        Date dataFim, DominioIndPendenteAmbulatorio situacao) {
		List<DominioIndPendenteAmbulatorio> situacoes = new ArrayList<DominioIndPendenteAmbulatorio>();
		if (situacao != null) {
		        situacoes.add(situacao);
		}
		return obterEvolucoesAnamnese(anamnese, dataFim, situacoes);
	}
	
	public List<MpmEvolucoes> obterEvolucoesAnamnese(MpmAnamneses anamnese,
	        Date dataFim, List<DominioIndPendenteAmbulatorio> situacoes) {
		if (anamnese == null) {
		        return null;
		}
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmEvolucoes.class, "MPM");
		criteria.createAlias("MPM." + MpmEvolucoes.Fields.SERVIDOR.toString(), "SER");
		criteria.createAlias("SER." + RapServidores.Fields.PESSOA_FISICA.toString(), "PES");
		criteria.add(Restrictions.eq(MpmEvolucoes.Fields.ANA_SEQ.toString(),anamnese.getSeq()));

		if (situacoes != null && !situacoes.isEmpty()) {
		        criteria.add(Restrictions.in(MpmEvolucoes.Fields.PENDENTE.toString(), situacoes));
		}
		criteria.addOrder(Order.asc(MpmEvolucoes.Fields.DTHR_FIM.toString()));
		return executeCriteria(criteria);
	}
	
	public List<MpmEvolucoes> listarEvolucoesConcluidasAnamnese(Long seqAnamnese) {
		if (seqAnamnese == null) {
		        return null;
		}
		DetachedCriteria criteria = DetachedCriteria
		                .forClass(MpmEvolucoes.class);
		criteria.createAlias(MpmEvolucoes.Fields.SERVIDOR.toString(), "servidor");
		criteria.createAlias("servidor." + RapServidores.Fields.PESSOA_FISICA.toString(), "pessoaFisica");
		criteria.add(Restrictions.eq(MpmEvolucoes.Fields.ANA_SEQ.toString(),
		                seqAnamnese));
		criteria.add(Restrictions.eq(MpmEvolucoes.Fields.PENDENTE.toString(),
		                DominioIndPendenteAmbulatorio.V));
		criteria.addOrder(Order.desc(MpmEvolucoes.Fields.DTHR_REFERENCIA
		                .toString()));
		return executeCriteria(criteria);
	}
	
	public boolean verificarEvolucoesPendentes(Long anaSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(
		                MpmEvolucoes.class, "EVO");
		criteria.createAlias("EVO." + MpmEvolucoes.Fields.ANAMNESE.toString(),
		                "ANA", JoinType.INNER_JOIN);
		criteria.add(Restrictions.ge(
		                "EVO." + MpmEvolucoes.Fields.DTHR_FIM.toString(), new Date()));
		criteria.add(Restrictions.eq(
		                "EVO." + MpmEvolucoes.Fields.IND_PENDENTE.toString(),
		                DominioIndPendenteAmbulatorio.P));
		criteria.add(Restrictions.eq(
		                "ANA." + MpmAnamneses.Fields.SEQ.toString(), anaSeq));
		return executeCriteriaExists(criteria);
	}
	
	public List<MpmEvolucoes> pesquisarEvolucoesAnteriores(Integer firstResult,
	        Integer maxResult, String orderProperty, boolean asc, Long anaSeq,
	        Date dataInicial, Date dataFinal) {
		DetachedCriteria criteria = montarCriteriaPesquisarEvolucoesAnteriores(
		                anaSeq, dataInicial, dataFinal);
		criteria.addOrder(Order.asc(MpmEvolucoes.Fields.DTHR_CRIACAO.toString()));
		return executeCriteria(criteria, firstResult, maxResult, orderProperty,
		                asc);
	}
	
	public Long pesquisarEvolucoesAnterioresCount(Long anaSeq, Date dataInicial, Date dataFinal) {
		DetachedCriteria criteria = montarCriteriaPesquisarEvolucoesAnteriores(
		                anaSeq, dataInicial, dataFinal);
		return executeCriteriaCount(criteria);
	}
	
	private DetachedCriteria montarCriteriaPesquisarEvolucoesAnteriores(
	        Long anaSeq, Date dataInicial, Date dataFinal) {
		DetachedCriteria criteria = DetachedCriteria.forClass(
		                MpmEvolucoes.class, "EVO");
		criteria.createAlias("EVO." + MpmEvolucoes.Fields.SERVIDOR.toString(), "SERV", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SERV." + RapServidores.Fields.PESSOA_FISICA.toString(), "PF", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq(MpmEvolucoes.Fields.ANA_SEQ.toString(),
		                anaSeq));
		criteria.add(Restrictions.eq(
		                "EVO." + MpmEvolucoes.Fields.IND_PENDENTE.toString(),
		                DominioIndPendenteAmbulatorio.V));
		if (dataInicial != null) {
		        criteria.add(Restrictions.ge("EVO."
		                        + MpmEvolucoes.Fields.DTHR_CRIACAO.toString(),
		                        DateUtil.truncaData(dataInicial)));
		}
		if (dataFinal != null) {
		        criteria.add(Restrictions.le("EVO."
		                        + MpmEvolucoes.Fields.DTHR_CRIACAO.toString(),
		                        DateUtil.truncaDataFim(dataFinal)));
		}
		return criteria;
	}
	
	public boolean verificarEvolucoesAnamnesePorSituacao(Long anaSeq,
	        DominioIndPendenteAmbulatorio pendente, boolean situacaoIgual) {
		DetachedCriteria criteria = DetachedCriteria.forClass(
		                MpmEvolucoes.class, "EVO");
		criteria.createAlias("EVO." + MpmEvolucoes.Fields.ANAMNESE.toString(),
		                "ANA", JoinType.INNER_JOIN);
		criteria.add(Restrictions.eq(
		                "ANA." + MpmAnamneses.Fields.SEQ.toString(), anaSeq));
		if (situacaoIgual) {
		        criteria.add(Restrictions.eq("EVO."
		                        + MpmEvolucoes.Fields.IND_PENDENTE.toString(), pendente));
		} else {
		        criteria.add(Restrictions.ne("EVO."
		                        + MpmEvolucoes.Fields.IND_PENDENTE.toString(), pendente));
		}
		return executeCriteriaExists(criteria);
	}
	
	public boolean verificarEvolucoesValidadas(Long anaSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(
		                MpmEvolucoes.class, "EVO");
		criteria.createAlias("EVO." + MpmEvolucoes.Fields.ANAMNESE.toString(),
		                "ANA", JoinType.INNER_JOIN);
		criteria.add(Restrictions.eq(
		                "EVO." + MpmEvolucoes.Fields.IND_PENDENTE.toString(),
		                DominioIndPendenteAmbulatorio.V));
		criteria.add(Restrictions.eq(
		                "ANA." + MpmAnamneses.Fields.SEQ.toString(), anaSeq));
		return executeCriteriaExists(criteria);
	}
	
	public MpmEvolucoes buscarMpmEvolucoes(Long seqEvolucao) {
		DetachedCriteria criteria = DetachedCriteria
		                .forClass(MpmEvolucoes.class);
		criteria.createAlias(MpmEvolucoes.Fields.ANAMNESE.toString(), "ANA");
		criteria.createAlias("ANA." + MpmAnamneses.Fields.ATENDIMENTO.toString(), "ATE");
		criteria.add(Restrictions.eq(MpmEvolucoes.Fields.SEQ.toString(),
		                seqEvolucao));
		return (MpmEvolucoes) executeCriteriaUniqueResult(criteria);
	}
	
	public boolean verificarEvolucaoRealizadaPorSituacao(Long anaSeq,
	        DominioIndPendenteAmbulatorio situacao,
	        Date dataReferencia, boolean situacaoIgual) {
		DetachedCriteria criteria = DetachedCriteria.forClass(
		                MpmEvolucoes.class, "EVO");
		criteria.createAlias("EVO." + MpmEvolucoes.Fields.ANAMNESE.toString(),
		                "ANA", JoinType.INNER_JOIN);
		criteria.add(Restrictions.eq(
		                "ANA." + MpmAnamneses.Fields.SEQ.toString(), anaSeq));
		criteria.add(Restrictions.eq("EVO."
		                + MpmEvolucoes.Fields.DTHR_REFERENCIA.toString(),
		                dataReferencia));
		if (situacaoIgual) {
		        criteria.add(Restrictions.eq("EVO."
		                        + MpmEvolucoes.Fields.IND_PENDENTE.toString(), situacao));
		} else {
		        criteria.add(Restrictions.ne("EVO."
		                        + MpmEvolucoes.Fields.IND_PENDENTE.toString(), situacao));
		}
		return executeCriteriaExists(criteria);
	}
	
	public boolean verificarEvolucaoSeguinteRealizadaPorSituacao(Long anaSeq,
	        DominioIndPendenteAmbulatorio situacao, Date dataReferencia) {
		DetachedCriteria criteria = DetachedCriteria.forClass(
		                MpmEvolucoes.class, "EVO");
		criteria.createAlias("EVO." + MpmEvolucoes.Fields.ANAMNESE.toString(),
		                "ANA", JoinType.INNER_JOIN);
		criteria.add(Restrictions.eq(
		                "ANA." + MpmAnamneses.Fields.SEQ.toString(), anaSeq));
		criteria.add(Restrictions.gt("EVO."
		                + MpmEvolucoes.Fields.DTHR_REFERENCIA.toString(),
		                dataReferencia));
		criteria.add(Restrictions.eq(
		                "EVO." + MpmEvolucoes.Fields.IND_PENDENTE.toString(), situacao));
		return executeCriteriaExists(criteria);
	}
	
	public List<MpmEvolucoes> obterEvolucoesAnamnese(MpmAnamneses anamnese, List<DominioIndPendenteAmbulatorio> situacoes, Date dataInicio, Date dataFim) {
		if (anamnese == null) {
			return null;
		}

		DetachedCriteria criteria = DetachedCriteria.forClass(MpmEvolucoes.class, "MPM");
		criteria.createAlias("MPM." + MpmEvolucoes.Fields.SERVIDOR.toString(), "SER", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SER." + RapServidores.Fields.PESSOA_FISICA.toString(), "PES", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("PES." + RapPessoasFisicas.Fields.QUALIFICACOES.toString(), "QUA", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("QUA." + RapQualificacao.Fields.TIPO_QUALIFICACAO.toString(), "TIP", JoinType.LEFT_OUTER_JOIN);
		
		criteria = createFiltro(criteria, anamnese, situacoes, dataFim, dataFim);
		criteria.addOrder(Order.asc("MPM." + MpmEvolucoes.Fields.DTHR_FIM.toString()));
		return executeCriteria(criteria);
	}
	
	public List<EvolucaoPrescricaoVO> obterEvolucoesAnamneseProjection(MpmAnamneses anamnese, List<DominioIndPendenteAmbulatorio> situacoes, Date dataInicio, Date dataFim) {	
		if (anamnese == null) {
			return null;
		}
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmEvolucoes.class,"MPM");
		
		criteria.createAlias("MPM." + MpmEvolucoes.Fields.SERVIDOR.toString(), "SER", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SER." + RapServidores.Fields.PESSOA_FISICA.toString(), "PES", JoinType.LEFT_OUTER_JOIN);
		
		criteria = createFiltro(criteria, anamnese, situacoes, dataFim, dataFim);
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("MPM."+MpmEvolucoes.Fields.SEQ.toString()), EvolucaoPrescricaoVO.Fields.SEQ.toString())
				.add(Projections.property("MPM."+MpmEvolucoes.Fields.DTHR_CRIACAO.toString()), EvolucaoPrescricaoVO.Fields.DTHR_CRIACAO.toString())
				.add(Projections.property("MPM."+MpmEvolucoes.Fields.DTHR_FIM.toString()), EvolucaoPrescricaoVO.Fields.DTHR_FIM.toString())
				.add(Projections.property("MPM."+MpmEvolucoes.Fields.DTHR_REFERENCIA.toString()), EvolucaoPrescricaoVO.Fields.DTHR_REFERENCIA.toString())
				.add(Projections.property("MPM."+MpmEvolucoes.Fields.PENDENTE.toString()), EvolucaoPrescricaoVO.Fields.PENDENTE.toString())
				.add(Projections.property("MPM."+MpmEvolucoes.Fields.SITUACAO.toString()), EvolucaoPrescricaoVO.Fields.SITUACAO.toString())
				.add(Projections.property("MPM."+MpmEvolucoes.Fields.DTHR_PENDENTE.toString()), EvolucaoPrescricaoVO.Fields.DTHR_PENDENTE.toString())
				.add(Projections.property("PES."+RapPessoasFisicas.Fields.NOME.toString()), EvolucaoPrescricaoVO.Fields.NOME_PROF.toString())
				.add(Projections.property("SER."+RapServidores.Fields.MATRICULA.toString()), EvolucaoPrescricaoVO.Fields.MATRICULA.toString())
				.add(Projections.property("SER."+RapServidores.Fields.VIN_CODIGO.toString()), EvolucaoPrescricaoVO.Fields.VIN_CODIGO.toString())
		);		
		criteria.addOrder(Order.desc("MPM." + MpmEvolucoes.Fields.DTHR_CRIACAO.toString()));
		
		criteria.setResultTransformer(Transformers.aliasToBean(EvolucaoPrescricaoVO.class));
		
		return executeCriteria(criteria);
	}
	
	
	private DetachedCriteria createFiltro(DetachedCriteria criteria, MpmAnamneses anamnese, 
			List<DominioIndPendenteAmbulatorio> situacoes, Date dataInicio, Date dataFim ) {
		criteria.add(Restrictions.eq("MPM." + MpmEvolucoes.Fields.ANA_SEQ.toString(), anamnese.getSeq()));
		if (situacoes != null && !situacoes.isEmpty()) {
			criteria.add(Restrictions.in("MPM." + MpmEvolucoes.Fields.PENDENTE.toString(), situacoes));
		}
		if (dataInicio != null) {
            Calendar dataInicial = Calendar.getInstance();
            dataInicial.setTime(dataInicio);
            dataInicial.set(Calendar.HOUR_OF_DAY,0);
            dataInicial.set(Calendar.MINUTE,0);
            dataInicial.set(Calendar.SECOND,0);
            criteria.add(Restrictions.ge("MPM." + MpmEvolucoes.Fields.DTHR_CRIACAO.toString(), dataInicial.getTime()));
		}
		if (dataFim != null) {
            Calendar dataFinal = Calendar.getInstance();
            dataFinal.setTime(dataFim);        
            dataFinal.set(Calendar.HOUR_OF_DAY,23);
            dataFinal.set(Calendar.MINUTE,59);
            dataFinal.set(Calendar.SECOND,59);
            criteria.add(Restrictions.le("MPM." + MpmEvolucoes.Fields.DTHR_FIM.toString(), dataFinal.getTime()));
		}
		return criteria;
	}
}