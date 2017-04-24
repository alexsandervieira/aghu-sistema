package br.gov.mec.aghu.estoque.ejb;

import java.io.Serializable;

import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.model.SceReqMaterial;

public interface EfetivarRequisicaoMaterialBeanLocal extends Serializable {

	void efetivarRequisicaoMaterial(SceReqMaterial sceReqMateriais, String nomeMicrocomputador) throws BaseException;

}
