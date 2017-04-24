package br.gov.mec.aghu.model;

import org.hibernate.cfg.Configuration;
import org.hibernate.tool.hbm2ddl.SchemaExport;

public class GerarTabelasMain {

	public static void main(String[] args) {
		 Configuration cfg = new Configuration();
		    //Entidades do modelo sendo adicionadas na configuração
		    cfg.addAnnotatedClass(AghSistemas.class);
		   // cfg.addAnnotatedClass(Clientes.class);
		       
		    SchemaExport schemaExport = new SchemaExport(cfg);
		    schemaExport.create(true, false);

	}

}
