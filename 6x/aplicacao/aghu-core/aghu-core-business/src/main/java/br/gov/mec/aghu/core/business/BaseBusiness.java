package br.gov.mec.aghu.core.business;

import java.io.Serializable;
import java.security.Principal;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.security.SecurityContextAssociation;

import br.gov.mec.aghu.casca.autenticacao.AghuQuartzPrincipal;
import br.gov.mec.aghu.core.business.moduleintegration.HospitalQualifier;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.exception.UsuarioNaoLogadoException;
import br.gov.mec.aghu.core.messages.MessagesUtils;
import br.gov.mec.aghu.core.persistence.BaseEntity;
import br.gov.mec.aghu.core.persistence.dao.DataAccessService;

/**
 * SuperClasse de todas as ONs e RNS do sistema. Injeta o serviço de acesso a
 * dados e declara os métodos de log para compatibilidade com código na
 * arquitetura antiga.
 * 
 */
public abstract class BaseBusiness implements Serializable {
	/**
	 * 
	 */
	private static final Log LOG = LogFactory.getLog(BaseBusiness.class);
	private static final long serialVersionUID = 4294710148867519816L;

	/**
	 * Classe que oferece os serviços de acesso a base de dados.
	 */
	@Inject
	protected DataAccessService dataAcessService;
	
	@Inject
	private MessagesUtils messagesUtils;
	
	@Resource
	protected SessionContext ctx;
	
	@Inject @HospitalQualifier 
	private Boolean isHCPA;
	
	protected void flush(){
		this.dataAcessService.flush();
	}
	
    protected <E extends BaseEntity>  void  evict(E entidade){
		this.dataAcessService.evict(entidade);
	}
    
    protected void clear(){
    	this.dataAcessService.entityManagerClear();
    }
    
    public <T extends BaseEntity> void refresh(T entity) {
    	this.dataAcessService.refresh(entity);
    }
    
	/**
	 * Retorna o contexto EJB.
	 * 
	 * @return
	 */
	protected SessionContext getSessionContext() {
		return ctx;
	}
	
	protected String obterLoginUsuarioLogado() {
		Principal p = ctx.getCallerPrincipal();
		if (p == null){
			throw new UsuarioNaoLogadoException();
		}
		if (p.getName().equalsIgnoreCase("anonymous")) {
			Principal principal = SecurityContextAssociation.getPrincipal();
			if(principal instanceof AghuQuartzPrincipal) {
				LOG.info("Retornando usuario apartir do SecurityContextAssociantion e nao do contexto do servidor. Usuario :" + principal);
				return principal.getName();
			} else {
				throw new UsuarioNaoLogadoException();
			}
			
		}
		return p.getName();
	}

	

	/**
	 * Obtem o logger para esta classe. <br/>
	 * Cada subclasse deve definir um atributo do tipo Log na forma:
	 * 
	 * <pre>
	 * private static final Log logger = LogFactory.getLog(this.getClass())
	 * </pre>
	 * 
	 * Este método deve ser sobrescrito em casa subclasse, de forma a retornar
	 * este atributo.
	 * 
	 * @return o logger para esta classe
	 */
	@Deprecated
	protected abstract Log getLogger();

	/**
	 * Verifica de o nivel de log info está habilitado. Não usar este método na
	 * arquitetura nova. Chamar diretamente o log estático criado em casa
	 * subclasse.
	 * 
	 * @return boolean indicando se o log de info está habilitado.
	 */
	@Deprecated
	protected boolean isInfoEnabled() {
		return this.getLogger().isInfoEnabled();
	}

	/**
	 * Verifica de o nivel de log warn está habilitado. Não usar este método na
	 * arquitetura nova. Chamar diretamente o log estático criado em casa
	 * subclasse.
	 * 
	 * @return boolean indicando se o log de warn está habilitado.
	 */
	@Deprecated
	protected boolean isWarnEnabled() {
		return this.getLogger().isWarnEnabled();
	}

	/**
	 * Verifica de o nivel de log error está habilitado. Não usar este método na
	 * arquitetura nova. Chamar diretamente o log estático criado em casa
	 * subclasse.
	 * 
	 * @return boolean indicando se o log de error está habilitado.
	 */
	@Deprecated
	protected boolean isErrorEnabled() {
		return this.getLogger().isErrorEnabled();
	}

	/**
	 * Loga a mensagem passada por parâmetro no nivel debug, se este estiver
	 * habilitado. Não usar este método na arquitetura nova. Chamar diretamente
	 * o log estático criado em casa subclasse.
	 * 
	 * @param mensagem
	 *            a ser logada.
	 */
	@Deprecated
	protected void logDebug(Object arg0) {
		this.getLogger().debug(arg0);
	}

	/**
	 * Loga a mensagem passada por parâmetro no nivel info, se este estiver
	 * habilitado. Não usar este método na arquitetura nova. Chamar diretamente
	 * o log estático criado em casa subclasse.
	 * 
	 * @param mensagem
	 *            a ser logada.
	 */
	@Deprecated
	protected void logInfo(Object arg0) {
		this.getLogger().info(arg0);
	}

	/**
	 * Loga a mensagem passada por parâmetro no nivel warn, se este estiver
	 * habilitado. Não usar este método na arquitetura nova. Chamar diretamente
	 * o log estático criado em casa subclasse.
	 * 
	 * @param mensagem
	 *            a ser logada.
	 */
	@Deprecated
	protected void logWarn(Object arg0, Throwable arg1) {
		this.getLogger().warn(arg0, arg1);
	}

	/**
	 * Loga a mensagem passada por parâmetro no nivel warn, se este estiver
	 * habilitado. Não usar este método na arquitetura nova. Chamar diretamente
	 * o log estático criado em casa subclasse.
	 * 
	 * @param mensagem
	 *            a ser logada.
	 */
	@Deprecated
	protected void logWarn(Object arg0) {
		this.getLogger().warn(arg0);
	}

	/**
	 * Loga a mensagem passada por parâmetro no nivel error, se este estiver
	 * habilitado. Não usar este método na arquitetura nova. Chamar diretamente
	 * o log estático criado em casa subclasse.
	 * 
	 * @param mensagem
	 *            a ser logada.
	 */
	@Deprecated
	protected void logError(Object arg0, Throwable arg1) {
		this.getLogger().error(arg0, arg1);
	}

	/**
	 * Loga a mensagem passada por parâmetro no nivel error, se este estiver
	 * habilitado. Não usar este método na arquitetura nova. Chamar diretamente
	 * o log estático criado em casa subclasse.
	 * 
	 * @param mensagem
	 *            a ser logada.
	 */
	@Deprecated
	protected void logError(Object arg0) {
		this.getLogger().error(arg0);
	}
	
	protected String getResourceBundleValue(String key) {
		return messagesUtils.getResourceBundleValue(key);		
	}
	
	protected String getResourceBundleValue(BaseException exc) {
		return messagesUtils.getResourceBundleValue(exc);
	}
	
	protected String getResourceBundleValue(String key, Object... params) {
		return messagesUtils.getResourceBundleValue(key, params);
	}	
	
	
	protected ApplicationBusinessException createMessage(Severity severity, String keyMessage, Object... params) {
		return new ApplicationBusinessException(keyMessage, severity, params);
	}
	
	protected void generateMessage(Severity severity, String keyMessage, Object... params) throws ApplicationBusinessException {
		throw new ApplicationBusinessException(keyMessage, severity, params);
	}
	
	
	protected Boolean isHCPA() {
		return isHCPA;
	}


	// TODO migracao -> Precisa implementar esse método certo. É preciso ajustar o enum para dividir
	//        variáveis para o contexto do EJB e as de Sessão.
	@Deprecated
	protected Object obterContextoSessao(String var) {
		throw new UnsupportedOperationException();
	}	
	
	//Métodos definitivo para passar parâmetros entre os EJBs
	@Deprecated
	public void atribuirContexto(String param, Object valor){
		throw new UnsupportedOperationException();
	}
	
	@Deprecated
	public Object obterContexto(String param){
		throw new UnsupportedOperationException();
	}
	
	
	//TODO Arquitetura -> Precisa implementar esse método certo. É preciso ajustar o enum para dividir
		//        variáveis para o contexto do EJB e as de Sessão.
	@Deprecated
	protected void removerContextoSessao(Enum var) {
		throw new UnsupportedOperationException();
	}
	
	//TODO Arquitetura -> Precisa implementar esse método certo. É preciso ajustar o enum para dividir
	//        variáveis para o contexto do EJB e as de Sessão.
	@Deprecated
	protected void atribuirContextoSessao(String key, Object value) {
		throw new UnsupportedOperationException();
	}
	
	
	//TODO Arquitetura -> Precisa implementar esse método certo. É preciso ajustar o enum para dividir
	//        variáveis para o contexto do EJB e as de Sessão.
	@Deprecated
	protected void atribuirContextoSessao(Enum var, Object value) {
		throw new UnsupportedOperationException();
	}		

	//TODO Arquitetura -> Precisa implementar esse método certo. É preciso ajustar o enum para dividir
	//        variáveis para o contexto do EJB e as de Sessão.
	@Deprecated
	protected Object obterDoContexto(Class clazz, String value) {
		throw new UnsupportedOperationException();
	}	

	
	//TODO Arquitetura -> Precisamos remover esse método, não se faz necessário.
	@Deprecated
	protected void atribuirContextoConversacao(String param, Object value){
		throw new UnsupportedOperationException();
	}
	
	//TODO Arquitetura -> Precisamos remover esse método
	@Deprecated
	protected Object buscarNosContextos(String nomeAtributo) {
		throw new UnsupportedOperationException();
	}
	
}
