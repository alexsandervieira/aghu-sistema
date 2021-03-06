package br.gov.mec.aghu.model;

// Generated 10/03/2011 17:20:24 by Hibernate Tools 3.4.0.CR1

import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;


import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.persistence.BaseEntityId;

/**
 * AelSinonimosExames generated by hbm2java
 */

@Entity
@Table(name = "AEL_SINONIMOS_EXAMES", schema = "AGH", uniqueConstraints = @UniqueConstraint(columnNames = "NOME"))
public class AelSinonimoExame extends BaseEntityId<AelSinonimoExameId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1478560047139571611L;
	private AelSinonimoExameId id;
	private String nome;
	private DominioSituacao indSituacao;
	private Date criadoEm;
	private RapServidores rapServidor;
	private AelExames exame;

	public AelSinonimoExame() {
	}

	public AelSinonimoExame(AelSinonimoExameId id, String nome,
			DominioSituacao indSituacao, Date criadoEm, RapServidores rapServidor) {
		this.id = id;
		this.nome = nome;
		this.indSituacao = indSituacao;
		this.criadoEm = criadoEm;
		this.rapServidor = rapServidor;
	}

	@EmbeddedId
	@AttributeOverrides({
		@AttributeOverride(name = "exaSigla", column = @Column(name = "EXA_SIGLA", nullable = false, length = 5)),
		@AttributeOverride(name = "seqp", column = @Column(name = "SEQP", nullable = false, precision = 3, scale = 0)) })
		public AelSinonimoExameId getId() {
		return this.id;
	}

	public void setId(AelSinonimoExameId id) {
		this.id = id;
	}

	@Column(name = "NOME", unique = true, nullable = false, length = 100)
	@Length(max = 100)
	public String getNome() {
		return this.nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	@Column(name = "IND_SITUACAO", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getIndSituacao() {
		return this.indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 7)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@JoinColumns( {
		@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = false),
		@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = false) })
		@ManyToOne(optional = false, fetch = FetchType.LAZY)
	public RapServidores getRapServidor() {
		return this.rapServidor;
	}

	public void setRapServidor(RapServidores rapServidor) {
		this.rapServidor = rapServidor;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "EXA_SIGLA", insertable = false, updatable = false)	
	public AelExames getExame() {
		return exame;
	}
	
	public void setExame(AelExames exame) {
		this.exame = exame;
	}


	public static enum Fields {
		EXA_SIGLA("id.exaSigla")
		, SEQ_P("id.seqp")
		, NOME("nome")
		, IND_SITUACAO("indSituacao")
		;

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}

	// ##### GeradorEqualsHashCodeMain #####
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof AelSinonimoExame)) {
			return false;
		}
		AelSinonimoExame other = (AelSinonimoExame) obj;
		if (getId() == null) {
			if (other.getId() != null) {
				return false;
			}
		} else if (!getId().equals(other.getId())) {
			return false;
		}
		return true;
	}
	// ##### GeradorEqualsHashCodeMain #####

}
