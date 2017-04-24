package br.gov.mec.aghu.casca.business;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.inject.Qualifier;


/**
 * Qualificador para identificar o produtor para gerenciamentos de usuarios por AD / LDAP.
 * 
 * @author corvalao
 */
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD, ElementType.FIELD,
		ElementType.PARAMETER })
public @interface GerenciadorUsuariosQualifier {

}
