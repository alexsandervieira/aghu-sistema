package br.gov.mec.aghu.estoque.ejb;

import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.jboss.ejb3.annotation.TransactionTimeout;

import br.gov.mec.aghu.core.business.BaseFacade;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.estoque.dao.SceReqMateriaisDAO;
import br.gov.mec.aghu.model.SceReqMaterial;

@Stateless
@SuppressWarnings({"PMD.PackagePrivateBaseBusiness","PMD.AtributoEmSeamContextManager"})
public class EfetivarRequisicaoMaterialBean extends BaseFacade implements EfetivarRequisicaoMaterialBeanLocal {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5506145209111954031L;
	
	@EJB
	private IEstoqueFacade estoqueFacade;
	
	@Inject
	private SceReqMateriaisDAO sceReqMateriaisDAO;
		
	@Resource
	protected SessionContext ctx;
	
	public enum EfetivarRequisicaoMaterialBeanExceptionCode implements BusinessExceptionCode {
		ERRO_AO_TENTAR_EFETIVAR_REQUISICAO_MATERIAL;
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	@TransactionTimeout(value=20, unit=TimeUnit.MINUTES)
	public void efetivarRequisicaoMaterial(SceReqMaterial sceReqMateriais, String nomeMicrocomputador) throws BaseException {
		
		try {
		
			getEstoqueFacade().efetivarRequisicaoMaterial(sceReqMateriais, nomeMicrocomputador);
			getReqMateriaisDAO().flush();
		
		} catch (BaseException e) {
			this.ctx.setRollbackOnly();
			throw e;
		} catch (Exception e) {
			this.ctx.setRollbackOnly();
			throw new ApplicationBusinessException(EfetivarRequisicaoMaterialBeanExceptionCode.ERRO_AO_TENTAR_EFETIVAR_REQUISICAO_MATERIAL);
		}
	
	}
	
	protected IEstoqueFacade getEstoqueFacade() {
		return this.estoqueFacade;
	}

	protected SceReqMateriaisDAO getReqMateriaisDAO() {
		return sceReqMateriaisDAO;
	}

}
