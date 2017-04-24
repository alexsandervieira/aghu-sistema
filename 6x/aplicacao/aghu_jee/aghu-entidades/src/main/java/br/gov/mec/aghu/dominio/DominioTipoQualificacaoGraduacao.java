package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio que indica os tipos de qualificacao presentes na tabela RAP_TIPOS_QUALIFICACAO
 * 
 * @author ghluz
 * 
 */
public enum DominioTipoQualificacaoGraduacao implements Dominio{
	
	MEDICINA,
	ENFERMAGEM,
	TECNICO_EM_ENFERMAGEM,
	FISIOTERAPIA,
	NUTRICAO,
	FONOAUDIOLOGIA,
	SERVICO_SOCIAL,
	TERAPIA_OCUPACIONAL,
	EDUCACAO_FÍSICA,
	ODONTOLOGIA;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case MEDICINA:
			return "Medicina";
		case ENFERMAGEM:
			return "Enfermagem";
		case TECNICO_EM_ENFERMAGEM:
			return "Técnico em enfermagem";
		case FISIOTERAPIA:
			return "Fisioterapia";
		case NUTRICAO:
			return "Nutrição";
		case FONOAUDIOLOGIA:
			return "Fonoaudiologia";
		case SERVICO_SOCIAL:
			return "Serviço Social";
		case TERAPIA_OCUPACIONAL:
			return "Terapia Ocupacional";
		case EDUCACAO_FÍSICA :
			return "Educação Física";
		case ODONTOLOGIA:
			return "Odontologia";
		default:
			return "";
		}
	}

}
