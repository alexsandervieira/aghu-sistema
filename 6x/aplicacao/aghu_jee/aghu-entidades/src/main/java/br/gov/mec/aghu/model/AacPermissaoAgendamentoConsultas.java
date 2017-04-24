package br.gov.mec.aghu.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;

import br.gov.mec.aghu.core.persistence.BaseEntitySeq;
import br.gov.mec.aghu.dominio.DominioSituacao;

@Entity
@Table(name = "AAC_PERMISSAO_AGENDAMENTO_CONSULTAS", schema = "AGH") 
@SequenceGenerator(name = "aghPermAgendConsSq1", sequenceName = "AGH.AAC_PERM_AGEND_CONS_SQ1", allocationSize = 1)
public class AacPermissaoAgendamentoConsultas extends BaseEntitySeq<Integer> implements java.io.Serializable {

	private static final long serialVersionUID = -2399514584271698321L;

	private Integer seq;
	private RapServidores servidor;
	private AghEspecialidades especialidade;
	private AghEquipes equipe;
	private AacGradeAgendamenConsultas grade;
	private AghUnidadesFuncionais unidadeFuncional;
	private RapServidores profissional;
	private Date criadoEm;
	private DominioSituacao indSituacao;
	private Integer version;
	
	private Integer qtdPermissoes;
	
	@Transient
	public Integer getQtdPermissoes() {
		return qtdPermissoes;
	}

	public void setQtdPermissoes(Integer qtdPermissoes) {
		this.qtdPermissoes = qtdPermissoes;
	}

	public AacPermissaoAgendamentoConsultas() {
	}

	public AacPermissaoAgendamentoConsultas(Integer seq) {
		this.seq = seq;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "aghPermAgendConsSq1")
	@Column(name = "SEQ", nullable = false)
	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = false),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	public RapServidores getServidor() {
		return servidor;
	}
	
	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ESP_SEQ", nullable = true)
	public AghEspecialidades getEspecialidade() {
		return especialidade;
	}

	public void setEspecialidade(AghEspecialidades especialidade) {
		this.especialidade = especialidade;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "EQP_SEQ", nullable = true)
	public AghEquipes getEquipe() {
		return equipe;
	}

	public void setEquipe(AghEquipes equipe) {
		this.equipe = equipe;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "GRD_SEQ", nullable = true)
	public AacGradeAgendamenConsultas getGrade() {
		return grade;
	}

	public void setGrade(AacGradeAgendamenConsultas grade) {
		this.grade = grade;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="UNF_SEQ", nullable = true)
	public AghUnidadesFuncionais getUnidadeFuncional() {
		return unidadeFuncional;
	}

	public void setUnidadeFuncional(AghUnidadesFuncionais unidadeFuncional) {
		this.unidadeFuncional = unidadeFuncional;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "PROF_SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = true),
			@JoinColumn(name = "PROF_SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = true) })
	public RapServidores getProfissional() {
		return profissional;
	}

	public void setProfissional(RapServidores profissional) {
		this.profissional = profissional;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 7)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Column(name = "IND_SITUACAO",nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getIndSituacao() {
		return indSituacao;
	}
	
	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}

	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public enum Fields {
		
		SEQ("seq"),
		SERVIDOR("servidor"),
		ESPECIALIDADE("especialidade"),
		EQUIPE("equipe"),
		GRADE("grade"),
		UNIDADE_FUNCIONAL("unidadeFuncional"),
		PROFISSIONAL("profissional"),
		SEQ_ESPECIALIDADE("especialidade.seq"),
		SEQ_EQUIPE("equipe.seq"),
		SEQ_GRADE("grade.seq"),
		SEQ_UNF("unidadeFuncional.seq"),
		ID_PROFISSIONAL("profissional.id");
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
		
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((seq == null) ? 0 : seq.hashCode());
		result = prime * result
				+ ((servidor == null) ? 0 : servidor.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (!(obj instanceof AacPermissaoAgendamentoConsultas)) {
			return false;
		}
		AacPermissaoAgendamentoConsultas other = (AacPermissaoAgendamentoConsultas) obj;
		if (seq == null) {
			if (other.seq != null) {
				return false;
			}
		} else if (!seq.equals(other.seq)) {
			return false;
		}
		if (servidor == null) {
			if (other.servidor != null) {
				return false;
			}
		} else if (!servidor.equals(other.servidor)) {
			return false;
		}
		return true;
	}


}
