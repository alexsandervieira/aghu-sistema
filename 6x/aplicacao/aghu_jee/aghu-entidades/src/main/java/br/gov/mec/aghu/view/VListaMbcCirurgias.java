package br.gov.mec.aghu.view;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Immutable;

import br.gov.mec.aghu.dominio.DominioIndPendenteAmbulatorio;
import br.gov.mec.aghu.dominio.DominioLadoCirurgiaAgendas;
import br.gov.mec.aghu.dominio.DominioNaturezaFichaAnestesia;
import br.gov.mec.aghu.dominio.DominioOrigemPacienteCirurgia;
import br.gov.mec.aghu.dominio.DominioSituacaoCirurgia;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.FatConvenioSaude;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;
import br.gov.mec.aghu.model.MbcMotivoCancelamento;
import br.gov.mec.aghu.model.MbcQuestao;
import br.gov.mec.aghu.model.MbcSalaCirurgica;
import br.gov.mec.aghu.model.MbcValorValidoCanc;
import br.gov.mec.aghu.model.RapServidores;

/**
 * VListaCirurgias generated by hbm2java
 */
@Entity
@Table(name = "V_LISTA_MBC_CIRURGIAS", schema = "AGH")
@Immutable
public class VListaMbcCirurgias implements java.io.Serializable {
	

	private static final long serialVersionUID = -3037826267323781731L;
	private Integer crgSeq;
	private Date crgData;
	private String salaNome;
	private Integer pacCodigo;
	private String pacNome;
	private Integer prontuario;	
    private Short espSeq;	
	private String espSigla;
	private String espNome;
	private DominioNaturezaFichaAnestesia naturezaAgenda;	
	private Date dataFimCirurgia;
	private DominioOrigemPacienteCirurgia origemPacienteCirurgia;
	private FatConvenioSaudePlano convenioSaudePlano;
	private FatConvenioSaude convenioSaude;
	private Date dataInicioCirurgia;
	private DominioLadoCirurgiaAgendas agendaLadoCirurgia;
	private DominioSituacaoCirurgia situacao;
	private AghAtendimentos atendimento;
	private MbcValorValidoCanc valorValidoCanc;
	private MbcQuestao questao;
	private MbcMotivoCancelamento motivoCancelamento;
	private String complementoCanc;
	private Date criadoEm;
	private Boolean digitaNotaSala;
	private RapServidores servidor;
	/*** CAMPOS NOVOS ***/
	private AghUnidadesFuncionais unidadeFuncional;
	private Short sciSeqp;
	private Date dataInicioOrdem;
	private Date dataEntradaSr;
	private Date dataSaidaSr;
	private MbcSalaCirurgica salaCirurgica;
	private String procedimentoCirurgicoDescricao;
	private String nomeEquipe;
	private Boolean projetoPesquisaPaciente;	
	private Integer tempDescrCirPendente;
	private Integer tempDescrCir;
	private Integer tempDescrPdtPendente;
	private Integer tempDescrPdtSimples;	
	private Long fichaSeq;
	private DominioIndPendenteAmbulatorio fichaPendente;
	private Integer temCertificacaoDigital;	
	private Integer temGermeMulti;	
	private Integer exigeCerih;	
	private String ltoLtoId;
	private Short pacUnfSeq;
	private Date dtUltInternacao;
	private Date dtUltAlta;
	private String qrtDescricao;
	private Integer temPacInternacao;
	private Byte pacUnfAndar;
	private String pacUnfAla;
	
	@Id	
	@Column(name = "SEQ", precision = 8, scale = 0)
	public Integer getCrgSeq() {
		return this.crgSeq;
	}

	public void setCrgSeq(Integer crgSeq) {
		this.crgSeq = crgSeq;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DATA")	
	public Date getCrgData() {
		return this.crgData;
	}

	public void setCrgData(Date crgData) {
		this.crgData = crgData;
	}
	
	@Column(name = "SALA_NOME", length = 60)	
	public String getSalaNome() {
		return salaNome;
	}

	public void setSalaNome(String salaNome) {
		this.salaNome = salaNome;
	}

	@Column(name = "PAC_CODIGO",precision = 8, scale = 0)	
	public Integer getPacCodigo() {
		return pacCodigo;
	}

	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}

	@Column(name = "NOME_PACIENTE", length = 50)
	public String getPacNome() {
		return pacNome;
	}

	public void setPacNome(String pacNome) {
		this.pacNome = pacNome;
	}

	@Column(name = "PRONTUARIO", precision = 8, scale = 0)	
	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}
		
	@Column(name = "ESP_SEQ", precision = 4, scale = 0)
	public Short getEspSeq() {
		return espSeq;
	}

	public void setEspSeq(Short espSeq) {
		this.espSeq = espSeq;
	}

	@Column(name = "ESP_SIGLA", nullable = false, length = 3)
	public String getEspSigla() {
		return espSigla;
	}

	public void setEspSigla(String espSigla) {
		this.espSigla = espSigla;
	}

	@Column(name = "ESP_NOME", nullable = false, length = 45)
	public String getEspNome() {
		return espNome;
	}

	public void setEspNome(String espNome) {
		this.espNome = espNome;
	}

	@Column(name = "NATUREZA_AGEND", length = 3)	
	@Enumerated(EnumType.STRING)
	public DominioNaturezaFichaAnestesia getNaturezaAgenda() {
		return naturezaAgenda;
	}

	public void setNaturezaAgenda(DominioNaturezaFichaAnestesia naturezaAgenda) {
		this.naturezaAgenda = naturezaAgenda;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DTHR_FIM_CIRG")
	public Date getDataFimCirurgia() {
		return this.dataFimCirurgia;
	}

	public void setDataFimCirurgia(Date dataFimCirurgia) {
		this.dataFimCirurgia = dataFimCirurgia;
	}
	
	@Column(name = "ORIGEM_PAC_CIRG", length = 1)
	@Enumerated(EnumType.STRING)
	public DominioOrigemPacienteCirurgia getOrigemPacienteCirurgia() {
		return this.origemPacienteCirurgia;
	}

	public void setOrigemPacienteCirurgia(
			DominioOrigemPacienteCirurgia origemPacienteCirurgia) {
		this.origemPacienteCirurgia = origemPacienteCirurgia;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "CSP_CNV_CODIGO", referencedColumnName = "CNV_CODIGO", insertable = false, updatable = false),
			@JoinColumn(name = "CSP_SEQ", referencedColumnName = "SEQ", insertable = false, updatable = false) })	
	public FatConvenioSaudePlano getConvenioSaudePlano() {
		return convenioSaudePlano;
	}

	public void setConvenioSaudePlano(FatConvenioSaudePlano convenioSaudePlano) {
		this.convenioSaudePlano = convenioSaudePlano;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CSP_CNV_CODIGO")	
	public FatConvenioSaude getConvenioSaude() {
		return this.convenioSaude;
	}

	public void setConvenioSaude(FatConvenioSaude fatConveniosSaude) {
		this.convenioSaude = fatConveniosSaude;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DTHR_INICIO_CIRG")
	public Date getDataInicioCirurgia() {
		return this.dataInicioCirurgia;
	}

	public void setDataInicioCirurgia(Date dataInicioCirurgia) {
		this.dataInicioCirurgia = dataInicioCirurgia;
	}

	@Column(name = "LADO_CIRURGIA", length = 2)
	@Enumerated(EnumType.STRING)
	public DominioLadoCirurgiaAgendas getAgendaLadoCirurgia() {
		return agendaLadoCirurgia;
	}

	public void setAgendaLadoCirurgia(DominioLadoCirurgiaAgendas agendaLadoCirurgia) {
		this.agendaLadoCirurgia = agendaLadoCirurgia;
	}
	
	@Column(name = "SITUACAO", nullable = false, length = 4)
	@Enumerated(EnumType.STRING)
	public DominioSituacaoCirurgia getSituacao() {
		return this.situacao;
	}

	public void setSituacao(DominioSituacaoCirurgia situacao) {
		this.situacao = situacao;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ATD_SEQ")
	public AghAtendimentos getAtendimento() {
		return atendimento;
	}

	public void setAtendimento(AghAtendimentos atendimento) {
		this.atendimento = atendimento;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "QES_MTC_SEQ", referencedColumnName = "MTC_SEQ"),
			@JoinColumn(name = "QES_SEQP", referencedColumnName = "SEQP") })
	public MbcQuestao getQuestao() {
		return this.questao;
	}

	public void setQuestao(MbcQuestao questao) {
		this.questao = questao;
	}
		
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "VVC_QES_MTC_SEQ", referencedColumnName = "QES_MTC_SEQ"),
			@JoinColumn(name = "VVC_QES_SEQP", referencedColumnName = "QES_SEQP"),
			@JoinColumn(name = "VVC_SEQP", referencedColumnName = "SEQP") })
	public MbcValorValidoCanc getValorValidoCanc() {
		return this.valorValidoCanc;
	}

	public void setValorValidoCanc(MbcValorValidoCanc valorValidoCanc) {
		this.valorValidoCanc = valorValidoCanc;
	}

	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "MTC_SEQ")
	public MbcMotivoCancelamento getMotivoCancelamento() {
		return this.motivoCancelamento;
	}
	
	public void setMotivoCancelamento(MbcMotivoCancelamento motivoCancelamento) {
		this.motivoCancelamento = motivoCancelamento;
	}

	@Column(name = "COMPLEMENTO_CANC", length = 100)	
	public String getComplementoCanc() {
		return this.complementoCanc;
	}

	public void setComplementoCanc(String complementoCanc) {
		this.complementoCanc = complementoCanc;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM")
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}
	
	@Column(name = "IND_DIGT_NOTA_SALA", length = 1)	
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getDigitaNotaSala() {
		return this.digitaNotaSala;
	}

	public void setDigitaNotaSala(Boolean digitaNotaSala) {
		this.digitaNotaSala = digitaNotaSala;
	}
	
	@JoinColumns( {
		@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA"),
		@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO") })
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
	public RapServidores getServidor() {
		return servidor;
	}

	public void setServidor(RapServidores sevidor) {
		this.servidor = sevidor;
	}
	
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "UNF_SEQ", nullable = false, referencedColumnName = "SEQ")	
	public AghUnidadesFuncionais getUnidadeFuncional() {
		return unidadeFuncional;
	}

	public void setUnidadeFuncional(AghUnidadesFuncionais unidadeFuncional) {
		this.unidadeFuncional = unidadeFuncional;
	}
	
	@Column(name = "SCI_SEQP", insertable=false, updatable=false)
	public Short getSciSeqp() {
		return sciSeqp;
	}	

	public void setSciSeqp(Short sciSeqp) {
		this.sciSeqp = sciSeqp;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DTHR_INICIO_ORDEM")
	public Date getDataInicioOrdem() {
		return this.dataInicioOrdem;
	}

	public void setDataInicioOrdem(Date dataInicioOrdem) {
		this.dataInicioOrdem = dataInicioOrdem;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DTHR_ENTRADA_SR")
	public Date getDataEntradaSr() {
		return this.dataEntradaSr;
	}

	public void setDataEntradaSr(Date dataEntradaSr) {
		this.dataEntradaSr = dataEntradaSr;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DTHR_SAIDA_SR")
	public Date getDataSaidaSr() {
		return this.dataSaidaSr;
	}

	public void setDataSaidaSr(Date dataSaidaSr) {
		this.dataSaidaSr = dataSaidaSr;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "SCI_UNF_SEQ", referencedColumnName = "UNF_SEQ"),
			@JoinColumn(name = "SCI_SEQP", referencedColumnName = "SEQP") })
	public MbcSalaCirurgica getSalaCirurgica() {
		return this.salaCirurgica;
	}

	public void setSalaCirurgica(MbcSalaCirurgica salaCirurgica) {
		this.salaCirurgica = salaCirurgica;
	}
	
	@Column(name = "PROC_DESCR", length = 120)
	public String getProcedimentoCirurgicoDescricao() {
		return procedimentoCirurgicoDescricao;
	}

	public void setProcedimentoCirurgicoDescricao(
			String procedimentoCirurgicoDescricao) {
		this.procedimentoCirurgicoDescricao = procedimentoCirurgicoDescricao;
	}

	@Column(name = "NOME_EQUIPE", length = 50)
	public String getNomeEquipe() {
		return nomeEquipe;
	}

	public void setNomeEquipe(String nomeEquipe) {
		this.nomeEquipe = nomeEquipe;
	}

	
	@Column(name = "PROJETO", nullable = false, length = 1)	
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getProjetoPesquisaPaciente() {
		return projetoPesquisaPaciente;
	}

	public void setProjetoPesquisaPaciente(Boolean projetoPesquisaPaciente) {
		this.projetoPesquisaPaciente = projetoPesquisaPaciente;
	}


	@Column(name = "TEMP_DESC_CIRU_PENDENTE", precision = 4, scale = 0)
	public Integer getTempDescrCirPendente() {
		return tempDescrCirPendente;
	}

	public void setTempDescrCirPendente(Integer tempDescrCirPendente) {
		this.tempDescrCirPendente = tempDescrCirPendente;
	}

	@Column(name = "TEMP_DESC_CIRU", precision = 4, scale = 0)
	public Integer getTempDescrCir() {
		return tempDescrCir;
	}

	public void setTempDescrCir(Integer tempDescrCir) {
		this.tempDescrCir = tempDescrCir;
	}

	@Column(name = "TEMP_DESC_PDT_PENDENTE", precision = 4, scale = 0)
	public Integer getTempDescrPdtPendente() {
		return tempDescrPdtPendente;
	}

	public void setTempDescrPdtPendente(Integer tempDescrPdtPendente) {
		this.tempDescrPdtPendente = tempDescrPdtPendente;
	}

	@Column(name = "TEMP_DESC_PDT_SIMPLES", precision = 4, scale = 0)
	public Integer getTempDescrPdtSimples() {
		return tempDescrPdtSimples;
	}

	public void setTempDescrPdtSimples(Integer tempDescrPdtSimples) {
		this.tempDescrPdtSimples = tempDescrPdtSimples;
	}
	
	@Column(name = "FICHA_SEQ", precision = 10, scale = 0)
	public Long getFichaSeq() {
		return this.fichaSeq;
	}

	public void setFichaSeq(Long fichaSeq) {
		this.fichaSeq = fichaSeq;
	}

	@Column(name = "FICHA_PENDENTE", length = 1)	
	@Enumerated(EnumType.STRING)
	public DominioIndPendenteAmbulatorio getFichaPendente() {
		return fichaPendente;
	}

	public void setFichaPendente(DominioIndPendenteAmbulatorio fichaPendente) {
		this.fichaPendente = fichaPendente;
	}

	@Column(name = "TEM_CERTIFICACAO", precision = 4, scale = 0)	
	public Integer getTemCertificacaoDigital() {
		return temCertificacaoDigital;
	}

	public void setTemCertificacaoDigital(Integer temCertificacaoDigital) {
		this.temCertificacaoDigital = temCertificacaoDigital;
	}
	
	@Column(name = "TEM_GERME_MULTI", precision = 4, scale = 0)	
	public Integer getTemGermeMulti() {
		return temGermeMulti;
	}

	public void setTemGermeMulti(Integer temGermeMulti) {
		this.temGermeMulti = temGermeMulti;
	}
	
	@Column(name = "EXIGE_CERIH", precision = 4, scale = 0)	
	public Integer getExigeCerih() {
		return exigeCerih;
	}

	public void setExigeCerih(Integer exigeCerih) {
		this.exigeCerih = exigeCerih;
	}

	@Column(name = "LTO_LTO_ID", precision = 4, scale = 0)
	public String getLtoLtoId() {
		return ltoLtoId;
	}

	public void setLtoLtoId(String ltoLtoId) {
		this.ltoLtoId = ltoLtoId;
	}

	@Column(name = "PAC_UNF_SEQ", precision = 4, scale = 0)
	public Short getPacUnfSeq() {
		return pacUnfSeq;
	}

	public void setPacUnfSeq(Short pacUnfSeq) {
		this.pacUnfSeq = pacUnfSeq;
	}

	@Column(name = "QRT_DESCRICAO")
	public String getQrtDescricao() {
		return qrtDescricao;
	}

	public void setQrtDescricao(String qrtDescricao) {
		this.qrtDescricao = qrtDescricao;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DT_ULT_INTERNACAO")
	public Date getDtUltInternacao() {
		return this.dtUltInternacao;
	}

	public void setDtUltInternacao(Date dtUltInternacao) {
		this.dtUltInternacao = dtUltInternacao;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DT_ULT_ALTA")
	public Date getDtUltAlta() {
		return this.dtUltAlta;
	}

	public void setDtUltAlta(Date dtUltAlta) {
		this.dtUltAlta = dtUltAlta;
	}

	@Column(name = "TEM_PAC_INTERNACAO", precision = 8, scale = 0)	
	public Integer getTemPacInternacao() {
		return temPacInternacao;
	}

	public void setTemPacInternacao(Integer temPacInternacao) {
		this.temPacInternacao = temPacInternacao;
	}

	@Column(name = "PAC_UNF_ANDAR", precision = 2, scale = 0)
	public Byte getPacUnfAndar() {
		return pacUnfAndar;
	}

	public void setPacUnfAndar(Byte pacUnfAndar) {
		this.pacUnfAndar = pacUnfAndar;
	}

	@Column(name = "PAC_UNF_ALA")
	public String getPacUnfAla() {
		return pacUnfAla;
	}

	public void setPacUnfAla(String pacUnfAla) {
		this.pacUnfAla = pacUnfAla;
	}

	public enum Fields {
		SEQ("crgSeq"),
		DATA("crgData"),
		SALA_NOME("salaNome"),
		PAC_CODIGO("pacCodigo"),
		NOME("pacNome"),
		PRONTUARIO("prontuario"),
		ESP_SEQ("espSeq"),
		ESP_SIGLA("espSigla"),
		ESP_NOME("espNome"),
		NATUREZA_AGEND("naturezaAgenda"),
		DTHR_FIM_CIRG("dataFimCirurgia"), 
		ORIGEM_PAC_CIRG("origemPacienteCirurgia"), 
		CONVENIO_SAUDE("convenioSaude"), 
		CONVENIO_SAUDE_PLANO("convenioSaudePlano"), 
		CSP_SEQ("convenioSaudePlano.id.seq"),
		CSP_CNV_CODIGO("convenioSaude.codigo"), 
		DTHR_INICIO_CIRG("dataInicioCirurgia"), 
		AGENDA_LADO_CIRG("agendaLadoCirurgia"),
		SITUACAO("situacao"),
		ATENDIMENTO_SEQ("atendimento.seq"), 
		ATENDIMENTO("atendimento"), 
		QUESTAO("questao"),
		QES_MTC_SEQ("questao.id.mtcSeq"),
		QES_SEQP("questao.id.seqp"),
		VVC_QES_MTC_SEQ("valorValidoCanc.id.qesMtcSeq"),
		VVC_QES_SEQP("valorValidoCanc.id.qesSeqp"),
		VVC_SEQP("valorValidoCanc.id.seqp"),
		MOTIVO_CANCELAMENTO("motivoCancelamento"), 
		MTC_SEQ("motivoCancelamento.seq"),
		COMPLEMENTO_CANC("complementoCanc"), 
		CRIADO_EM("criadoEm"),
		IND_DIGT_NOTA_SALA("digitaNotaSala"),
		SERVIDOR("servidor"), 
		SERVIDOR_MATRICULA("servidor.id.matricula"),
		SERVIDOR_VIN_CODIGO("servidor.id.vinCodigo"),
		UNIDADE_FUNCIONAL("unidadeFuncional"),
		SALA_CIRURGICA("salaCirurgica"),
		SCI_SEQP("sciSeqp"), 
		DTHR_ENTRADA_SR("dataEntradaSr"),
		DTHR_SAIDA_SR("dataSaidaSr"), 
		DTHR_INICIO_ORDEM("dataInicioOrdem"),
		PROC_CIR_DESCRICAO("procedimentoCirurgicoDescricao"),
		NOME_EQUIPE("nomeEquipe"),
		PROJ_PESQ_PAC("projetoPesquisaPaciente"),		
		TEMP_DESC_CIR_PENDENTE("tempDescrCirPendente"),
		TEMP_DESCR_CIR("tempDescrCir"),
		TEMP_DESCR_PDT_PENDENTE("tempDescrPdtPendente"),
		TEMP_DESCR_PDT_SIMPLES("tempDescrPdtSimples"),
		FICHA_SEQ("fichaSeq"),
		FICHA_PENDENTE("fichaPendente"),
		TEM_CERTIF_DIGITAL("temCertificacaoDigital"),		
		TEM_GERME_MULTI("temGermeMulti"),
		EXIGE_CERIH("exigeCerih"),
        LTO_LTO_ID("ltoLtoId"),
        PAC_UNF_SEQ("pacUnfSeq"),
        DT_ULT_INTERNACAO("dtUltInternacao"),
        DT_ULT_ALTA("dtUltAlta"),
		QRT_DESCRICAO("qrtDescricao"),
		TEM_PAC_INTERNACAO("temPacInternacao"),
		PAC_UNF_ANDAR("pacUnfAndar"),
		PAC_UNF_ALA("pacUnfAla");

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
		int result = 1;
		result = prime * result + ((crgSeq == null) ? 0 : crgSeq.hashCode());
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
		if (getClass() != obj.getClass()) {
			return false;
		}
		VListaMbcCirurgias other = (VListaMbcCirurgias) obj;
		if (crgSeq == null) {
			if (other.crgSeq != null) {
				return false;
			}
		} else if (!crgSeq.equals(other.crgSeq)) {
			return false;
		}
		return true;
	}

	


}