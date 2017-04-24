package br.gov.mec.aghu.casca.business;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.SizeLimitExceededException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.casca.exceptioncode.AdGerenciadorUsuariosExceptionCode;
import br.gov.mec.aghu.core.commons.ParametrosSistema;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.seguranca.HashGenerationException;
import br.gov.mec.aghu.core.seguranca.HashGenerator;

public class GerenciadorUsuariosDelegateLDAP implements IGerenciadorUsuarios {
	
	private static final Log LOG = LogFactory.getLog(GerenciadorUsuariosDelegateLDAP.class);
	
	private static final String LDAP_BOOLEAN_TRUE = "TRUE";
	

	private String serverAddress = "localhost"; // omega
	private int serverPort = 389; //  3268
	private String userDNPrefix = "uid="; //"cn="
	private String userDNSuffix = ",ou=Person,dc=acme,dc=com"; // ",dc=aghu"
	private String enabledAttribute = null;
	private String bindDN = "cn=Manager,dc=acme,dc=com"; //"aghuldap@hcpa"
	private String bindCredentials = "secret"; // "asclepios@2011"
	private String userContextDN = "ou=Person,dc=acme,dc=com";  // "DC=hcpa";
	private String[] userObjectClasses = { "person", "uidObject" } ;//{ "organizationalPerson" }
	private String userNameAttribute = "sAMAccountName"; //  sAMAccountName
	
	private String serverSecurityPort="636";
	private String serverSecurityProtocol="ldaps";
	private String serverProtocol="ldap";
	private String userPasswordAttribute;
	
	/**
	 * Time limit for LDAP searches, in milliseconds
	 */
	private int searchTimeLimit = 10000;
	private String[] userObjectCategories;
	private String objectCategoryAttribute = "objectCategory";
	private int searchScope = SearchControls.SUBTREE_SCOPE;
	private String objectClassAttribute = "objectClass";

	
	
	public GerenciadorUsuariosDelegateLDAP() {
	}
	
	public GerenciadorUsuariosDelegateLDAP(ParametrosSistema parametros) {
		inicializarLDAP(parametros);
	}
	
	@SuppressWarnings({"PMD.NPathComplexity"})
	private void inicializarLDAP(ParametrosSistema parametros) {
		// initAdBindDN();		
		String parametroAdBindDN = parametros.getParametro("ldap_bind_DN");	
		if (parametroAdBindDN != null && !parametroAdBindDN.isEmpty()) {
			this.setBindDN(parametroAdBindDN);
		}
		// initBindCredentials();		
		String parametroBindCredentials= parametros.getParametro("ldap_bind_credentials");
		if (parametroBindCredentials != null && !parametroBindCredentials.isEmpty()) {
			this.setBindCredentials(parametroBindCredentials);
		}
		// initUserContextDN();		
		String parametroUserContextDN= parametros.getParametro("ldap_user_context_DN");	
		if (parametroUserContextDN != null && !parametroUserContextDN.isEmpty()) {
			this.setUserContextDN(parametroUserContextDN);
		}
		// initUserObjectClasses();		
		String parametroUserObjectClasses= parametros.getParametro("ldap_user_object_classes");
		if (parametroUserObjectClasses != null && !parametroUserObjectClasses.isEmpty()) {
			this.setUserObjectClasses(parametroUserObjectClasses.replaceAll(" ", "").split(","));
		}
		// initUserNameAttribute();	
		String parametroUserNameAttribute= parametros.getParametro("ldap_user_name_attribute");
		if (parametroUserNameAttribute != null && !parametroUserNameAttribute.isEmpty()) {
			this.setUserNameAttribute(parametroUserNameAttribute);
		}
		
		String pServerAddress = parametros.getParametro("ldap_server_address");
		if (pServerAddress != null && !pServerAddress.isEmpty()) {
			this.serverAddress = pServerAddress;
		}
		String pServerPort = parametros.getParametro("ldap_server_port");
		if (pServerPort != null && !pServerPort.isEmpty()) {
			this.serverPort = Integer.valueOf(pServerPort);
		}
		String parametroUserDNPrefix = parametros.getParametro("ldap_user_DN_prefix");
		if (parametroUserDNPrefix != null && !parametroUserDNPrefix.isEmpty()) {
			this.userDNPrefix =  parametroUserDNPrefix;
		}		
		String parametrouUserDNSuffix = parametros.getParametro("ldap_user_DN_suffix");
		if (parametrouUserDNSuffix != null && !parametrouUserDNSuffix.isEmpty()) {
			this.userDNSuffix =  parametrouUserDNSuffix;
		}
		
		String parametrouServerSecurityPort = parametros.getParametro("ldap_server_security_port");
		if (parametrouServerSecurityPort != null && !parametrouServerSecurityPort.isEmpty()) {
			this.setServerSecurityPort(parametrouServerSecurityPort);
		}
		String parametrouServerSecurityProtocol = parametros.getParametro("ldap_server_security_protocol");
		if (parametrouServerSecurityProtocol != null && !parametrouServerSecurityProtocol.isEmpty()) {
			this.setServerSecurityProtocol(parametrouServerSecurityProtocol);
		}
		String parametroServerProtocol = parametros.getParametro("ldap_server_protocol");
		if (parametroServerProtocol != null && !parametroServerProtocol.isEmpty()) {
			this.setServerProtocol(parametroServerProtocol);
		}
		String parametroUserPasswordAttribute = parametros.getParametro("ldap_user_pwd_attribute");
		if (parametroUserPasswordAttribute != null && !parametroUserPasswordAttribute.isEmpty()) {
			this.setUserPasswordAttribute(parametroUserPasswordAttribute);
		}
		
		String parametroEnabledAttribute = parametros.getParametro("ldap_enabled_attribute");
		if (parametroEnabledAttribute != null && !parametroEnabledAttribute.isEmpty()) {
			this.enabledAttribute =  parametroEnabledAttribute;
		}
	
	}
	
	/**
	 * Inicializa um contexto seguro. O método de inicialização de contexto
	 * original, da classe LDAPIdentityStore, cria apenas conexões não seguras.
	 * Como o AD necessita de uma conexão segura para algumas operações, este
	 * método é utilizado no lugar do original.
	 * 
	 * @return Um contexto que utiliza uma conexão segura.
	 * @throws NamingException
	 */
	protected InitialLdapContext initialiseSecureContext() throws NamingException {
		LOG.info("Conectando de forma segura com AD/LDAP ...");
		Properties env = new Properties();

		env.setProperty(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		env.setProperty(Context.SECURITY_AUTHENTICATION, "simple");
		
		String providerUrl = String.format("%s://%s:%s", getServerSecurityProtocol(), getServerAddress(), getServerSecurityPort());
		env.setProperty(Context.PROVIDER_URL, providerUrl);

		env.setProperty(Context.SECURITY_PRINCIPAL, getBindDN());
		env.setProperty(Context.SECURITY_CREDENTIALS, getBindCredentials());
		env.setProperty(Context.SECURITY_PROTOCOL, "ssl");

		return new InitialLdapContext(env, null);
	}
	
	protected InitialLdapContext initialiseContext(String principal, String credentials) throws NamingException {
		LOG.info("Conectando com AD/LDAP ...");
		Properties env = new Properties();

		env.setProperty(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		env.setProperty(Context.SECURITY_AUTHENTICATION, "simple");

		String providerUrl = String.format("%s://%s:%d", getServerProtocol(), serverAddress, serverPort);
		env.setProperty(Context.PROVIDER_URL, providerUrl);

		env.setProperty(Context.SECURITY_PRINCIPAL, principal);
		env.setProperty(Context.SECURITY_CREDENTIALS, credentials);

		return new InitialLdapContext(env, null);
	}
	
	protected String getUserDN(String username) {
		LOG.info("userDN para LDAP");
		return String.format("%s%s%s", getUserDNPrefix(), username,
			getUserDNSuffix());
	}
	
	
	
	
	@Override
	public List<String> listarLoginsRegistrados(String filtro) {
		List<String> users = this.listUsers(filtro);

		Collections.sort(users, new Comparator<String>() {
			public int compare(String value1, String value2) {
				return value1.compareTo(value2);
			}
		});

		return users;
	}

	/**
	 * Metodo adaptado do LdapIdentityStore. Foi feita uma alteração no filtro
	 * de pesquisa, para incluir também o nome do usuario na String de pesquisa,
	 * para que os resultados vindos do AD ja venham filtrados. No algorítimo
	 * original, todos os usuários eram recuperados e o filtro era feito dentro
	 * do método.
	 * 
	 * Provavelmente o filtro era feito dentro do método para tratar caracteres
	 * maiúsculos e minúsculos. Mas como no AD a pesquisa é case insensitive,
	 * isto não é necessário.
	 * 
	 * Este novo método resolve o problema de limite máximo de registros que
	 * acontecia no algoritmo original do LdapIdentityStore
	 * 
	 * @see LdapIdentityStore#listUsers(String)
	 */
	protected List<String> listUsers(String filter) {
		List<String> users = new ArrayList<String>();

		InitialLdapContext ctx = null;
		try {
			ctx = initialiseContext(getBindDN(), getBindCredentials());

			if (LOG.isDebugEnabled()) {
				LOG.debug("Listando os usuários para o filtro: " + filter);
			}

			String[] userAttr = { getUserNameAttribute() };

			SearchControls controls = new SearchControls();
			// FIXME Scope forcado. Deveria vir dos parametros em components.xml
			controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
			controls.setReturningAttributes(userAttr);
			controls.setTimeLimit(getSearchTimeLimit());

			StringBuilder userFilter = new StringBuilder("(&");

			for (int i = 0; i < getUserObjectClasses().length; i++) {
				userFilter.append(String.format("(%s=%s)",
						getObjectClassAttribute(), getUserObjectClasses()[i]));
			}
			
			if (getUserObjectCategories() != null) {
				for (int i = 0; i < getUserObjectCategories().length; i++) {
					userFilter.append(String.format("(%s=%s)",
							getUserObjectCategories()[i]));
				}
			}

			filter = StringUtils.isBlank(filter) ? "*" : "*" + filter + "*";
			userFilter.append(String.format("(%s=%s)", getUserNameAttribute(),filter)).append(')');

			NamingEnumeration answer = ctx.search(getUserContextDN(),
					userFilter.toString(), controls);
			while (answer.hasMoreElements()) {
				SearchResult sr = (SearchResult) answer.next();
				Attributes attrs = sr.getAttributes();
				Attribute user = attrs.get(getUserNameAttribute());

				users.add(user.get().toString());
			}

			answer.close();

			return users;

			// Se o limite for atingido, retornar os ja recuperados.
		} catch (SizeLimitExceededException e) {
			LOG.error("Limite de dados consultados excedido, retornando quantidade de dados permitidos ("
					+ e.getExplanation() + ").");
			return users;
		} catch (NamingException e) {
			throw new BaseRuntimeException(
					AdGerenciadorUsuariosExceptionCode.ERRO_LISTAR_USUARIOS, e);
		} finally {
			if (LOG.isDebugEnabled()) {
				LOG.debug("Retornando [" + users.size()
						+ "] do Active Directory.");
			}
			if (ctx != null) {
				try {
					ctx.close();
				} catch (NamingException ex) {
					LOG.error("Erro ao tentar fechar contexto: ", ex);
				}
			}
		}
	}
	
	protected String getEncryptedPassword(String message) throws HashGenerationException {
		return "{MD5}" + new HashGenerator().generateMD5(message); 
	}

	/**
	 * Altera a senha do usuário no Active Directory
	 * 
	 * @throws ApplicationBusinessException
	 * 
	 * @see LdapIdentityStore#changePassword(String, String)
	 */
	@Override
	public void changePassword(String login, String password) throws ApplicationBusinessException {
		InitialLdapContext context = null;
		try {
			context = initialiseSecureContext();

			String userDN = findUserDN(context, login);

			if (userDN == null) {
				throw new ApplicationBusinessException(
						AdGerenciadorUsuariosExceptionCode.ERRO_ALTERAR_SENHA_USUARIO
						, "UserDN para usuário: " + login);
			}

//			Attributes passwdAttribs = new BasicAttributes();
//			passwdAttribs.put(new BasicAttribute("unicodePwd", ("\"" + password + "\"").getBytes("UTF-16LE")));
//			context.modifyAttributes(userDN, DirContext.REPLACE_ATTRIBUTE, passwdAttribs);			
			ModificationItem[] mods = new ModificationItem[1];
			mods[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE,
	                  new BasicAttribute(getUserPasswordAttribute(), getEncryptedPassword(password)));
			
			context.modifyAttributes(userDN, mods);
		} catch (NamingException e) {
			LOG.error("Erro ao alterar a senha",e);
			throw new ApplicationBusinessException(
					AdGerenciadorUsuariosExceptionCode.ERRO_ALTERAR_SENHA_USUARIO,
					e.getMessage());
		} catch (HashGenerationException e) {
			LOG.error("Erro ao alterar a senha", e);
			throw new ApplicationBusinessException(
					AdGerenciadorUsuariosExceptionCode.ERRO_ALTERAR_SENHA_USUARIO,
					e.getMessage());
		} finally {
			if (context != null) {
				try {
					context.close();
				} catch (NamingException ex) {
					LOG.error("Erro ao tentar fechar contexto: ", ex);
				}
			}
		}
	}

	/**
	 * Recupera o UserDN de um usuario de acordo com o login. Como os métodos de
	 * manipulação de usuário em um LDAP e como no AD o login está armazenado no
	 * atributo sAMAccountName, é necessário que seja feita uma pesquisa para
	 * recurar o DN verdadeiro deste usuário.
	 * 
	 * @param context
	 *            DirContext já devidamente aberto
	 * @param login
	 *            Login do usuário a ser pesquisado
	 * @return o DN do usuário
	 * @throws NamingException
	 */
	protected String findUserDN(DirContext context, String login)
			throws NamingException {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Executando busca pelo DN para o login [" + login + "]");
		}
		SearchControls controls = new SearchControls();
		controls.setSearchScope(SearchControls.SUBTREE_SCOPE);

		String userFilter = String.format("(&(%s=%s))", getUserNameAttribute(),
				login);

		NamingEnumeration<SearchResult> answer = context.search(
				getUserContextDN(), userFilter, controls);

		String userDN = null;

		if (answer.hasMoreElements()) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("Encontrou vários elementos...");
			}
			SearchResult result = (SearchResult) answer.next();

			userDN = result.getNameInNamespace();
		}

		answer.close();
		return userDN;
	}

	@Override
	public boolean authenticate(String username, String password)
			throws ApplicationBusinessException {
		final String securityPrincipal = getUserDN(username);

		InitialLdapContext ctx = null;
		try {
			ctx = initialiseContext(securityPrincipal, password);

			if (getEnabledAttribute() != null) {
				Attributes attribs = ctx.getAttributes(securityPrincipal,
						new String[] { getEnabledAttribute() });
				Attribute enabledAttrib = attribs.get(getEnabledAttribute());
				if (enabledAttrib != null) {
					for (int r = 0; r < enabledAttrib.size(); r++) {
						Object value = enabledAttrib.get(r);
						if (LDAP_BOOLEAN_TRUE.equals(value)){
							return true;
						}
					}
				}
				return false;
			}

			return true;
		} catch (NamingException ex) {
			throw new ApplicationBusinessException(
					AdGerenciadorUsuariosExceptionCode.ERRO_AUTENTICACAO, ex);
		} finally {
			if (ctx != null) {
				try {
					ctx.close();
				} catch (NamingException ex) {
					LOG.error("Erro ao feixar contexto ldap",ex);
				}
			}
		}
	}

	
	
	
	
	
	public String getBindDN() {
		return bindDN;
	}

	public void setBindDN(String bindDN) {
		this.bindDN = bindDN;
	}

	public String getBindCredentials() {
		return bindCredentials;
	}

	public void setBindCredentials(String bindCredentials) {
		this.bindCredentials = bindCredentials;
	}

	public String getServerAddress() {
		return serverAddress;
	}

	public void setServerAddress(String serverAddress) {
		this.serverAddress = serverAddress;
	}

	public int getServerPort() {
		return serverPort;
	}

	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}

	public String getUserNameAttribute() {
		return userNameAttribute;
	}

	public void setUserNameAttribute(String userNameAttribute) {
		this.userNameAttribute = userNameAttribute;
	}
	
	public String[] getUserObjectClasses() {
		return userObjectClasses;
	}

	public void setUserObjectClasses(String[] userObjectClasses) {
		this.userObjectClasses = userObjectClasses;
	}
	
	public String getUserContextDN() {
		return userContextDN;
	}

	public void setUserContextDN(String userContextDN) {
		this.userContextDN = userContextDN;
	}
	
	public String getUserDNPrefix() {
		return userDNPrefix;
	}

	public void setUserDNPrefix(String userDNPrefix) {
		this.userDNPrefix = userDNPrefix;
	}

	public String getUserDNSuffix() {
		return userDNSuffix;
	}

	public void setUserDNSuffix(String userDNSuffix) {
		this.userDNSuffix = userDNSuffix;
	}

	public String getEnabledAttribute() {
		return enabledAttribute;
	}

	public void setEnabledAttribute(String enabledAttribute) {
		this.enabledAttribute = enabledAttribute;
	}

	public int getSearchTimeLimit() {
		return searchTimeLimit;
	}

	public void setSearchTimeLimit(int searchTimeLimit) {
		this.searchTimeLimit = searchTimeLimit;
	}

	public String[] getUserObjectCategories() {
		return userObjectCategories;
	}

	public void setUserObjectCategories(String[] userObjectCategories) {
		this.userObjectCategories = userObjectCategories;
	}

	public String getObjectCategoryAttribute() {
		return objectCategoryAttribute;
	}

	public void setObjectCategoryAttribute(String objectCategoryAttribute) {
		this.objectCategoryAttribute = objectCategoryAttribute;
	}

	public int getSearchScope() {
		return searchScope;
	}

	public void setSearchScope(int searchScope) {
		this.searchScope = searchScope;
	}

	public String getObjectClassAttribute() {
		return objectClassAttribute;
	}

	public void setObjectClassAttribute(String objectClassAttribute) {
		this.objectClassAttribute = objectClassAttribute;
	}

	public String getServerSecurityPort() {
		return serverSecurityPort;
	}

	public void setServerSecurityPort(String serverSecurityPort) {
		this.serverSecurityPort = serverSecurityPort;
	}

	public String getServerSecurityProtocol() {
		return serverSecurityProtocol;
	}

	public void setServerSecurityProtocol(String serverSecurityProtocol) {
		this.serverSecurityProtocol = serverSecurityProtocol;
	}

	public String getServerProtocol() {
		return serverProtocol;
	}

	public void setServerProtocol(String serverProtocol) {
		this.serverProtocol = serverProtocol;
	}
	

	public String getUserPasswordAttribute() {
		return userPasswordAttribute;
	}

	public void setUserPasswordAttribute(String userPasswordAttribute) {
		this.userPasswordAttribute = userPasswordAttribute;
	}


}
