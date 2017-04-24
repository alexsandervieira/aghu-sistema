package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;



public enum DominioPeriodoDiasMeses implements Dominio {
	
	DIA1,
	DIAS7,
	DIAS15,
	DIAS30,
	DIAS45,
	DIAS60,
	MESES3,
	MESES6;
	
	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case DIA1:
			return "Últimas 24 horas";
		case DIAS7:
			return "Últimos 7 dias";
		case DIAS15:
			return "Últimos 15 dias";
		case DIAS30:
			return "Últimos 30 dias";
		case DIAS45:
			return "Últimos 45 dias";		
		case DIAS60:
			return "Últimos 60 dias";		
		case MESES3:
			return "Últimos 90 dias";		
		case MESES6:
			return "Últimos 120 dias";
		default:
			return "";
		}
	}
	

	public long getValorEmMilisegundos() {
		switch (this) {
		case DIA1:
			return 86400000; //24*60*60*1000
		case DIAS7:
			return 604800000; //7*24*60*60*1000
		case DIAS15:
			return 1296000000 ; //15*24*60*60*1000;
		case DIAS30:
			return 2592000000L; //30*24*60*60*1000;
		case DIAS45:
			return 3888000000L; //45*24*60*60*1000;		
		case DIAS60:
			return 5184000000L;  //60*24*60*60*1000;			
		case MESES3:
			return 7776000000L ;  //90*24*60*60*1000;	
		case MESES6:
			return 15552000000L ;  //180*24*60*60*1000;	
		default:
			return 0;
		}
	}
	
	public int getValorEmDias() {
		switch (this) {
		case DIA1:
			return 1; 
		case DIAS7:
			return 7; 		
		case DIAS15:
			return 15; 		
		case DIAS30:
			return 30;	
		case DIAS45:
			return 45;
		case DIAS60:
			return 60;
		case MESES3:
			return 90;
		case MESES6:
			return 120;	
		default:
			return 0;
		}		
	}

}
