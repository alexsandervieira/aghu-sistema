package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio que representa os possíveis tipos (origens) de solicitação de prontuário.
 *
 */
public enum DominioTipoSolicitacaoProntuario implements Dominio {
	/**
	 * Ambulatório
	 */
	A,
	/**
	 * Internação
	 */
	I,
	/**
	 * Avulsa
	 */
	V,
	/**
	 * Cirurgia
	 */
	C;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case A:
			return "Ambulatório";
		case I:
			return "Internação";
		case V:
			return "Avulsa";	
		case C:
			return "Cirurgia";	
		default:
			return "";
		}

	}

}
