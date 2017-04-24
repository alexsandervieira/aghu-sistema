package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioOpcaoEncerramentoAmbulatorio implements Dominio {

	/**
	 * Apac Quimio
	 */
	APAC, 
	/**
	 * Apac Nefro
	 */
	APAN,
	/**
	 * Apac de Acomp. Pós-transplante
	 */
	APAP,
	/**
	 * Apac de Exames
	 */
	APEX,
	/**
	 * Apac de Radioterapia
	 */
	APAR,
	/**
	 * Apa de Otorrino
	 */
	APAT,
	/**
	 * Apac de Fotocoagulação
	 */
	APAF,
	/**
	 * Apac de Pré-Transplante
	 */
	APRE,
	/**
	 * Programa SISCOLO
	 */
	SISCOLO,
	/**
	 * Programa SISMAMA
	 */
	SISMAMA,
	/**
	 * Ambulatório (BPA/BPI) 
	 */
	AMB,
	/**
	 * Todos 
	 */
	TODOS
	;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case APAC:
			return "Apac Quimio";
		case APAN:
			return "Apac Nefro";
		case APAP:
			return "Apac de Acomp. Pós-transplante";
		case APEX:
			return "Apac de Exames";
		case APAR:
			return "Apac de Radioterapia";
		case APAT:
			return "Apa de Otorrino";
		case APAF:
			return "Apac de Fotocoagulação";
		case APRE:
			return "Apac de Pré-Transplante";
		case SISCOLO:
			return "Programa SISCOLO";
		case SISMAMA:
			return "Programa SISMAMA";
		case AMB:
			return "Ambulatório (BPA/BPI)";
		case TODOS:
			return "Todos";
		default:
			return "";
		}
	}

	public static DominioOpcaoEncerramentoAmbulatorio getInstance(String valor) {
		if ("AMB".equals(valor)) {
			return DominioOpcaoEncerramentoAmbulatorio.AMB;
		} else if ("APAC".equals(valor)) {
			return DominioOpcaoEncerramentoAmbulatorio.APAC;
		} else if ("APAF".equals(valor)) {
			return DominioOpcaoEncerramentoAmbulatorio.APAF;
		} else if ("APAN".equals(valor)) {
			return DominioOpcaoEncerramentoAmbulatorio.APAN;
		} else if ("APAP".equals(valor)) {
			return DominioOpcaoEncerramentoAmbulatorio.APAP;
		} else if ("APAR".equals(valor)) {
			return DominioOpcaoEncerramentoAmbulatorio.APAR;			
		} else if ("APAT".equals(valor)) {
			return DominioOpcaoEncerramentoAmbulatorio.APAT;			
		} else if ("APEX".equals(valor)) {
			return DominioOpcaoEncerramentoAmbulatorio.APEX;			
		} else if ("APRE".equals(valor)) {
			return DominioOpcaoEncerramentoAmbulatorio.APRE;			
		} else if ("SISCOLO".equals(valor)) {
			return DominioOpcaoEncerramentoAmbulatorio.SISCOLO;			
		} else if ("SISMAMA".equals(valor)) {
			return DominioOpcaoEncerramentoAmbulatorio.SISMAMA;			
		} else if ("TODOS".equals(valor)) {
			return DominioOpcaoEncerramentoAmbulatorio.TODOS;			
		} else {
			return null;
		}
	}
	
}
