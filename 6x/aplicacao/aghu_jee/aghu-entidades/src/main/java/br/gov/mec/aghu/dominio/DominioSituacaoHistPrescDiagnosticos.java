package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioSituacaoHistPrescDiagnosticos implements Dominio {

	/**
	 * Informado
	 */
	I,
	/**
	 * Confirmado
	 */
	C,
	/**
	 * Encerrado
	 */
	E,
	/**
	 * Excluído
	 */
	X;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case I:
			return "Informado";
		case C:
			return "Confirmado";
		case E:
			return "Encerrado";
		case X:
			return "Excluído";	
		default:
			return "Estado inválido. Contate o administrador do sistema.";
		}
	}
}
