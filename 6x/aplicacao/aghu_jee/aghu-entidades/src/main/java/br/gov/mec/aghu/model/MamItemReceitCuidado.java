package br.gov.mec.aghu.model;

// Generated 09/04/2012 16:32:53 by Hibernate Tools 3.4.0.CR1

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;


import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.core.persistence.BaseEntityId;

/**
 * MamItemReceitCuidado generated by hbm2java
 */
@Entity
@Table(name = "MAM_ITEM_RECEIT_CUIDADOS", schema = "AGH")
public class MamItemReceitCuidado extends BaseEntityId<MamItemReceitCuidadoId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7113020246685917671L;
	private MamItemReceitCuidadoId id;
	private Integer version;
	private MamReceituarioCuidado mamReceituarioCuidado;
	private String descricao;

	public MamItemReceitCuidado() {
	}

	public MamItemReceitCuidado(MamItemReceitCuidadoId id, MamReceituarioCuidado mamReceituarioCuidado, String descricao) {
		this.id = id;
		this.mamReceituarioCuidado = mamReceituarioCuidado;
		this.descricao = descricao;
	}

	@EmbeddedId
	@AttributeOverrides({ @AttributeOverride(name = "rcuSeq", column = @Column(name = "RCU_SEQ", nullable = false)),
			@AttributeOverride(name = "seqp", column = @Column(name = "SEQP", nullable = false)) })
	public MamItemReceitCuidadoId getId() {
		return this.id;
	}

	public void setId(MamItemReceitCuidadoId id) {
		this.id = id;
	}

	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "RCU_SEQ", nullable = false, insertable = false, updatable = false)
	public MamReceituarioCuidado getMamReceituarioCuidado() {
		return this.mamReceituarioCuidado;
	}

	public void setMamReceituarioCuidado(MamReceituarioCuidado mamReceituarioCuidado) {
		this.mamReceituarioCuidado = mamReceituarioCuidado;
	}

	@Column(name = "DESCRICAO", nullable = false, length = 240)
	@Length(max = 240)
	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public enum Fields {

		ID("id"),
		SEQP("id.seqp"),
		RCU_SEQ("id.rcuSeq"),
		VERSION("version"),
		MAM_RECEITUARIO_CUIDADO("mamReceituarioCuidado"),
		DESCRICAO("descricao");

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
		if (!(obj instanceof MamItemReceitCuidado)) {
			return false;
		}
		MamItemReceitCuidado other = (MamItemReceitCuidado) obj;
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
