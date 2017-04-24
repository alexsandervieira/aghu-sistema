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
import javax.persistence.Version;

import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.core.persistence.BaseEntitySeq;
import br.gov.mec.aghu.dominio.DominioSituacaoHistPrescDiagnosticos;

@Entity
@SequenceGenerator(name="epeHpdSeq1", sequenceName="AGH.EPE_HPD_SQ1", allocationSize = 1)
@Table(name = "EPE_HISTORICO_PRESC_DIAGNOSTICOS", schema = "AGH")
public class EpeHistoricoPrescDiagnosticos extends BaseEntitySeq<Integer> implements java.io.Serializable {

	private static final long serialVersionUID = -3572192431752934208L;

	private Integer seq;
	private RapServidores servidor;
	private DominioSituacaoHistPrescDiagnosticos situacao;
	private Date criadoEm;
	private String descricao;
	private EpeDiagnostico diagnostico;
	private Integer version;
	private EpePrescricaoEnfermagem prescricaoEnfermagem;
	private AghAtendimentos atendimento;
	private Boolean indPendente;
	private EpeFatRelacionado fatRelacionado;
	private EpeFatRelDiagnostico fatRelDiagnostico;
	
	public EpeHistoricoPrescDiagnosticos() {
	}

	public EpeHistoricoPrescDiagnosticos(Integer seq) {
		this.seq = seq;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "epeHpdSeq1")
	@Column(name = "SEQ", nullable = false, precision = 9, scale = 0)
	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = false),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	public RapServidores getServidor() {
		return this.servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	@Column(name = "IND_SITUACAO", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacaoHistPrescDiagnosticos getSituacao() {
		return this.situacao;
	}

	public void setSituacao(DominioSituacaoHistPrescDiagnosticos situacao) {
		this.situacao = situacao;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 7)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Column(name = "DESCRICAO", length = 120)
	@Length(max = 120)
	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "PEN_ATD_SEQ", referencedColumnName = "ATD_SEQ", nullable = false),
			@JoinColumn(name = "PEN_SEQ", referencedColumnName = "SEQ", nullable = false) })
	public EpePrescricaoEnfermagem getPrescricaoEnfermagem() {
		return prescricaoEnfermagem;
	}

	public void setPrescricaoEnfermagem(EpePrescricaoEnfermagem prescricaoEnfermagem) {
		this.prescricaoEnfermagem = prescricaoEnfermagem;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "DGN_SNB_GNB_SEQ", referencedColumnName = "SNB_GNB_SEQ", nullable = false),
			@JoinColumn(name = "DGN_SNB_SEQUENCIA", referencedColumnName = "SNB_SEQUENCIA", nullable = false),
			@JoinColumn(name = "DGN_SEQUENCIA", referencedColumnName = "SEQUENCIA", nullable = false) })
	public EpeDiagnostico getDiagnostico() {
		return this.diagnostico;
	}

	public void setDiagnostico(EpeDiagnostico diagnostico) {
		this.diagnostico = diagnostico;
	}

	@Version
	@Column(name = "VERSION", nullable = false, precision = 9, scale = 0)
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PEN_ATD_SEQ", insertable=false, updatable=false)
	public AghAtendimentos getAtendimento() {
		return atendimento;
	}

	public void setAtendimento(AghAtendimentos atendimento) {
		this.atendimento = atendimento;
	}

	@Column(name = "IND_PENDENTE", nullable = false, length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndPendente() {
		return this.indPendente;
	}

	public void setIndPendente(Boolean indPendente) {
		this.indPendente = indPendente;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "FRE_SEQ", referencedColumnName = "SEQ", nullable = false)
	public EpeFatRelacionado getFatRelacionado() {
		return fatRelacionado;
	}

	public void setFatRelacionado(EpeFatRelacionado fatRelacionado) {
		this.fatRelacionado = fatRelacionado;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "DGN_SNB_GNB_SEQ", referencedColumnName = "DGN_SNB_GNB_SEQ", nullable = false, insertable = false, updatable = false),
			@JoinColumn(name = "DGN_SNB_SEQUENCIA", referencedColumnName = "DGN_SNB_SEQUENCIA", nullable = false, insertable = false, updatable = false),
			@JoinColumn(name = "DGN_SEQUENCIA", referencedColumnName = "DGN_SEQUENCIA", nullable = false, insertable = false, updatable = false),
			@JoinColumn(name = "FRE_SEQ", referencedColumnName = "FRE_SEQ", nullable = false, insertable = false, updatable = false) })
	public EpeFatRelDiagnostico getFatRelDiagnostico() {
		return this.fatRelDiagnostico;
	}

	public void setFatRelDiagnostico(
			EpeFatRelDiagnostico fatRelDiagnostico) {
		this.fatRelDiagnostico = fatRelDiagnostico;
	}

	public enum Fields {
		SEQ("id.seq"),
		PEN_ID("prescricaoEnfermagem.id"),
		PEN_SEQ("prescricaoEnfermagem.id.seq"),
		PEN_ATD_SEQ("prescricaoEnfermagem.id.atdSeq"),
		CRIADO_EM("criadoEm"),
		DIAGNOSTICO("diagnostico"),
		DIAGNOSTICO_ID("diagnostico.id"),
		IND_SITUACAO("situacao"),
		PRESCRICAO_ENFERMAGEM("prescricaoEnfermagem"),
		DESCRICAO("descricao"),
		ATENDIMENTO("atendimento"),
		ATENDIMENTO_SEQ("atendimento.seq"),
		IND_PENDENTE("indPendente"),
		FAT_REL_DIAGNOSTICO("fatRelDiagnostico"),
		SERVIDOR("servidor");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}

}
