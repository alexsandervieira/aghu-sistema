package br.gov.mec.aghu.casca.business;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.core.commons.ParametrosSistema;
import br.gov.mec.aghu.core.seguranca.HashGenerationException;

/**
 * Classe responsável por operações de segurança relacionadas aos usuários do
 * sistema no AD.
 * 
 * @author rcorvalao
 * 
 */
public class GerenciadorUsuariosDelegateAD extends GerenciadorUsuariosDelegateLDAP {
	
	private static final Log LOG = LogFactory.getLog(GerenciadorUsuariosDelegateAD.class);
	
	private String domain = null; //hcpa
	
	
	
	public GerenciadorUsuariosDelegateAD(ParametrosSistema parametros) {
		inicializarAD(parametros);
	}
	
	@SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.NPathComplexity"})
	private void inicializarAD(ParametrosSistema parametros) {
//		initAdBindDN();		
		String parametroAdBindDN = parametros.getParametro("ad_bind_DN");	
		if (parametroAdBindDN != null && !parametroAdBindDN.isEmpty()) {
			this.setBindDN(parametroAdBindDN);
		}
//		initBindCredentials();		
		String parametroBindCredentials= parametros.getParametro("ad_bind_credentials");
		if (parametroBindCredentials != null && !parametroBindCredentials.isEmpty()) {
			this.setBindCredentials(parametroBindCredentials);
		}
//		initServerAddress();		
		String parametroServerAddress= parametros.getParametro("ad_server_address");
		if (parametroServerAddress != null && !parametroServerAddress.isEmpty()) {
			this.setServerAddress(parametroServerAddress);
		}
//		initServerPort();		
		String parametroServerPort= parametros.getParametro("ad_server_port");	
		if (parametroServerPort != null && !parametroServerPort.isEmpty()) {
			this.setServerPort(Integer.parseInt(parametroServerPort));
		}
//		initUserNameAttribute();	
		String parametroUserNameAttribute= parametros.getParametro("ad_user_name_attribute");
		if (parametroUserNameAttribute != null && !parametroUserNameAttribute.isEmpty()) {
			this.setUserNameAttribute(parametroUserNameAttribute);
		}
//		initUserObjectClasses();		
		String parametroUserObjectClasses= parametros.getParametro("ad_user_object_classes");
		if (parametroUserObjectClasses != null && !parametroUserObjectClasses.isEmpty()) {
			this.setUserObjectClasses(parametroUserObjectClasses.replaceAll(" ", "").split(","));
		}
//		initObjectClassAttribute();		
		String parametroObjectClassAttribute= parametros.getParametro("ad_object_class_attribute");	
		if (parametroObjectClassAttribute != null && !parametroObjectClassAttribute.isEmpty()) {
			this.setObjectClassAttribute(parametroObjectClassAttribute);
		}
//		initUserContextDN();		
		String parametroUserContextDN= parametros.getParametro("ad_user_context_DN");	
		if (parametroUserContextDN != null && !parametroUserContextDN.isEmpty()) {
			this.setUserContextDN(parametroUserContextDN);
		}
//		initUserObjectCategories();		
		String parametroUserObjectCategories= parametros.getParametro("ad_user_object_categories");	
		if (parametroUserObjectCategories != null && !parametroUserObjectCategories.isEmpty()) {
			this.setUserObjectCategories(parametroUserObjectCategories.replaceAll(" ", "").split(","));
		}
//		initObjectCategoryAttribute();		
		String parametroObjectCategoryAttribute= parametros.getParametro("ad_object_category_attribute");	
		if (parametroObjectCategoryAttribute != null && !parametroObjectCategoryAttribute.isEmpty()) {
			this.setObjectCategoryAttribute(parametroObjectCategoryAttribute);
		}
		
		String parametrouServerSecurityPort = parametros.getParametro("ad_server_security_port");
		if (parametrouServerSecurityPort != null && !parametrouServerSecurityPort.isEmpty()) {
			this.setServerSecurityPort(parametrouServerSecurityPort);
		}
		String parametrouServerSecurityProtocol = parametros.getParametro("ad_server_security_protocol");
		if (parametrouServerSecurityProtocol != null && !parametrouServerSecurityProtocol.isEmpty()) {
			this.setServerSecurityProtocol(parametrouServerSecurityProtocol);
		}
		String parametroServerProtocol = parametros.getParametro("ad_server_protocol");
		if (parametroServerProtocol != null && !parametroServerProtocol.isEmpty()) {
			this.setServerProtocol(parametroServerProtocol);
		}
		String parametroUserPasswordAttribute = parametros.getParametro("ad_user_pwd_attribute");
		if (parametroUserPasswordAttribute != null && !parametroUserPasswordAttribute.isEmpty()) {
			this.setUserPasswordAttribute(parametroUserPasswordAttribute);
		}
		
		String parametroDomain= parametros.getParametro("ad_domain");	
		if (parametroDomain != null && !parametroDomain.isEmpty()) {
			this.domain =  parametroDomain;
		}
	}
	
	@Override
	protected String getUserDN(String username) {
		LOG.info("userDN para AD");
		return username + "@" + domain;
	}
	
	protected String getEncryptedPassword(String message) throws HashGenerationException {
		throw new UnsupportedOperationException("Encriptacao de senha para AD nao implementada!"); 
	}
	
}



