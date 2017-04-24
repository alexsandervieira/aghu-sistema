package br.gov.mec.aghu.blococirurgico.vo;

import java.io.Serializable;


public class SubRelatorioNotasDeConsumoDaSalaOPMEVO implements Serializable {
	
	private static final long serialVersionUID = -2047178099786822060L;

	private String nome;
	private Double quantidade;
	
	public SubRelatorioNotasDeConsumoDaSalaOPMEVO() {
		super();
	}
	
	public SubRelatorioNotasDeConsumoDaSalaOPMEVO(String nome, Double quantidade) {
		super();
		this.nome = nome;
		this.quantidade = quantidade;
	}

	public enum Fields {

		NOME("nome"),
		QUANTIDADE("quantidade");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}

	}

	//Getters and Setters
	public Double getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(Double quantidade) {
		this.quantidade = quantidade;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}
}