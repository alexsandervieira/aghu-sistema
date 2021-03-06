package br.gov.mec.aghu.registrocolaborador.dao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.LongType;
import org.hibernate.type.Type;

import br.gov.mec.aghu.model.RapUniServPlano;
import br.gov.mec.aghu.core.persistence.dao.QueryBuilder;

public class CursorVerifServUniMatriculaQueryBuilder extends QueryBuilder<DetachedCriteria> {
	/**
	 * cursor c_ser da procedure RAPC_VESETEM_UNIMED Incluido pela estória #42229
	 * 
	 * @author rodrigo.saraujo
	 */
	private static final String ASPAS_SIMPLES = "'";
	private static final long serialVersionUID = 6685078910226941904L;
	private DetachedCriteria criteria;
	private Integer matricula;
	private Short vinCodigo;
	private String data;
	private boolean isOracle;

	@Override
	protected DetachedCriteria createProduct() {
		return DetachedCriteria.forClass(RapUniServPlano.class, "RSP");
	}

	@Override
	protected void doBuild(DetachedCriteria criteria) {
		this.criteria = criteria;
		setFiltro();
		setProjecao();

	}

	private void setFiltro() {
		criteria.add(Restrictions.eq("RSP." + RapUniServPlano.Fields.SAD_SER_MATRICULA.toString(), this.matricula));
		criteria.add(Restrictions.eq("RSP." + RapUniServPlano.Fields.SAD_SER_VIN_CODIGO.toString(), this.vinCodigo));
		StringBuilder restricao = new StringBuilder(500);
		restricao.append(ASPAS_SIMPLES).append(this.data).append("'  between {alias}.dt_inicio");
		if (this.isOracle) {
			restricao.append(" and NVL(");
		} else {
			restricao.append(" and COALESCE(");
		}
		restricao.append("{alias}.dt_fim,'").append(this.data).append("')");
		criteria.add(Restrictions.sqlRestriction(restricao.toString()));

	}

	private void setProjecao() {
		ProjectionList projList = Projections.projectionList();
		StringBuilder nroCarteira = new StringBuilder(100);
		if (this.isOracle) {
			nroCarteira.append(" NVL({alias}.NRO_CARTEIRA,0) nroCarteira");
		} else {
			nroCarteira.append(" COALESCE({alias}.NRO_CARTEIRA,0) nroCarteira");
		}
		projList.add(Projections.sqlProjection(nroCarteira.toString(), new String[] { "nroCarteira" }, new Type[] { new LongType() }));
		criteria.setProjection(projList);
	}

	public DetachedCriteria build(Integer matricula, Short vinCodigo, String data, Boolean isOracle) {
		this.matricula = matricula;
		this.vinCodigo = vinCodigo;
		this.data = data;
		this.isOracle = isOracle;
		return super.build();
	}
}
