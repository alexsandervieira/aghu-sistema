package br.gov.mec.aghu.business.prescricaoenfermagem;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.dominio.DominioSituacaoHistPrescDiagnosticos;
import br.gov.mec.aghu.model.EpeFatRelDiagnostico;
import br.gov.mec.aghu.model.EpeHistoricoPrescDiagnosticos;
import br.gov.mec.aghu.model.EpePrescCuidDiagnostico;
import br.gov.mec.aghu.model.EpePrescricaoEnfermagem;
import br.gov.mec.aghu.model.EpePrescricoesCuidados;
import br.gov.mec.aghu.model.EpePrescricoesCuidadosId;
import br.gov.mec.aghu.prescricaoenfermagem.dao.EpeHistoricoPrescDiagnosticosDAO;
import br.gov.mec.aghu.prescricaoenfermagem.dao.EpePrescCuidDiagnosticoDAO;
import br.gov.mec.aghu.prescricaoenfermagem.dao.EpePrescricoesCuidadosDAO;
import br.gov.mec.aghu.prescricaoenfermagem.vo.DiagnosticoEtiologiaVO;

/**
 * 
 * @author diego.pacheco
 *
 */
@Stateless
public class EncerramentoDiagnosticoON extends BaseBusiness {


	@EJB
	private ManutencaoPrescricaoCuidadoON manutencaoPrescricaoCuidadoON;
	
	private static final Log LOG = LogFactory.getLog(EncerramentoDiagnosticoON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
	return LOG;
	}

	@Inject
	private EpePrescCuidDiagnosticoDAO epePrescCuidDiagnosticoDAO;
	
	@Inject
	private EpePrescricoesCuidadosDAO epePrescricoesCuidadosDAO;
	
	@Inject
	private EpeHistoricoPrescDiagnosticosDAO epeHistoricoPrescDiagnosticosDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -3373519232355888633L;
	
	public enum EncerramentoDiagnosticoONExceptionCode implements BusinessExceptionCode {
		ERRO_REMOVER_PRESCRICAO_CUIDADO_COM_AUTO_RELACIONAMENTO;
	}
	
	@SuppressWarnings("deprecation")
	public void removerPrescCuidadosDiagnosticosSelecionados (List<DiagnosticoEtiologiaVO> listaDiagnosticoEtiologiaVO, 
			Integer penAtdSeq, Integer penSeq, Boolean excluir) throws ApplicationBusinessException {
		
		
		for (DiagnosticoEtiologiaVO diagnosticoEtiologiaVO : listaDiagnosticoEtiologiaVO) {

			if (diagnosticoEtiologiaVO.getSelecionado()) {
				
				List<EpePrescCuidDiagnostico> listaPrescCuidDiagnostico = epePrescCuidDiagnosticoDAO
						.listarPrescCuidDiagnosticoPorAtdSeqEDiagnostico(
								diagnosticoEtiologiaVO.getPrcAtdSeq(),
								diagnosticoEtiologiaVO.getCdgFdgDgnSnbGnbSeq(),
								diagnosticoEtiologiaVO.getCdgFdgDgnSnbSequencia(),
								diagnosticoEtiologiaVO.getCdgFdgDgnSequencia(),
								diagnosticoEtiologiaVO.getCdgFdgFreSeq(), penAtdSeq, penSeq);
				
				if(excluir){
					manutencaoPrescricaoCuidadoON.validarHistoricoExlusaoPrescDiagnostico(listaPrescCuidDiagnostico.get(0).getPrescricaoCuidado().getPrescricaoEnfermagem(),
							diagnosticoEtiologiaVO.getCdgFdgFreSeq(), diagnosticoEtiologiaVO.getCdgFdgDgnSequencia(), 
							diagnosticoEtiologiaVO.getCdgFdgDgnSnbSequencia(), diagnosticoEtiologiaVO.getCdgFdgDgnSnbGnbSeq());
				}else{
					gravarHistoricoEncerramento(diagnosticoEtiologiaVO, listaPrescCuidDiagnostico.get(0).getPrescricaoCuidado().getPrescricaoEnfermagem());
				}
				
				for (EpePrescCuidDiagnostico prescCuidDiagnostico : listaPrescCuidDiagnostico) {
					EpePrescricoesCuidadosId prescricaoCuidadoId = new EpePrescricoesCuidadosId();
					prescricaoCuidadoId.setSeq(prescCuidDiagnostico.getId().getPrcSeq());
					//Atributo setado para buscar a prescricao cuidado associada a prescricao cuidado diagnostico
					prescricaoCuidadoId.setAtdSeq(penAtdSeq);
					EpePrescricoesCuidados prescricaoCuidado = epePrescricoesCuidadosDAO.obterPorChavePrimaria(prescricaoCuidadoId);
					epePrescCuidDiagnosticoDAO.refresh(prescCuidDiagnostico);

					manutencaoPrescricaoCuidadoON.removerPrescricaoCuidado(prescricaoCuidado);
				}
			}
		}
	}

	private void gravarHistoricoEncerramento(DiagnosticoEtiologiaVO diagnosticoEtiologiaVO,	EpePrescricaoEnfermagem epePrescricaoEnfermagem) {
		
		EpeFatRelDiagnostico epeFatRelDiagnostico = manutencaoPrescricaoCuidadoON.obterEpeFatRelDiagnostico(diagnosticoEtiologiaVO.getCdgFdgFreSeq(), 
				diagnosticoEtiologiaVO.getCdgFdgDgnSequencia(),diagnosticoEtiologiaVO.getCdgFdgDgnSnbSequencia(), diagnosticoEtiologiaVO.getCdgFdgDgnSnbGnbSeq());
		
		EpeHistoricoPrescDiagnosticos historico = epeHistoricoPrescDiagnosticosDAO.obterEpeHistoricoPrescDiagnosticosPorPrescDiag(epeFatRelDiagnostico,
				epePrescricaoEnfermagem, true);
		if(historico!= null){
			historico.setSituacao(DominioSituacaoHistPrescDiagnosticos.E);
			epeHistoricoPrescDiagnosticosDAO.merge(historico);
			epeHistoricoPrescDiagnosticosDAO.flush();
		}
	}

}
