package br.gov.mec.aghu.casca.business;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.core.commons.ParametrosSistema;

@ApplicationScoped
public class GerenciadorUsuariosProducer {

	private static final Log LOG = LogFactory.getLog(GerenciadorUsuariosProducer.class);

	private static final String GERENCIADOR_USUARIO_AD = "AD";
	private static final String GERENCIADOR_USUARIO_LDAP = "LDAP";

	@Inject
	private ParametrosSistema parametros;
	
	private IGerenciadorUsuarios gerenciadorUsuarios;

	@Produces @GerenciadorUsuariosQualifier
	public IGerenciadorUsuarios construirGerenciadorUsuario() {
		String usuarioTipo = parametros.getParametro("ldap_ad");
		LOG.info("Usando: " + usuarioTipo);
		
		if (GERENCIADOR_USUARIO_AD.equalsIgnoreCase(usuarioTipo)) {
			return loadAD();
		} else if (GERENCIADOR_USUARIO_LDAP.equalsIgnoreCase(usuarioTipo)) {
			return loadLDAP();			
		} else {
			LOG.error("\n\nTipo - ldap_ad - definido incorretamente no app_parameters!!! Usando LDAP.\n\n");
			return loadLDAP();
		}
	}

	private IGerenciadorUsuarios loadAD() {
		if (gerenciadorUsuarios == null) {
			gerenciadorUsuarios = new GerenciadorUsuariosDelegateAD(this.parametros);
		}
		return gerenciadorUsuarios;
	}

	private IGerenciadorUsuarios loadLDAP() {
		if (gerenciadorUsuarios == null) {
			gerenciadorUsuarios = new GerenciadorUsuariosDelegateLDAP(this.parametros);
		}
		return gerenciadorUsuarios;
	}
	
	
}
