package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioTipoAgendamentoSisreg implements Dominio {


	/**
	 * Municipal
	 */
	M,
	
	/**
	 * Estadual
	 */
	E;
	
	
	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {

		switch (this) {
		case M:
			return "Municipal";
		case E:
			return "Estadual";
		default:
			return "";
		}

	}

}
