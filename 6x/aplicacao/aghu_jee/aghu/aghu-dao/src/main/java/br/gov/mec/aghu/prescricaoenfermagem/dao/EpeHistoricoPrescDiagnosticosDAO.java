package br.gov.mec.aghu.prescricaoenfermagem.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.dominio.DominioSituacaoHistPrescDiagnosticos;
import br.gov.mec.aghu.model.EpeDiagnostico;
import br.gov.mec.aghu.model.EpeFatRelDiagnostico;
import br.gov.mec.aghu.model.EpeHistoricoPrescDiagnosticos;
import br.gov.mec.aghu.model.EpePrescricaoEnfermagem;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.vo.RapServidoresVO;

public class EpeHistoricoPrescDiagnosticosDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<EpeHistoricoPrescDiagnosticos> {

	private static final long serialVersionUID = -2425925223725859877L;
	
	public List<EpeHistoricoPrescDiagnosticos> listarEpeHistoricoPrescDiagnosticosPorAtendimento(Integer atdSeq,
			List<DominioSituacaoHistPrescDiagnosticos> listSituacao, EpeDiagnostico diagnostico, 
			RapServidoresVO servidor, Date dataInicio, Date dataFim) {
		DetachedCriteria criteria = DetachedCriteria.forClass(EpeHistoricoPrescDiagnosticos.class);
		criteria.createAlias(EpeHistoricoPrescDiagnosticos.Fields.SERVIDOR.toString(), "SER");
		criteria.createAlias("SER." + RapServidores.Fields.PESSOA_FISICA.toString(), "PF");
		
		criteria.add(Restrictions.eq(EpeHistoricoPrescDiagnosticos.Fields.ATENDIMENTO_SEQ.toString(), atdSeq));
		criteria.add(Restrictions.in(EpeHistoricoPrescDiagnosticos.Fields.IND_SITUACAO.toString(), listSituacao));
		criteria.add(Restrictions.eq(EpeHistoricoPrescDiagnosticos.Fields.IND_PENDENTE.toString(), false));
		criteria.add(Restrictions.ge(EpeHistoricoPrescDiagnosticos.Fields.CRIADO_EM.toString(),DateUtil.obterDataComHoraInical(dataInicio)));
		if(diagnostico != null){
			criteria.add(Restrictions.eq(EpeHistoricoPrescDiagnosticos.Fields.DIAGNOSTICO.toString(), diagnostico));
		}
		if(servidor != null){
			criteria.add(Restrictions.eq("SER." + RapServidores.Fields.MATRICULA.toString(), servidor.getMatricula()));
		}
		if(dataFim != null){
			criteria.add(Restrictions.le(EpeHistoricoPrescDiagnosticos.Fields.CRIADO_EM.toString(),DateUtil.obterDataComHoraFinal(dataFim)));
		}
		
		return executeCriteria(criteria);
	}
	
	public List<EpeDiagnostico> listarDiagnosticosPorAtendimento(String filtro, Integer atdSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(EpeHistoricoPrescDiagnosticos.class);
		
		criteria.createAlias(EpeHistoricoPrescDiagnosticos.Fields.DIAGNOSTICO.toString(), "DGN");
		
		ProjectionList projList = Projections.projectionList();
		projList.add(Projections.groupProperty("DGN." + EpeDiagnostico.Fields.ID.toString()), EpeDiagnostico.Fields.ID.toString());
		projList.add(Projections.property("DGN." + EpeDiagnostico.Fields.VERSION.toString()), EpeDiagnostico.Fields.VERSION.toString());
		projList.add(Projections.property("DGN." + EpeDiagnostico.Fields.SUBGRUPO_NECES_BASICA.toString()), EpeDiagnostico.Fields.SUBGRUPO_NECES_BASICA.toString());
		projList.add(Projections.property("DGN." + EpeDiagnostico.Fields.DESCRICAO.toString()), EpeDiagnostico.Fields.DESCRICAO.toString());
		projList.add(Projections.property("DGN." + EpeDiagnostico.Fields.DEFINICAO.toString()), EpeDiagnostico.Fields.DEFINICAO.toString());
		projList.add(Projections.property("DGN." + EpeDiagnostico.Fields.SITUACAO.toString()), EpeDiagnostico.Fields.SITUACAO.toString());
		criteria.setProjection(projList);
		
		criteria.add(Restrictions.eq(EpeHistoricoPrescDiagnosticos.Fields.ATENDIMENTO_SEQ.toString(), atdSeq));
	
		if(CoreUtil.isNumeroShort(filtro)){
			Short seq = Short.parseShort(filtro);
			criteria.add(Restrictions.eq("DGN." + EpeDiagnostico.Fields.SEQUENCIA.toString(), seq));
		}else{
			criteria.add(Restrictions.ilike("DGN." + EpeDiagnostico.Fields.DESCRICAO.toString(),filtro, MatchMode.ANYWHERE));
			
		}
		criteria.setResultTransformer(Transformers.aliasToBean(EpeDiagnostico.class));
		return executeCriteria(criteria);
	}
	
	public List<RapServidoresVO> listarProfissionalPorAtendimento(String parametro, Integer atdSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(EpeHistoricoPrescDiagnosticos.class);
		
		criteria.createAlias(EpeHistoricoPrescDiagnosticos.Fields.SERVIDOR.toString(), "SER");
		criteria.createAlias("SER." + RapServidores.Fields.PESSOA_FISICA.toString(), "PF", JoinType.LEFT_OUTER_JOIN);
		
		criteria.setProjection(Projections.distinct(Projections.projectionList()
		.add(Projections.property("SER." + RapServidores.Fields.MATRICULA.toString()), RapServidoresVO.Fields.MATRICULA.toString())
		.add(Projections.property("SER." + RapServidores.Fields.VIN_CODIGO.toString()), RapServidoresVO.Fields.VINCULO.toString())
		.add(Projections.property("PF." +  RapPessoasFisicas.Fields.NOME.toString()), RapServidoresVO.Fields.NOME.toString())));
		
		criteria.add(Restrictions.eq(EpeHistoricoPrescDiagnosticos.Fields.ATENDIMENTO_SEQ.toString(), atdSeq));
		criteria.add(Restrictions.ilike("PF." + RapPessoasFisicas.Fields.NOME.toString(),parametro , MatchMode.ANYWHERE));
		criteria.setResultTransformer(Transformers.aliasToBean(RapServidoresVO.class));
		
		return executeCriteria(criteria);
	}
	
	/*ProjectionList projList = Projections.projectionList();
		projList.add(Projections.groupProperty("SER." + RapServidores.Fields.ID.toString()), RapServidores.Fields.ID.toString());
		projList.add(Projections.property("SER." + RapServidores.Fields.USUARIO.toString()), RapServidores.Fields.USUARIO.toString());
		projList.add(Projections.property("SER." + RapServidores.Fields.PESSOA_FISICA.toString()), RapServidores.Fields.PESSOA_FISICA.toString());
		projList.add(Projections.property("PF." +  RapPessoasFisicas.Fields.NOME.toString()), "PF." + RapPessoasFisicas.Fields.NOME.toString());*/
	
	public EpeHistoricoPrescDiagnosticos obterEpeHistoricoPrescDiagnosticosPorPrescDiag(EpeFatRelDiagnostico fatRelDiagnostico,
			EpePrescricaoEnfermagem prescricaoEnfermagem, Boolean indPendente) {
		DetachedCriteria criteria = DetachedCriteria.forClass(EpeHistoricoPrescDiagnosticos.class);
		
		criteria.add(Restrictions.eq(EpeHistoricoPrescDiagnosticos.Fields.FAT_REL_DIAGNOSTICO.toString(), fatRelDiagnostico));
		criteria.add(Restrictions.eq(EpeHistoricoPrescDiagnosticos.Fields.PRESCRICAO_ENFERMAGEM.toString(), prescricaoEnfermagem));
		if(indPendente != null){
			criteria.add(Restrictions.eq(EpeHistoricoPrescDiagnosticos.Fields.IND_PENDENTE.toString(), indPendente));
		}
		
		return (EpeHistoricoPrescDiagnosticos)executeCriteriaUniqueResult(criteria);
	}
	
	public List<EpeHistoricoPrescDiagnosticos> listarEpeHistPrescDiagPorPrescSituacao(
			EpePrescricaoEnfermagem prescricaoEnfermagem, Boolean indPendente) {
		DetachedCriteria criteria = DetachedCriteria.forClass(EpeHistoricoPrescDiagnosticos.class);
		
		criteria.add(Restrictions.eq(EpeHistoricoPrescDiagnosticos.Fields.PRESCRICAO_ENFERMAGEM.toString(), prescricaoEnfermagem));
		if(indPendente != null){
			criteria.add(Restrictions.eq(EpeHistoricoPrescDiagnosticos.Fields.IND_PENDENTE.toString(), indPendente));
		}
		
		return executeCriteria(criteria);
	}

}