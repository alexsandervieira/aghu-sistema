package br.gov.mec.aghu.prescricaomedica.vo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import br.gov.mec.aghu.dominio.DominioIndPendenteAmbulatorio;
import br.gov.mec.aghu.dominio.DominioSituacaoEvolucao;

public class EvolucaoPrescricaoVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3352218055819318838L;
	
	private Long seq;
	private String descricao;
	private Date dthrReferencia;
	private Date dthrPendente;
	private Date dthrCriacao;
	private Date dthrFim;
	private String nomeProfissional;
	private List<String> categoriasProfissional;
	private DominioSituacaoEvolucao situacao;
	private DominioIndPendenteAmbulatorio pendente;
	private Integer matricula;
	private Short vinCodigo;
	
	public enum Fields{
		SEQ("seq"),
		DTHR_REFERENCIA("dthrReferencia"),
		DTHR_CRIACAO("dthrCriacao"),
		DTHR_FIM("dthrFim"),
		NOME_PROF("nomeProfissional"),
		CATEGORIAS_PROFISSIONAIS("categoriasProfissional"),
		SITUACAO("situacao"),
		PENDENTE("pendente"),
		DTHR_PENDENTE("dthrPendente"),
		MATRICULA("matricula"),
		VIN_CODIGO("vinCodigo");
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}
	
	public Long getSeq() {
		return seq;
	}
	public void setSeq(Long seq) {
		this.seq = seq;
	}
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	public Date getDthrReferencia() {
		return dthrReferencia;
	}
	public void setDthrReferencia(Date dthrReferencia) {
		this.dthrReferencia = dthrReferencia;
	}
	public Date getDthrPendente() {
		return dthrPendente;
	}
	public void setDthrPendente(Date dthrPendente) {
		this.dthrPendente = dthrPendente;
	}
	public Date getDthrCriacao() {
		return dthrCriacao;
	}
	public void setDthrCriacao(Date dthrCriacao) {
		this.dthrCriacao = dthrCriacao;
	}
	public Date getDthrFim() {
		return dthrFim;
	}
	public void setDthrFim(Date dthrFim) {
		this.dthrFim = dthrFim;
	}
	public String getNomeProfissional() {
		return nomeProfissional;
	}
	public void setNomeProfissional(String profissional) {
		this.nomeProfissional = profissional;
	}
	public DominioSituacaoEvolucao getSituacao() {
		return situacao;
	}
	public void setSituacao(DominioSituacaoEvolucao situacao) {
		this.situacao = situacao;
	}
	public DominioIndPendenteAmbulatorio getPendente() {
		return pendente;
	}
	public void setPendente(DominioIndPendenteAmbulatorio pendente) {
		this.pendente = pendente;
	}
	public List<String> getCategoriasProfissional() {
		return categoriasProfissional;
	}
	public void setCategoriasProfissional(List<String> categoriasProfissional) {
		this.categoriasProfissional = categoriasProfissional;
	}
	public Integer getMatricula() {
		return matricula;
	}
	public void setMatricula(Integer matricula) {
		this.matricula = matricula;
	}
	public Short getVinCodigo() {
		return vinCodigo;
	}
	public void setVinCodigo(Short vinCodigo) {
		this.vinCodigo = vinCodigo;
	}
	
}
