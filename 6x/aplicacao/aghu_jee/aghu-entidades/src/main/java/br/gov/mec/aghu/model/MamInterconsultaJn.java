package br.gov.mec.aghu.model;

// Generated 09/04/2012 16:32:53 by Hibernate Tools 3.4.0.CR1

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


import org.hibernate.annotations.Immutable;
import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.core.model.BaseJournal;

/**
 * MamInterconsultaJn generated by hbm2java
 */
@Entity
@SequenceGenerator(name="mamIeoJnSeq", sequenceName="AGH.MAM_IEO_JN_SEQ", allocationSize = 1)
@Table(name = "MAM_INTERCONSULTAS_JN", schema = "AGH")
@Immutable
public class MamInterconsultaJn extends BaseJournal {

/**
	 * 
	 */
	private static final long serialVersionUID = 4994370838798255708L;
	/* ATUALIZADOR JOURNALS - PROPERTIES	private Long seqJn;
	private String jnUser;
	private Date jnDateTime;
	private String jnOperation;*/
	private Long seq;
	private String observacao;
	private Date dthrCriacao;
	private Date dthrValida;
	private Date dthrMvto;
	private Date dthrValidaMvto;
	private String indPendente;
	private Integer pacCodigo;
	private Integer conNumero;
	private Short espSeq;
	private Integer serMatricula;
	private Short serVinCodigo;
	private Integer serMatriculaValida;
	private Short serVinCodigoValida;
	private Integer serMatriculaMvto;
	private Short serVinCodigoMvto;
	private Integer serMatriculaValidaMvto;
	private Short serVinCodigoValidaMvto;
	private Long ieoSeq;
	private Short eqpSeq;
	private Integer serMatriculaResponsavel;
	private Short serVinCodigoResponsavel;
	private String indImpresso;
	private String situacao;
	private Integer conNumeroMarcada;
	private String indDigitado;
	private Short espSeqAgenda;
	private String observacaoAdicional;
	private Short espSeqAdm;
	private Date dthrMarcada;
	private Date dthrAvisada;
	private Integer serMatriculaMarcada;
	private Short serVinCodigoMarcada;
	private Integer serMatriculaAvisada;
	private Short serVinCodigoAvisada;
	private String indUrgente;
	private String indCanceladoObito;
	private String avaliacao;
	private Integer serMatriculaAvalia;
	private Short serVinCodigoAvalia;
	private Date dthrAvaliacao;
	private String prioridade;
	private String tipoSolicitacao;
	private String prioridadeAprovada;
	private String parecerConsultor;
	private Date dthrConhecimentoConsultor;
	private Integer serMatriculaConhecimento;
	private Short serVinCodigoConhecimento;
	private Date dthrVisualizaResposta;
	private Integer serMatriculaVisualiza;
	private Short serVinCodigoVisualiza;
	private Integer conNumeroRetorno;
	private Date dthrMarcacaoRetorno;
	private Integer serMatriculaRetorno;
	private Short serVinCodigoRetorno;
	private Date dthrAvisaRetorno;
	private Integer serMatriculaAvisaRetorno;
	private Integer serVinCodigoAvisaRetorno;

	public MamInterconsultaJn() {
	}

	public MamInterconsultaJn(Long seqJn, String jnUser, Date jnDateTime, String jnOperation, Long seq) {
	/* ATUALIZADOR JOURNALS - contrutor	this.seqJn = seqJn;
		this.jnUser = jnUser;
		this.jnDateTime = jnDateTime;
		this.jnOperation = jnOperation;*/
		this.seq = seq;
	}

	@SuppressWarnings({"PMD.ExcessiveParameterList"})
	public MamInterconsultaJn(Long seqJn, String jnUser, Date jnDateTime, String jnOperation, Long seq, String observacao,
			Date dthrCriacao, Date dthrValida, Date dthrMvto, Date dthrValidaMvto, String indPendente, Integer pacCodigo,
			Integer conNumero, Short espSeq, Integer serMatricula, Short serVinCodigo, Integer serMatriculaValida,
			Short serVinCodigoValida, Integer serMatriculaMvto, Short serVinCodigoMvto, Integer serMatriculaValidaMvto,
			Short serVinCodigoValidaMvto, Long ieoSeq, Short eqpSeq, Integer serMatriculaResponsavel, Short serVinCodigoResponsavel,
			String indImpresso, String situacao, Integer conNumeroMarcada, String indDigitado, Short espSeqAgenda,
			String observacaoAdicional, Short espSeqAdm, Date dthrMarcada, Date dthrAvisada, Integer serMatriculaMarcada,
			Short serVinCodigoMarcada, Integer serMatriculaAvisada, Short serVinCodigoAvisada, String indUrgente,
			String indCanceladoObito, String avaliacao, Integer serMatriculaAvalia, Short serVinCodigoAvalia, Date dthrAvaliacao,
			String prioridade, String tipoSolicitacao, String prioridadeAprovada, String parecerConsultor,
			Date dthrConhecimentoConsultor, Integer serMatriculaConhecimento, Short serVinCodigoConhecimento,
			Date dthrVisualizaResposta, Integer serMatriculaVisualiza, Short serVinCodigoVisualiza, Integer conNumeroRetorno,
			Date dthrMarcacaoRetorno, Integer serMatriculaRetorno, Short serVinCodigoRetorno, Date dthrAvisaRetorno,
			Integer serMatriculaAvisaRetorno, Integer serVinCodigoAvisaRetorno) {
	/* ATUALIZADOR JOURNALS - contrutor	this.seqJn = seqJn;
		this.jnUser = jnUser;
		this.jnDateTime = jnDateTime;
		this.jnOperation = jnOperation;*/
		this.seq = seq;
		this.observacao = observacao;
		this.dthrCriacao = dthrCriacao;
		this.dthrValida = dthrValida;
		this.dthrMvto = dthrMvto;
		this.dthrValidaMvto = dthrValidaMvto;
		this.indPendente = indPendente;
		this.pacCodigo = pacCodigo;
		this.conNumero = conNumero;
		this.espSeq = espSeq;
		this.serMatricula = serMatricula;
		this.serVinCodigo = serVinCodigo;
		this.serMatriculaValida = serMatriculaValida;
		this.serVinCodigoValida = serVinCodigoValida;
		this.serMatriculaMvto = serMatriculaMvto;
		this.serVinCodigoMvto = serVinCodigoMvto;
		this.serMatriculaValidaMvto = serMatriculaValidaMvto;
		this.serVinCodigoValidaMvto = serVinCodigoValidaMvto;
		this.ieoSeq = ieoSeq;
		this.eqpSeq = eqpSeq;
		this.serMatriculaResponsavel = serMatriculaResponsavel;
		this.serVinCodigoResponsavel = serVinCodigoResponsavel;
		this.indImpresso = indImpresso;
		this.situacao = situacao;
		this.conNumeroMarcada = conNumeroMarcada;
		this.indDigitado = indDigitado;
		this.espSeqAgenda = espSeqAgenda;
		this.observacaoAdicional = observacaoAdicional;
		this.espSeqAdm = espSeqAdm;
		this.dthrMarcada = dthrMarcada;
		this.dthrAvisada = dthrAvisada;
		this.serMatriculaMarcada = serMatriculaMarcada;
		this.serVinCodigoMarcada = serVinCodigoMarcada;
		this.serMatriculaAvisada = serMatriculaAvisada;
		this.serVinCodigoAvisada = serVinCodigoAvisada;
		this.indUrgente = indUrgente;
		this.indCanceladoObito = indCanceladoObito;
		this.avaliacao = avaliacao;
		this.serMatriculaAvalia = serMatriculaAvalia;
		this.serVinCodigoAvalia = serVinCodigoAvalia;
		this.dthrAvaliacao = dthrAvaliacao;
		this.prioridade = prioridade;
		this.tipoSolicitacao = tipoSolicitacao;
		this.prioridadeAprovada = prioridadeAprovada;
		this.parecerConsultor = parecerConsultor;
		this.dthrConhecimentoConsultor = dthrConhecimentoConsultor;
		this.serMatriculaConhecimento = serMatriculaConhecimento;
		this.serVinCodigoConhecimento = serVinCodigoConhecimento;
		this.dthrVisualizaResposta = dthrVisualizaResposta;
		this.serMatriculaVisualiza = serMatriculaVisualiza;
		this.serVinCodigoVisualiza = serVinCodigoVisualiza;
		this.conNumeroRetorno = conNumeroRetorno;
		this.dthrMarcacaoRetorno = dthrMarcacaoRetorno;
		this.serMatriculaRetorno = serMatriculaRetorno;
		this.serVinCodigoRetorno = serVinCodigoRetorno;
		this.dthrAvisaRetorno = dthrAvisaRetorno;
		this.serMatriculaAvisaRetorno = serMatriculaAvisaRetorno;
		this.serVinCodigoAvisaRetorno = serVinCodigoAvisaRetorno;
	}

	// ATUALIZADOR JOURNALS - ID
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "mamIeoJnSeq")
	@Column(name = "SEQ_JN", unique = true, nullable = false)
	//@GeneratedValue(strategy = GenerationType.AUTO, generator = "")
	@Override
	public Integer getSeqJn() {
		return super.getSeqJn();
	}
	// ATUALIZADOR JOURNALS - ID
	
/* ATUALIZADOR JOURNALS - Get / Set	@Id
	@Column(name = "SEQ_JN", unique = true, nullable = false)
	public Long getSeqJn() {
		return this.seqJn;
	}

	public void setSeqJn(Long seqJn) {
		this.seqJn = seqJn;
	}

	@Column(name = "JN_USER", nullable = false, length = 30)
	@Length(max = 30)
	public String getJnUser() {
		return this.jnUser;
	}

	public void setJnUser(String jnUser) {
		this.jnUser = jnUser;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "JN_DATE_TIME", nullable = false, length = 29)
	public Date getJnDateTime() {
		return this.jnDateTime;
	}

	public void setJnDateTime(Date jnDateTime) {
		this.jnDateTime = jnDateTime;
	}

	@Column(name = "JN_OPERATION", nullable = false, length = 3)
	@Length(max = 3)
	public String getJnOperation() {
		return this.jnOperation;
	}

	public void setJnOperation(String jnOperation) {
		this.jnOperation = jnOperation;
	}*/

	@Column(name = "SEQ", nullable = false)
	public Long getSeq() {
		return this.seq;
	}

	public void setSeq(Long seq) {
		this.seq = seq;
	}

	@Column(name = "OBSERVACAO", length = 1000)
	@Length(max = 1000)
	public String getObservacao() {
		return this.observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DTHR_CRIACAO", length = 29)
	public Date getDthrCriacao() {
		return this.dthrCriacao;
	}

	public void setDthrCriacao(Date dthrCriacao) {
		this.dthrCriacao = dthrCriacao;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DTHR_VALIDA", length = 29)
	public Date getDthrValida() {
		return this.dthrValida;
	}

	public void setDthrValida(Date dthrValida) {
		this.dthrValida = dthrValida;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DTHR_MVTO", length = 29)
	public Date getDthrMvto() {
		return this.dthrMvto;
	}

	public void setDthrMvto(Date dthrMvto) {
		this.dthrMvto = dthrMvto;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DTHR_VALIDA_MVTO", length = 29)
	public Date getDthrValidaMvto() {
		return this.dthrValidaMvto;
	}

	public void setDthrValidaMvto(Date dthrValidaMvto) {
		this.dthrValidaMvto = dthrValidaMvto;
	}

	@Column(name = "IND_PENDENTE", length = 1)
	@Length(max = 1)
	public String getIndPendente() {
		return this.indPendente;
	}

	public void setIndPendente(String indPendente) {
		this.indPendente = indPendente;
	}

	@Column(name = "PAC_CODIGO")
	public Integer getPacCodigo() {
		return this.pacCodigo;
	}

	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}

	@Column(name = "CON_NUMERO")
	public Integer getConNumero() {
		return this.conNumero;
	}

	public void setConNumero(Integer conNumero) {
		this.conNumero = conNumero;
	}

	@Column(name = "ESP_SEQ")
	public Short getEspSeq() {
		return this.espSeq;
	}

	public void setEspSeq(Short espSeq) {
		this.espSeq = espSeq;
	}

	@Column(name = "SER_MATRICULA")
	public Integer getSerMatricula() {
		return this.serMatricula;
	}

	public void setSerMatricula(Integer serMatricula) {
		this.serMatricula = serMatricula;
	}

	@Column(name = "SER_VIN_CODIGO")
	public Short getSerVinCodigo() {
		return this.serVinCodigo;
	}

	public void setSerVinCodigo(Short serVinCodigo) {
		this.serVinCodigo = serVinCodigo;
	}

	@Column(name = "SER_MATRICULA_VALIDA")
	public Integer getSerMatriculaValida() {
		return this.serMatriculaValida;
	}

	public void setSerMatriculaValida(Integer serMatriculaValida) {
		this.serMatriculaValida = serMatriculaValida;
	}

	@Column(name = "SER_VIN_CODIGO_VALIDA")
	public Short getSerVinCodigoValida() {
		return this.serVinCodigoValida;
	}

	public void setSerVinCodigoValida(Short serVinCodigoValida) {
		this.serVinCodigoValida = serVinCodigoValida;
	}

	@Column(name = "SER_MATRICULA_MVTO")
	public Integer getSerMatriculaMvto() {
		return this.serMatriculaMvto;
	}

	public void setSerMatriculaMvto(Integer serMatriculaMvto) {
		this.serMatriculaMvto = serMatriculaMvto;
	}

	@Column(name = "SER_VIN_CODIGO_MVTO")
	public Short getSerVinCodigoMvto() {
		return this.serVinCodigoMvto;
	}

	public void setSerVinCodigoMvto(Short serVinCodigoMvto) {
		this.serVinCodigoMvto = serVinCodigoMvto;
	}

	@Column(name = "SER_MATRICULA_VALIDA_MVTO")
	public Integer getSerMatriculaValidaMvto() {
		return this.serMatriculaValidaMvto;
	}

	public void setSerMatriculaValidaMvto(Integer serMatriculaValidaMvto) {
		this.serMatriculaValidaMvto = serMatriculaValidaMvto;
	}

	@Column(name = "SER_VIN_CODIGO_VALIDA_MVTO")
	public Short getSerVinCodigoValidaMvto() {
		return this.serVinCodigoValidaMvto;
	}

	public void setSerVinCodigoValidaMvto(Short serVinCodigoValidaMvto) {
		this.serVinCodigoValidaMvto = serVinCodigoValidaMvto;
	}

	@Column(name = "IEO_SEQ")
	public Long getIeoSeq() {
		return this.ieoSeq;
	}

	public void setIeoSeq(Long ieoSeq) {
		this.ieoSeq = ieoSeq;
	}

	@Column(name = "EQP_SEQ")
	public Short getEqpSeq() {
		return this.eqpSeq;
	}

	public void setEqpSeq(Short eqpSeq) {
		this.eqpSeq = eqpSeq;
	}

	@Column(name = "SER_MATRICULA_RESPONSAVEL")
	public Integer getSerMatriculaResponsavel() {
		return this.serMatriculaResponsavel;
	}

	public void setSerMatriculaResponsavel(Integer serMatriculaResponsavel) {
		this.serMatriculaResponsavel = serMatriculaResponsavel;
	}

	@Column(name = "SER_VIN_CODIGO_RESPONSAVEL")
	public Short getSerVinCodigoResponsavel() {
		return this.serVinCodigoResponsavel;
	}

	public void setSerVinCodigoResponsavel(Short serVinCodigoResponsavel) {
		this.serVinCodigoResponsavel = serVinCodigoResponsavel;
	}

	@Column(name = "IND_IMPRESSO", length = 1)
	@Length(max = 1)
	public String getIndImpresso() {
		return this.indImpresso;
	}

	public void setIndImpresso(String indImpresso) {
		this.indImpresso = indImpresso;
	}

	@Column(name = "SITUACAO", length = 1)
	@Length(max = 1)
	public String getSituacao() {
		return this.situacao;
	}

	public void setSituacao(String situacao) {
		this.situacao = situacao;
	}

	@Column(name = "CON_NUMERO_MARCADA")
	public Integer getConNumeroMarcada() {
		return this.conNumeroMarcada;
	}

	public void setConNumeroMarcada(Integer conNumeroMarcada) {
		this.conNumeroMarcada = conNumeroMarcada;
	}

	@Column(name = "IND_DIGITADO", length = 1)
	@Length(max = 1)
	public String getIndDigitado() {
		return this.indDigitado;
	}

	public void setIndDigitado(String indDigitado) {
		this.indDigitado = indDigitado;
	}

	@Column(name = "ESP_SEQ_AGENDA")
	public Short getEspSeqAgenda() {
		return this.espSeqAgenda;
	}

	public void setEspSeqAgenda(Short espSeqAgenda) {
		this.espSeqAgenda = espSeqAgenda;
	}

	@Column(name = "OBSERVACAO_ADICIONAL", length = 2000)
	@Length(max = 2000)
	public String getObservacaoAdicional() {
		return this.observacaoAdicional;
	}

	public void setObservacaoAdicional(String observacaoAdicional) {
		this.observacaoAdicional = observacaoAdicional;
	}

	@Column(name = "ESP_SEQ_ADM")
	public Short getEspSeqAdm() {
		return this.espSeqAdm;
	}

	public void setEspSeqAdm(Short espSeqAdm) {
		this.espSeqAdm = espSeqAdm;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DTHR_MARCADA", length = 29)
	public Date getDthrMarcada() {
		return this.dthrMarcada;
	}

	public void setDthrMarcada(Date dthrMarcada) {
		this.dthrMarcada = dthrMarcada;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DTHR_AVISADA", length = 29)
	public Date getDthrAvisada() {
		return this.dthrAvisada;
	}

	public void setDthrAvisada(Date dthrAvisada) {
		this.dthrAvisada = dthrAvisada;
	}

	@Column(name = "SER_MATRICULA_MARCADA")
	public Integer getSerMatriculaMarcada() {
		return this.serMatriculaMarcada;
	}

	public void setSerMatriculaMarcada(Integer serMatriculaMarcada) {
		this.serMatriculaMarcada = serMatriculaMarcada;
	}

	@Column(name = "SER_VIN_CODIGO_MARCADA")
	public Short getSerVinCodigoMarcada() {
		return this.serVinCodigoMarcada;
	}

	public void setSerVinCodigoMarcada(Short serVinCodigoMarcada) {
		this.serVinCodigoMarcada = serVinCodigoMarcada;
	}

	@Column(name = "SER_MATRICULA_AVISADA")
	public Integer getSerMatriculaAvisada() {
		return this.serMatriculaAvisada;
	}

	public void setSerMatriculaAvisada(Integer serMatriculaAvisada) {
		this.serMatriculaAvisada = serMatriculaAvisada;
	}

	@Column(name = "SER_VIN_CODIGO_AVISADA")
	public Short getSerVinCodigoAvisada() {
		return this.serVinCodigoAvisada;
	}

	public void setSerVinCodigoAvisada(Short serVinCodigoAvisada) {
		this.serVinCodigoAvisada = serVinCodigoAvisada;
	}

	@Column(name = "IND_URGENTE", length = 1)
	@Length(max = 1)
	public String getIndUrgente() {
		return this.indUrgente;
	}

	public void setIndUrgente(String indUrgente) {
		this.indUrgente = indUrgente;
	}

	@Column(name = "IND_CANCELADO_OBITO", length = 1)
	@Length(max = 1)
	public String getIndCanceladoObito() {
		return this.indCanceladoObito;
	}

	public void setIndCanceladoObito(String indCanceladoObito) {
		this.indCanceladoObito = indCanceladoObito;
	}

	@Column(name = "AVALIACAO", length = 1)
	@Length(max = 1)
	public String getAvaliacao() {
		return this.avaliacao;
	}

	public void setAvaliacao(String avaliacao) {
		this.avaliacao = avaliacao;
	}

	@Column(name = "SER_MATRICULA_AVALIA")
	public Integer getSerMatriculaAvalia() {
		return this.serMatriculaAvalia;
	}

	public void setSerMatriculaAvalia(Integer serMatriculaAvalia) {
		this.serMatriculaAvalia = serMatriculaAvalia;
	}

	@Column(name = "SER_VIN_CODIGO_AVALIA")
	public Short getSerVinCodigoAvalia() {
		return this.serVinCodigoAvalia;
	}

	public void setSerVinCodigoAvalia(Short serVinCodigoAvalia) {
		this.serVinCodigoAvalia = serVinCodigoAvalia;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DTHR_AVALIACAO", length = 29)
	public Date getDthrAvaliacao() {
		return this.dthrAvaliacao;
	}

	public void setDthrAvaliacao(Date dthrAvaliacao) {
		this.dthrAvaliacao = dthrAvaliacao;
	}

	@Column(name = "PRIORIDADE", length = 1)
	@Length(max = 1)
	public String getPrioridade() {
		return this.prioridade;
	}

	public void setPrioridade(String prioridade) {
		this.prioridade = prioridade;
	}

	@Column(name = "TIPO_SOLICITACAO", length = 1)
	@Length(max = 1)
	public String getTipoSolicitacao() {
		return this.tipoSolicitacao;
	}

	public void setTipoSolicitacao(String tipoSolicitacao) {
		this.tipoSolicitacao = tipoSolicitacao;
	}

	@Column(name = "PRIORIDADE_APROVADA", length = 1)
	@Length(max = 1)
	public String getPrioridadeAprovada() {
		return this.prioridadeAprovada;
	}

	public void setPrioridadeAprovada(String prioridadeAprovada) {
		this.prioridadeAprovada = prioridadeAprovada;
	}

	@Column(name = "PARECER_CONSULTOR", length = 4000)
	@Length(max = 4000)
	public String getParecerConsultor() {
		return this.parecerConsultor;
	}

	public void setParecerConsultor(String parecerConsultor) {
		this.parecerConsultor = parecerConsultor;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DTHR_CONHECIMENTO_CONSULTOR", length = 29)
	public Date getDthrConhecimentoConsultor() {
		return this.dthrConhecimentoConsultor;
	}

	public void setDthrConhecimentoConsultor(Date dthrConhecimentoConsultor) {
		this.dthrConhecimentoConsultor = dthrConhecimentoConsultor;
	}

	@Column(name = "SER_MATRICULA_CONHECIMENTO")
	public Integer getSerMatriculaConhecimento() {
		return this.serMatriculaConhecimento;
	}

	public void setSerMatriculaConhecimento(Integer serMatriculaConhecimento) {
		this.serMatriculaConhecimento = serMatriculaConhecimento;
	}

	@Column(name = "SER_VIN_CODIGO_CONHECIMENTO")
	public Short getSerVinCodigoConhecimento() {
		return this.serVinCodigoConhecimento;
	}

	public void setSerVinCodigoConhecimento(Short serVinCodigoConhecimento) {
		this.serVinCodigoConhecimento = serVinCodigoConhecimento;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DTHR_VISUALIZA_RESPOSTA", length = 29)
	public Date getDthrVisualizaResposta() {
		return this.dthrVisualizaResposta;
	}

	public void setDthrVisualizaResposta(Date dthrVisualizaResposta) {
		this.dthrVisualizaResposta = dthrVisualizaResposta;
	}

	@Column(name = "SER_MATRICULA_VISUALIZA")
	public Integer getSerMatriculaVisualiza() {
		return this.serMatriculaVisualiza;
	}

	public void setSerMatriculaVisualiza(Integer serMatriculaVisualiza) {
		this.serMatriculaVisualiza = serMatriculaVisualiza;
	}

	@Column(name = "SER_VIN_CODIGO_VISUALIZA")
	public Short getSerVinCodigoVisualiza() {
		return this.serVinCodigoVisualiza;
	}

	public void setSerVinCodigoVisualiza(Short serVinCodigoVisualiza) {
		this.serVinCodigoVisualiza = serVinCodigoVisualiza;
	}

	@Column(name = "CON_NUMERO_RETORNO")
	public Integer getConNumeroRetorno() {
		return this.conNumeroRetorno;
	}

	public void setConNumeroRetorno(Integer conNumeroRetorno) {
		this.conNumeroRetorno = conNumeroRetorno;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DTHR_MARCACAO_RETORNO", length = 29)
	public Date getDthrMarcacaoRetorno() {
		return this.dthrMarcacaoRetorno;
	}

	public void setDthrMarcacaoRetorno(Date dthrMarcacaoRetorno) {
		this.dthrMarcacaoRetorno = dthrMarcacaoRetorno;
	}

	@Column(name = "SER_MATRICULA_RETORNO")
	public Integer getSerMatriculaRetorno() {
		return this.serMatriculaRetorno;
	}

	public void setSerMatriculaRetorno(Integer serMatriculaRetorno) {
		this.serMatriculaRetorno = serMatriculaRetorno;
	}

	@Column(name = "SER_VIN_CODIGO_RETORNO")
	public Short getSerVinCodigoRetorno() {
		return this.serVinCodigoRetorno;
	}

	public void setSerVinCodigoRetorno(Short serVinCodigoRetorno) {
		this.serVinCodigoRetorno = serVinCodigoRetorno;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DTHR_AVISA_RETORNO", length = 29)
	public Date getDthrAvisaRetorno() {
		return this.dthrAvisaRetorno;
	}

	public void setDthrAvisaRetorno(Date dthrAvisaRetorno) {
		this.dthrAvisaRetorno = dthrAvisaRetorno;
	}

	@Column(name = "SER_MATRICULA_AVISA_RETORNO")
	public Integer getSerMatriculaAvisaRetorno() {
		return this.serMatriculaAvisaRetorno;
	}

	public void setSerMatriculaAvisaRetorno(Integer serMatriculaAvisaRetorno) {
		this.serMatriculaAvisaRetorno = serMatriculaAvisaRetorno;
	}

	@Column(name = "SER_VIN_CODIGO_AVISA_RETORNO")
	public Integer getSerVinCodigoAvisaRetorno() {
		return this.serVinCodigoAvisaRetorno;
	}

	public void setSerVinCodigoAvisaRetorno(Integer serVinCodigoAvisaRetorno) {
		this.serVinCodigoAvisaRetorno = serVinCodigoAvisaRetorno;
	}

	public enum Fields {

	/* ATUALIZADOR JOURNALS - Fields	SEQ_JN("seqJn"),
		JN_USER("jnUser"),
		JN_DATE_TIME("jnDateTime"),
		JN_OPERATION("jnOperation"),*/
		SEQ("seq"),
		OBSERVACAO("observacao"),
		DTHR_CRIACAO("dthrCriacao"),
		DTHR_VALIDA("dthrValida"),
		DTHR_MVTO("dthrMvto"),
		DTHR_VALIDA_MVTO("dthrValidaMvto"),
		IND_PENDENTE("indPendente"),
		PAC_CODIGO("pacCodigo"),
		CON_NUMERO("conNumero"),
		ESP_SEQ("espSeq"),
		SER_MATRICULA("serMatricula"),
		SER_VIN_CODIGO("serVinCodigo"),
		SER_MATRICULA_VALIDA("serMatriculaValida"),
		SER_VIN_CODIGO_VALIDA("serVinCodigoValida"),
		SER_MATRICULA_MVTO("serMatriculaMvto"),
		SER_VIN_CODIGO_MVTO("serVinCodigoMvto"),
		SER_MATRICULA_VALIDA_MVTO("serMatriculaValidaMvto"),
		SER_VIN_CODIGO_VALIDA_MVTO("serVinCodigoValidaMvto"),
		IEO_SEQ("ieoSeq"),
		EQP_SEQ("eqpSeq"),
		SER_MATRICULA_RESPONSAVEL("serMatriculaResponsavel"),
		SER_VIN_CODIGO_RESPONSAVEL("serVinCodigoResponsavel"),
		IND_IMPRESSO("indImpresso"),
		SITUACAO("situacao"),
		CON_NUMERO_MARCADA("conNumeroMarcada"),
		IND_DIGITADO("indDigitado"),
		ESP_SEQ_AGENDA("espSeqAgenda"),
		OBSERVACAO_ADICIONAL("observacaoAdicional"),
		ESP_SEQ_ADM("espSeqAdm"),
		DTHR_MARCADA("dthrMarcada"),
		DTHR_AVISADA("dthrAvisada"),
		SER_MATRICULA_MARCADA("serMatriculaMarcada"),
		SER_VIN_CODIGO_MARCADA("serVinCodigoMarcada"),
		SER_MATRICULA_AVISADA("serMatriculaAvisada"),
		SER_VIN_CODIGO_AVISADA("serVinCodigoAvisada"),
		IND_URGENTE("indUrgente"),
		IND_CANCELADO_OBITO("indCanceladoObito"),
		AVALIACAO("avaliacao"),
		SER_MATRICULA_AVALIA("serMatriculaAvalia"),
		SER_VIN_CODIGO_AVALIA("serVinCodigoAvalia"),
		DTHR_AVALIACAO("dthrAvaliacao"),
		PRIORIDADE("prioridade"),
		TIPO_SOLICITACAO("tipoSolicitacao"),
		PRIORIDADE_APROVADA("prioridadeAprovada"),
		PARECER_CONSULTOR("parecerConsultor"),
		DTHR_CONHECIMENTO_CONSULTOR("dthrConhecimentoConsultor"),
		SER_MATRICULA_CONHECIMENTO("serMatriculaConhecimento"),
		SER_VIN_CODIGO_CONHECIMENTO("serVinCodigoConhecimento"),
		DTHR_VISUALIZA_RESPOSTA("dthrVisualizaResposta"),
		SER_MATRICULA_VISUALIZA("serMatriculaVisualiza"),
		SER_VIN_CODIGO_VISUALIZA("serVinCodigoVisualiza"),
		CON_NUMERO_RETORNO("conNumeroRetorno"),
		DTHR_MARCACAO_RETORNO("dthrMarcacaoRetorno"),
		SER_MATRICULA_RETORNO("serMatriculaRetorno"),
		SER_VIN_CODIGO_RETORNO("serVinCodigoRetorno"),
		DTHR_AVISA_RETORNO("dthrAvisaRetorno"),
		SER_MATRICULA_AVISA_RETORNO("serMatriculaAvisaRetorno"),
		SER_VIN_CODIGO_AVISA_RETORNO("serVinCodigoAvisaRetorno");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}

	}

}
