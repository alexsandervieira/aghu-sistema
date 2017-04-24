package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioSigTipoAlertaDetalhado implements Dominio{
	
	QA,		
	CR,	
	NC,
	SA,
	CP;
	

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case QA:
			return "Centros de atividade com ajustes nas quantidades alocadas nas atividades.";
		case CR:
			return "Centros de atividade com objetos de custo calculados somente por rateio.";
		case NC:
			return "Centros de atividade sem objetos de custos ativos.";
		case SA:
			return "Centros de atividade sem análise do processamento.";
		case CP:
			return "Clientes de um objeto de custo de apoio sem peso/valor para receber rateio";
		default:
			return "";
		}
		
	}
}
