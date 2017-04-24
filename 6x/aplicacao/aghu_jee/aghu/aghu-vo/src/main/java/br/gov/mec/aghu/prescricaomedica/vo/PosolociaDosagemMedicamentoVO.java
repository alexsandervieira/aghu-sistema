package br.gov.mec.aghu.prescricaomedica.vo;



public class PosolociaDosagemMedicamentoVO  implements java.io.Serializable{


	/**
	 * 
	 */
	private static final long serialVersionUID = -2347693833058531014L;
	private String posologia;
	private String dosagem;
	private String complementoDosagem;
	private String aprazamento;
	
	public PosolociaDosagemMedicamentoVO() {
	}

	public String getPosologia() {
		return posologia;
	}

	public void setPosologia(String posologia) {
		this.posologia = posologia;
	}

	public String getDosagem() {
		return dosagem;
	}

	public void setDosagem(String dosagem) {
		this.dosagem = dosagem;
	}

	public String getAprazamento() {
		return aprazamento;
	}

	public void setAprazamento(String aprazamento) {
		this.aprazamento = aprazamento;
	}

	public String getComplementoDosagem() {
		return complementoDosagem;
	}

	public void setComplementoDosagem(String complementoDosagem) {
		this.complementoDosagem = complementoDosagem;
	}


}
