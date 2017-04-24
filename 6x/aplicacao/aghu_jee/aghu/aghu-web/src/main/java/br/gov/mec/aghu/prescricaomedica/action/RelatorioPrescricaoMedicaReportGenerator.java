package br.gov.mec.aghu.prescricaomedica.action;

import static br.gov.mec.aghu.model.AipPacientes.VALOR_MAXIMO_PRONTUARIO;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.transaction.SystemException;

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.StreamedContent;

import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.constantes.TipoItemAprazamento;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.persistence.BaseEntity;
import br.gov.mec.aghu.core.report.DocumentoJasper;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.dominio.DominioPrioridadeCid;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioTipoDocumento;
import br.gov.mec.aghu.model.AbsSolicitacoesHemoterapicas;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghDocumentoCertificado;
import br.gov.mec.aghu.model.ItemPrescricaoMedica;
import br.gov.mec.aghu.model.MpmCidAtendimento;
import br.gov.mec.aghu.model.MpmPrescricaoCuidado;
import br.gov.mec.aghu.model.MpmPrescricaoDieta;
import br.gov.mec.aghu.model.MpmPrescricaoMdto;
import br.gov.mec.aghu.model.MpmPrescricaoMedica;
import br.gov.mec.aghu.model.MpmPrescricaoNpt;
import br.gov.mec.aghu.model.MpmPrescricaoProcedimento;
import br.gov.mec.aghu.model.MpmSolicitacaoConsultoria;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.prescricaomedica.constantes.EnumStatusItem;
import br.gov.mec.aghu.prescricaomedica.constantes.EnumTipoImpressao;
import br.gov.mec.aghu.prescricaomedica.vo.BuscaConselhoProfissionalServidorVO;
import br.gov.mec.aghu.prescricaomedica.vo.PosolociaDosagemMedicamentoVO;
import br.gov.mec.aghu.prescricaomedica.vo.RelatorioConfirmacaoItensPrescricaoVOPai;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.report.AghuReportGenerator;

import com.itextpdf.text.DocumentException;

public class RelatorioPrescricaoMedicaReportGenerator extends AghuReportGenerator {

	private static final String ALTERAR = "Alterar";

	private static final long serialVersionUID = 2415874135856208809L;
	
	private static final Log LOG = LogFactory.getLog(RelatorioPrescricaoMedicaReportGenerator.class);

	protected final String TIPO_IMPRESSAO = "TIPO_IMPRESSAO";
	
	private Boolean impressaoTotal = true;
	
	private MpmPrescricaoMedica prescricao;
	
	private String login;
	
	private EnumTipoImpressao tipoImpressao;
	
	private List<ItemPrescricaoMedica> itensDaPrescricaoConfirmacao;
	
	
	private Boolean contraCheque = false;

	private Boolean prescricaoMedicaRascunho = false;

	@EJB
	protected IParametroFacade parametroFacade;

	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	@EJB @SuppressWarnings("cdi-ambiguous-dependency")
	private IServidorLogadoFacade servidorLogadoFacade;
	
	private DocumentoJasper documentoJasper;
	
	RapServidores servidorValida;

	private Date dataMovimento;
	
	protected DocumentoJasper getDocumentoJasper() throws ApplicationBusinessException {
		return documentoJasper;
	}
	
	@Override
	protected Collection recuperarColecao() throws ApplicationBusinessException {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

		prescricao =  prescricaoMedicaFacade.obterPrescricaoComAtendimentoPaciente(prescricao.getId().getAtdSeq(),prescricao.getId().getSeq());
		
		RelatorioConfirmacaoItensPrescricaoVOPai voPai = new RelatorioConfirmacaoItensPrescricaoVOPai();
			
		String aprazamento = null;

		formatarItensPrescricaoMedica(voPai, aprazamento);

		voPai.ordernarListas();
		voPai.atribuirIndice();

		String validadePrescricao = "Validade: de "
				+ sdf.format(prescricao.getDthrInicio()) + " h. a "
				+ sdf.format(prescricao.getDthrFim()) + " h.";

		voPai.setDataValidadeInicial(validadePrescricao);
		voPai.setNomePaciente(prescricao.getAtendimento().getPaciente().getNome().toUpperCase());

		if (prescricao.getAtendimento().getPaciente().getProntuario() != null
				&& prescricao.getAtendimento().getPaciente().getProntuario() < VALOR_MAXIMO_PRONTUARIO) {
			voPai.setProntuarioPaciente(CoreUtil.formataProntuarioRelatorio(prescricao.getAtendimento().getPaciente().getProntuario().toString()));
		}
		voPai.setSequencialPrescricao(prescricao.getId().getSeq());

		// Dados do médico que está confirmando
		BuscaConselhoProfissionalServidorVO vo = null;
		if(prescricao.getServidorValida() == null && servidorValida != null){
			prescricao.setServidorValida(servidorValida);
		} else if (prescricao.getServidorValida() == null && servidorValida == null){
			prescricao.setServidorValida(servidorLogadoFacade.obterServidorLogado());
		}
		vo = prescricaoMedicaFacade.buscaConselhoProfissionalServidorVO(prescricao.getServidorValida().getId().getMatricula(),  prescricao.getServidorValida().getId().getVinCodigo());

		formataNomeMedico(voPai, vo);

		// paciente recém nacido requer prontuário da mãe no relatório
		formatarPacienteRecemNascido(voPai);

		MpmCidAtendimento cidPrincipal = obterCidPrincipal(prescricao.getAtendimento());
		if (cidPrincipal != null) {
			voPai.setCidPrincipalAtendimento("CID : " + cidPrincipal.getCid().getDescricao());
		} else {
			voPai.setCidPrincipalAtendimento(StringUtils.EMPTY);
		}

		if (prescricao.getAtendimento().getInternacao() != null && prescricao.getAtendimento().getInternacao().getDthrInternacao() != null) {
			voPai.setDtHoraInternacao("Data da Internação: " + 
					DateUtil.dataToString(prescricao.getAtendimento().getInternacao().getDthrInternacao(), "dd/MM/yyyy")); 
		} else {
			voPai.setDtHoraInternacao(StringUtils.EMPTY);
		}
	
		formatarLocalInternacao(voPai);
		formatarUnidadeFuncional(voPai);

		Collection<RelatorioConfirmacaoItensPrescricaoVOPai> colVOPai = new ArrayList<RelatorioConfirmacaoItensPrescricaoVOPai>();
		colVOPai.add(voPai);

		/*-------------VERIFICA SE DEVE IMPRIMIR NOVAS VIAS--------------- */
		Byte nroViasPme = prescricaoMedicaFacade.obterNumeroDeViasRelatorio(prescricao);
		if (nroViasPme > 1) {
			this.prepararImprimirNovasVias(voPai, colVOPai, nroViasPme);
		}
		/*------------------------------------------- */

		return colVOPai;
	}

	private void formatarUnidadeFuncional(
			RelatorioConfirmacaoItensPrescricaoVOPai voPai) {
		if(prescricao.getAtendimento().getUnidadeFuncional() != null){
			voPai.setUnidadeFuncionalInternacao("Unidade: ".concat(prescricao.getAtendimento().getUnidadeFuncional().getAndarAlaDescricao()));
		}
		
	}

	private void formatarPacienteRecemNascido(
			RelatorioConfirmacaoItensPrescricaoVOPai voPai) {
		if (prescricao.getAtendimento().getPaciente().getProntuario() != null
				&& prescricao.getAtendimento().getPaciente().getProntuario() > VALOR_MAXIMO_PRONTUARIO) {

			if (prescricao.getAtendimento().getAtendimentoMae() != null) {
				voPai.setNomeMaePaciente(CoreUtil
						.formataProntuarioRelatorio(prescricao.getAtendimento().getPaciente().getProntuario())
						+ "          Mãe: "
						+ CoreUtil.formataProntuarioRelatorio(prescricao.getAtendimento().getAtendimentoMae().getPaciente().getProntuario()));
			}

		}
	}

	private void formatarItensPrescricaoMedica(
			RelatorioConfirmacaoItensPrescricaoVOPai voPai, String aprazamento)
			throws ApplicationBusinessException {
		for (ItemPrescricaoMedica item : itensDaPrescricaoConfirmacao) {

			String operacao = "";
			Integer ordem = 0;
			Boolean inclusaoExclusao = false;
			EnumStatusItem statusItem = null;
			
			if(!contraCheque) {
				statusItem = prescricaoMedicaFacade.buscarStatusItem(item, dataMovimento);
			}

			/*---------------VERIFICA SE É REIMPRESSÃO ---------------------*/
			impressaoTotal = this.verificarReimpressao(tipoImpressao, servidorValida);
			/*--------------------------------------------------------------*/
			
			if (!impressaoTotal) {
				if (statusItem.equals(EnumStatusItem.ALTERADO)) {
					operacao = ALTERAR;
					ordem = 2;
				} else if (statusItem.equals(EnumStatusItem.INCLUIDO)) {
					operacao = "Incluir";
					ordem = 1;
					inclusaoExclusao = true;
				} else if (statusItem.equals(EnumStatusItem.EXCLUIDO)) {
					operacao = "Excluir";
					ordem = 3;
					inclusaoExclusao = true;
				}
			}

			formatar(item, voPai, aprazamento, statusItem, operacao, ordem, inclusaoExclusao);
		}
	}

	private void formatar(ItemPrescricaoMedica item, RelatorioConfirmacaoItensPrescricaoVOPai voPai, String aprazamento,
						  EnumStatusItem statusItem, String operacao, Integer ordem, Boolean inclusaoExclusao) throws ApplicationBusinessException {
		if(formataMpmPrescricaoDieta(item, voPai, statusItem, operacao, ordem, inclusaoExclusao)){
			return;
		}
		
		if(formataMpmPrescricaoMdto(item, voPai, aprazamento, statusItem, operacao, ordem, inclusaoExclusao)){
			return;
		}
		
		if(formataMpmPrescricaoCuidado(item, voPai, aprazamento, statusItem, operacao, ordem, inclusaoExclusao)){
			return;
		}
		
		if(formataMpmSolicitacaoConsultoria(item, voPai, aprazamento, statusItem, operacao, ordem, inclusaoExclusao)){
			return;
		}
		
		if(formataAbsSolicitacoesHemoterapicas(item, voPai, aprazamento, statusItem, operacao, ordem, inclusaoExclusao)){
			return;
		}
		
		if(formataMpmPrescricaoNpt(item, voPai, operacao, ordem)){
			return;
		}
		
		if(formataMpmPrescricaoProcedimento(item, voPai, statusItem, operacao, ordem, inclusaoExclusao)){
			return;
		}		
	}

	private boolean formataMpmPrescricaoDieta(ItemPrescricaoMedica item, RelatorioConfirmacaoItensPrescricaoVOPai voPai,
											  EnumStatusItem statusItem, String operacao, Integer ordem, Boolean inclusaoExclusao){
		
		if (item instanceof MpmPrescricaoDieta) {
			MpmPrescricaoDieta dieta = (MpmPrescricaoDieta) item;
			
			List<String> listStringAprazamentoDieta = prescricaoMedicaFacade.obterAprazamentoDieta(dieta);
			
			String aprazamento = listStringAprazamentoDieta != null ? gerarAprazamentoString(listStringAprazamentoDieta) : "";
			if (!impressaoTotal && statusItem.equals(EnumStatusItem.ALTERADO)) {
				operacao = ALTERAR;
				// Obtém a descrição de alteração
				String descricaoAlteracao = prescricaoMedicaFacade.obterDescricaoAlteracaoDietaRelatorioItensConfirmados(dieta);				
				voPai.adicionarDietaConfirmada(descricaoAlteracao,operacao, ordem, aprazamento);

			} else {
				// Obtém a descrição total
				String descricaoFormatada = prescricaoMedicaFacade.obterDescricaoFormatadaDietaRelatorioItensConfirmados(dieta, inclusaoExclusao, impressaoTotal);
				voPai.adicionarDietaConfirmada(descricaoFormatada,operacao, ordem, aprazamento);
			}
			return true;
		}
		
		return false;
	}
	
	private boolean formataMpmPrescricaoMdto(ItemPrescricaoMedica item, RelatorioConfirmacaoItensPrescricaoVOPai voPai, String aprazamento,
											 EnumStatusItem statusItem, String operacao, Integer ordem, Boolean inclusaoExclusao) throws ApplicationBusinessException{
		
		if (item instanceof MpmPrescricaoMdto) {
			List<PosolociaDosagemMedicamentoVO> listDescMedicamentos = new ArrayList<PosolociaDosagemMedicamentoVO>();
			MpmPrescricaoMdto mpmPrescricaoMdto = (MpmPrescricaoMdto)item;
			//Busca o objeto para carregar os dados da prescrição que serão utilizados
			MpmPrescricaoMdto medicamentoSolucao = prescricaoMedicaFacade.obterPrescricaoMedicamento(mpmPrescricaoMdto.getId().getAtdSeq(), mpmPrescricaoMdto.getId().getSeq());
			if (!impressaoTotal
					&& statusItem.equals(EnumStatusItem.ALTERADO)) {
				// Obtém a descrição de alteração
				listDescMedicamentos = prescricaoMedicaFacade.obterDescricaoAlteracaoMedicamentoSolucaoRelatorioItensConfirmados(medicamentoSolucao, false);
			}else {
				// Obtém a descrição total
				listDescMedicamentos = prescricaoMedicaFacade
						.obterDescricaoFormatadaMedicamentoSolucaoRelatorioItensConfirmados(
								medicamentoSolucao, inclusaoExclusao, impressaoTotal, false, false);
			}

			if (medicamentoSolucao.getIndSolucao()) {
				aprazamento = gerarAprazamentoString( prescricaoMedicaFacade.gerarAprazamento(
													  prescricao.getId().getAtdSeq(),
													  prescricao.getId().getSeq(), 
													  medicamentoSolucao.getDthrInicio(),
													  medicamentoSolucao.getDthrFim(),
													  medicamentoSolucao.getTipoFreqAprazamento().getSeq(),
													  TipoItemAprazamento.SOLUCAO,
													  medicamentoSolucao.getHoraInicioAdministracao(),
													  medicamentoSolucao.getIndSeNecessario(),
													  medicamentoSolucao.getFrequencia()));
				voPai.adicionarSolucaoConfirmada(listDescMedicamentos, aprazamento, operacao, impressaoTotal ? mpmPrescricaoMdto.getOrdem() : ordem);
				
			} else {

				aprazamento = gerarAprazamentoString( prescricaoMedicaFacade.gerarAprazamento(
													  prescricao.getId().getAtdSeq(),
													  prescricao.getId().getSeq(), medicamentoSolucao.getDthrInicio(),
													  medicamentoSolucao.getDthrFim(),
													  medicamentoSolucao.getTipoFreqAprazamento().getSeq(),
													  TipoItemAprazamento.MEDICAMENTO,
													  medicamentoSolucao.getHoraInicioAdministracao(),
													  medicamentoSolucao.getIndSeNecessario(),
													  medicamentoSolucao.getFrequencia()));
				voPai.adicionarMedicamentoConfirmado(listDescMedicamentos, aprazamento, medicamentoSolucao.getIndAntiMicrobiano(), operacao, impressaoTotal ? mpmPrescricaoMdto.getOrdem() : ordem);
					
			}
			return true;
		}
		
		return false;
	}


	private boolean formataMpmPrescricaoCuidado(ItemPrescricaoMedica item, RelatorioConfirmacaoItensPrescricaoVOPai voPai, String aprazamento,
			  									EnumStatusItem statusItem, String operacao, Integer ordem, Boolean inclusaoExclusao){
		
		if (item instanceof MpmPrescricaoCuidado) {

			MpmPrescricaoCuidado cuidado = (MpmPrescricaoCuidado) item;

			Short frequencia = null;
			if (cuidado.getFrequencia() != null) {
				frequencia = cuidado.getFrequencia().shortValue();
			}
			aprazamento = gerarAprazamentoString(prescricaoMedicaFacade.gerarAprazamento(
												 prescricao.getId().getAtdSeq(),
												 prescricao.getId().getSeq(), 
												 cuidado.getDthrInicio(), cuidado.getDthrFim(),
												 cuidado.getMpmTipoFreqAprazamentos().getSeq(),
												 TipoItemAprazamento.CUIDADO, null, null, frequencia));

			if (!impressaoTotal && statusItem.equals(EnumStatusItem.ALTERADO)) {
				operacao = ALTERAR;
				// Obtém a descrição de alteração
				String descricaoAlteracao = prescricaoMedicaFacade.obterDescricaoAlteracaoCuidadoRelatorioItensConfirmados(cuidado);
				voPai.adicionarCuidadoConfirmado(descricaoAlteracao, aprazamento, operacao, ordem);
				
			} else {
				// Obtém a descrição formatada
				String descricaoformatada = prescricaoMedicaFacade.obterDescricaoFormatadaCuidadoRelatorioItensConfirmados(cuidado.getId().getAtdSeq(), cuidado.getId().getSeq());
				voPai.adicionarCuidadoConfirmado(descricaoformatada,aprazamento, operacao, cuidado.getOrdem());
			}

			return true;
		}
		return false;
	}
	
	private boolean formataMpmSolicitacaoConsultoria(ItemPrescricaoMedica item, RelatorioConfirmacaoItensPrescricaoVOPai voPai, String aprazamento,
													 EnumStatusItem statusItem, String operacao, Integer ordem, Boolean inclusaoExclusao){
		
		if (item instanceof MpmSolicitacaoConsultoria) {
			MpmSolicitacaoConsultoria consultoria = (MpmSolicitacaoConsultoria) item;
			
			if (!impressaoTotal && statusItem.equals(EnumStatusItem.ALTERADO)) {
				operacao = ALTERAR;
				// Obtém a descrição de alteração
				String descricaoAlteracao = prescricaoMedicaFacade.obterDescricaoAlteracaoConsultoriaRelatorioItensConfirmados(consultoria.getId().getAtdSeq(), consultoria.getId().getSeq());
				voPai.adicionarConsultoriaConfirmada(descricaoAlteracao,operacao, ordem);
				
			} else {
				// Obtém a descrição formatada
				String descricaoformatada = prescricaoMedicaFacade.obterDescricaoFormatadaConsultoriaRelatorioItensConfirmados(consultoria.getId().getAtdSeq(), consultoria.getId().getSeq());
				voPai.adicionarConsultoriaConfirmada(descricaoformatada, operacao, consultoria.getOrdem());
			}
			return true;
		}
		return false;
	}
	
	private boolean formataAbsSolicitacoesHemoterapicas(ItemPrescricaoMedica item, RelatorioConfirmacaoItensPrescricaoVOPai voPai, String aprazamento,
			 											EnumStatusItem statusItem, String operacao, Integer ordem, Boolean inclusaoExclusao){
		
		if (item instanceof AbsSolicitacoesHemoterapicas) {

			AbsSolicitacoesHemoterapicas solicitacaoHemoterapica = (AbsSolicitacoesHemoterapicas) item;
			
			if (!impressaoTotal && statusItem.equals(EnumStatusItem.ALTERADO)) {
				String descricaoAlteracao = prescricaoMedicaFacade
						.obterDescricaoAlteracaoSolicitacaoHemoterapicasRelatorioItensConfirmados(solicitacaoHemoterapica.getId().getAtdSeq(), solicitacaoHemoterapica.getId().getSeq(), impressaoTotal);
				
				voPai.adicionarHemoterapiaConfirmada(descricaoAlteracao, operacao, ordem);
			} else {
				String descricaoformatada = prescricaoMedicaFacade
						.obterDescricaoFormatadaSolicitacaoHemoterapicasRelatorioItensConfirmados(solicitacaoHemoterapica.getId().getAtdSeq(), 
																								  solicitacaoHemoterapica.getId().getSeq(), 
																								  impressaoTotal,
																								  inclusaoExclusao);
				voPai.adicionarHemoterapiaConfirmada(descricaoformatada,operacao, solicitacaoHemoterapica.getOrdem());
			}
			return true;
		}
		
		return false;
	}
	
	private boolean formataMpmPrescricaoNpt(ItemPrescricaoMedica item, RelatorioConfirmacaoItensPrescricaoVOPai voPai, String operacao, Integer ordem){
		if (item instanceof MpmPrescricaoNpt) {
			String descricaoFormatada = prescricaoMedicaFacade.descricaoFormatadaNpt((MpmPrescricaoNpt) item);
			descricaoFormatada = StringUtils.capitalize(descricaoFormatada.toLowerCase());
			voPai.adicionarNPTConfirmada(descricaoFormatada, operacao,ordem);

			return true;
		}
		return false;
	}
	
	private boolean formataMpmPrescricaoProcedimento(ItemPrescricaoMedica item, RelatorioConfirmacaoItensPrescricaoVOPai voPai, EnumStatusItem statusItem, String operacao, Integer ordem, Boolean inclusaoExclusao){
		if (item instanceof MpmPrescricaoProcedimento) {
			MpmPrescricaoProcedimento procedimento = (MpmPrescricaoProcedimento) item;
			if (!impressaoTotal && statusItem.equals(EnumStatusItem.ALTERADO)) {
				String descricaoAlteracao = prescricaoMedicaFacade.obterDescricaoAlteracaoProcedimentoRelatorioItensConfirmados(procedimento.getId().getAtdSeq(),procedimento.getId().getSeq());
				voPai.adicionarProcedimentoConfirmado(descricaoAlteracao, operacao, ordem);
				
			} else {
				String descricaoformatada = prescricaoMedicaFacade.obterDescricaoFormatadaProcedimentoRelatorioItensConfirmados( procedimento.getId().getAtdSeq(), procedimento.getId().getSeq(), 
																															     impressaoTotal, inclusaoExclusao);
				voPai.adicionarProcedimentoConfirmado(descricaoformatada, operacao, procedimento.getOrdem());
			}
			return true;
		}
		
		return false;
	}

	private void formataNomeMedico(RelatorioConfirmacaoItensPrescricaoVOPai voPai, BuscaConselhoProfissionalServidorVO vo) {
		StringBuilder medico = new StringBuilder();
		String nomeMedico = vo.getNome();
		String siglaConselho = vo.getSiglaConselho();
		String numeroRegConselho = vo.getNumeroRegistroConselho();

		if (nomeMedico != null) {
			medico.append(nomeMedico).append("   ");
		}
		if (siglaConselho != null) {
			medico.append(siglaConselho).append(": ");
		}
		if (numeroRegConselho != null) {
			medico.append(numeroRegConselho);
		}

		voPai.setMedicoConfirmacao(medico.toString());
	}
	
	private void formatarLocalInternacao(
			RelatorioConfirmacaoItensPrescricaoVOPai voPai) {
		if (prescricao.getAtendimento().getLeito() != null) {
			voPai.setLocalInternacao("Leito: "+ prescricao.getAtendimento().getLeito().getLeitoID());
			
		} else if (prescricao.getAtendimento().getQuarto() != null) {
			voPai.setLocalInternacao("Quarto: "+ prescricao.getAtendimento().getQuarto().getDescricao());
			
		} else if (prescricao.getAtendimento().getUnidadeFuncional() != null) {
			StringBuffer unidade = null;
			if (prescricao.getAtendimento().getUnidadeFuncional().getSigla() != null) {
				unidade = new StringBuffer(prescricao.getAtendimento().getUnidadeFuncional().getSigla());
				
			} else {
				unidade = new StringBuffer(prescricao.getAtendimento().getUnidadeFuncional().getAndar().toString());
				
				if (prescricao.getAtendimento().getUnidadeFuncional().getIndAla() != null) {
					unidade.append(prescricao.getAtendimento().getUnidadeFuncional().getIndAla());
				}
			}
			voPai.setLocalInternacao(unidade.insert(0, "Unidade: ").toString());
		}
	}
	
	@Override
	public String recuperarArquivoRelatorio() {
		String valorParametroRelatorioPaisagem = "N";
		String valorParametroRelatorioInfoPacienteCabecalho = "N";
		try {
			valorParametroRelatorioPaisagem = parametroFacade.buscarValorTexto(AghuParametrosEnum.P_RELATORIO_PRESCRICAO_MEDICA_PAISAGEM);
			valorParametroRelatorioInfoPacienteCabecalho = parametroFacade.buscarValorTexto(AghuParametrosEnum.P_REL_PRESC_MEDICA_INFO_PACIENTE_CABECALHO);
		} catch (ApplicationBusinessException e) {
			LOG.error("Erro ao tentar recuparar parâmetros",e);
		}
		
		//Garantir que caso não venha "S" seja "N"
		if(!valorParametroRelatorioPaisagem.equalsIgnoreCase("S")){
			valorParametroRelatorioPaisagem = "N";
		}
		if(!valorParametroRelatorioInfoPacienteCabecalho.equalsIgnoreCase("S")){
			valorParametroRelatorioInfoPacienteCabecalho = "N";
		}
		/*---------------VERIFICA SE É REIMPRESSÃO ---------------------*/
		impressaoTotal = this.verificarReimpressao(tipoImpressao, servidorValida);
		/*--------------------------------------------------------------*/

		String caminhoConfirmados;
		if(contraCheque) {
			caminhoConfirmados = "br/gov/mec/aghu/prescricaomedica/report/relatorioContraChequePrescricaoMedica.jasper";
		}else if (!impressaoTotal){
			if(valorParametroRelatorioPaisagem.equalsIgnoreCase("S") && valorParametroRelatorioInfoPacienteCabecalho.equalsIgnoreCase("S")){
				caminhoConfirmados = "br/gov/mec/aghu/prescricaomedica/report/DiferencaItensPrescricaoConfirmadosPaisagemInfoPacTop.jasper";
			}else if (valorParametroRelatorioPaisagem.equalsIgnoreCase("S") && valorParametroRelatorioInfoPacienteCabecalho.equalsIgnoreCase("N")) {
				caminhoConfirmados = "br/gov/mec/aghu/prescricaomedica/report/DiferencaItensPrescricaoConfirmadosPaisagemInfoPacBot.jasper";
			}else if (valorParametroRelatorioPaisagem.equalsIgnoreCase("N") && valorParametroRelatorioInfoPacienteCabecalho.equalsIgnoreCase("S")){
				caminhoConfirmados = "br/gov/mec/aghu/prescricaomedica/report/DiferencaItensPrescricaoConfirmadosInfoPacTop.jasper";
			}else {
				caminhoConfirmados = "br/gov/mec/aghu/prescricaomedica/report/DiferencaItensPrescricaoConfirmadosInfoPacBot.jasper";
			}
		}else if (valorParametroRelatorioPaisagem.equalsIgnoreCase("S") && valorParametroRelatorioInfoPacienteCabecalho.equalsIgnoreCase("S")){
			caminhoConfirmados = "br/gov/mec/aghu/prescricaomedica/report/ItensPrescricaoConfirmadosPaisagemInfoPacTop.jasper";
		}else if (valorParametroRelatorioPaisagem.equalsIgnoreCase("S") && valorParametroRelatorioInfoPacienteCabecalho.equalsIgnoreCase("N")) {
			caminhoConfirmados = "br/gov/mec/aghu/prescricaomedica/report/ItensPrescricaoConfirmadosPaisagemInfoPacBot.jasper";
		}else if (valorParametroRelatorioPaisagem.equalsIgnoreCase("N") && valorParametroRelatorioInfoPacienteCabecalho.equalsIgnoreCase("S")){
			caminhoConfirmados = "br/gov/mec/aghu/prescricaomedica/report/ItensPrescricaoConfirmadosInfoPacTop.jasper";
		}else {
			caminhoConfirmados = "br/gov/mec/aghu/prescricaomedica/report/ItensPrescricaoConfirmadosInfoPacBot.jasper";
		}

		return caminhoConfirmados;
	}

	private MpmCidAtendimento obterCidPrincipal(AghAtendimentos atendimento) {
		MpmCidAtendimento cidPrincipalAtendimento = null;
		for (MpmCidAtendimento cidAtendimento : atendimento.getCidAtendimentos()) {
			if (DominioPrioridadeCid.N.equals(cidAtendimento.getPrioridadeInicio())) {
				cidPrincipalAtendimento = cidAtendimento;
				break;
			}
		}
		return cidPrincipalAtendimento;
	}
	
	/**
	 * Método responsável por retornar os parâmetros utilizados no relatório.
	 */
	public Map<String, Object> recuperarParametros() {

		Map<String, Object> params = new HashMap<String, Object>();
		
		String valorParametroRelatorioPaisagem = "N";
		try {
			valorParametroRelatorioPaisagem = parametroFacade.buscarValorTexto(AghuParametrosEnum.P_RELATORIO_PRESCRICAO_MEDICA_PAISAGEM);
		} catch (ApplicationBusinessException e) {
			LOG.error("Erro ao tentar recuparar parâmetros",e);
		} 
		
		//Garantir que caso não venha "S" seja "N"
		if(!valorParametroRelatorioPaisagem.equalsIgnoreCase("S")){
			valorParametroRelatorioPaisagem = "N";
		}

		String caminhoSemAprazamento;
		String caminhoComAprazamento;
		String caminhoMedSol;
		String caminhoMedSolSubReport;
		String titulo;

		/*---------------VERIFICA SE É REIMPRESSÃO ---------------------*/
		impressaoTotal = this.verificarReimpressao(tipoImpressao, servidorValida);
		/*--------------------------------------------------------------*/

		if(contraCheque) {
			titulo = "CONTRA CHEQUE";
		} else if (!impressaoTotal){
			titulo = "MOVIMENTAÇÕES DA PRESCRIÇÃO";
		}else {
			titulo = "PRESCRIÇÃO";
		}

		if (!impressaoTotal && valorParametroRelatorioPaisagem.equalsIgnoreCase("S")) {
			caminhoSemAprazamento = "br/gov/mec/aghu/prescricaomedica/report/DiferencaItensPrescricaoConfirmadosSemAprazamentoPaisagem.jasper";
			caminhoComAprazamento = "br/gov/mec/aghu/prescricaomedica/report/DiferencaItensPrescricaoConfirmadosComAprazamentoPaisagem.jasper";
			caminhoMedSol = "br/gov/mec/aghu/prescricaomedica/report/DiferencaItensPrescricaoConfirmadosMedSolPaisagem.jasper";
			caminhoMedSolSubReport = "br/gov/mec/aghu/prescricaomedica/report/ItensPrescricaoConfirmadosMedSolPaisagemSub.jasper";
		} else if(!impressaoTotal){
			caminhoSemAprazamento = "br/gov/mec/aghu/prescricaomedica/report/DiferencaItensPrescricaoConfirmadosSemAprazamento.jasper";
			caminhoComAprazamento = "br/gov/mec/aghu/prescricaomedica/report/DiferencaItensPrescricaoConfirmadosComAprazamento.jasper";
			caminhoMedSol = "br/gov/mec/aghu/prescricaomedica/report/DiferencaItensPrescricaoConfirmadosMedSol.jasper";
			caminhoMedSolSubReport = "br/gov/mec/aghu/prescricaomedica/report/ItensPrescricaoConfirmadosMedSolSub.jasper";
		}else if(valorParametroRelatorioPaisagem.equalsIgnoreCase("S")){
			caminhoSemAprazamento = "br/gov/mec/aghu/prescricaomedica/report/ItensPrescricaoConfirmadosSemAprazamentoPaisagem.jasper";
			caminhoComAprazamento = "br/gov/mec/aghu/prescricaomedica/report/ItensPrescricaoConfirmadosComAprazamentoPaisagem.jasper";
			caminhoMedSol = "br/gov/mec/aghu/prescricaomedica/report/ItensPrescricaoConfirmadosMedSolPaisagem.jasper";
			caminhoMedSolSubReport = "br/gov/mec/aghu/prescricaomedica/report/ItensPrescricaoConfirmadosMedSolPaisagemSub.jasper";
		}else {
			caminhoSemAprazamento = "br/gov/mec/aghu/prescricaomedica/report/ItensPrescricaoConfirmadosSemAprazamento.jasper";
			caminhoComAprazamento = "br/gov/mec/aghu/prescricaomedica/report/ItensPrescricaoConfirmadosComAprazamento.jasper";
			caminhoMedSol = "br/gov/mec/aghu/prescricaomedica/report/ItensPrescricaoConfirmadosMedSol.jasper";
			caminhoMedSolSubReport = "br/gov/mec/aghu/prescricaomedica/report/ItensPrescricaoConfirmadosMedSolSub.jasper";
		}


		params.put("subRelatorioSemAprazamento",Thread.currentThread().getContextClassLoader().getResourceAsStream(caminhoSemAprazamento));
		params.put("subRelatorioComAprazamento",Thread.currentThread().getContextClassLoader().getResourceAsStream(caminhoComAprazamento));
		params.put("subRelatorioMedSol",Thread.currentThread().getContextClassLoader().getResourceAsStream(caminhoMedSol));
		params.put("subRelatorioMedSolSub",Thread.currentThread().getContextClassLoader().getResourceAsStream(caminhoMedSolSubReport));
		params.put("titulo", titulo);
		params.put("reimpressao", EnumTipoImpressao.REIMPRESSAO.equals(tipoImpressao));
		params.put("rascunho", this.prescricaoMedicaRascunho);
		
		try {
			params.put("imagemLogoHospital", recuperarCaminhoLogo());
		} catch (BaseException e) {
			LOG.error("Erro ao tentar recuparar logotipo para o relatório",e);
		}

		return params;
	}
	

	public StreamedContent getRenderPdf()  throws IOException, JRException, SystemException, DocumentException, ApplicationBusinessException {
		Map<String, Object> parametros = new HashMap<String, Object>();					
		parametros.put(TIPO_IMPRESSAO, tipoImpressao);
		documentoJasper = gerarDocumento(parametros); 
		return ActionReport.criarStreamedContentPdfPorByteArray(documentoJasper.getPdfByteArray(false));
	}
	
	public StreamedContent getRenderPdfMultiPresc() throws IOException, BaseException, JRException, SystemException, DocumentException {
		documentoJasper = gerarDocumento(); 
		return ActionReport.criarStreamedContentPdfPorByteArray(documentoJasper.getPdfByteArray(false));
	}
	
	public StreamedContent getRenderPdfContraCheque() throws IOException, BaseException, JRException, SystemException, DocumentException {
		this.setContraCheque(true);
		documentoJasper = gerarDocumento();
		return ActionReport.criarStreamedContentPdfPorByteArray(documentoJasper.getPdfByteArray(false));
	}
	
	public DocumentoJasper observarEventoImpressaoPrescricaoConfirmada(final EnumTipoImpressao tipoImpressao) throws ApplicationBusinessException {
		Map<String, Object> parametros = new HashMap<String, Object>();					
		parametros.put(TIPO_IMPRESSAO, tipoImpressao);
		return gerarDocumento(parametros);
	}
	
	@Override
	protected void executarPosGeracaoRelatorio(Map<String,Object> parametros) throws ApplicationBusinessException {
		AghDocumentoCertificado documentoCertificado = null;
		
		String nomeRelatorio = (String) parametros.get(PARAMETRO_CAMINHO_RELATORIO);

		EnumTipoImpressao tipoImpressao = (EnumTipoImpressao) parametros.get(TIPO_IMPRESSAO);

		DominioTipoDocumento tipoDocumento = null;
		if (parametros.containsKey(TIPO_DOCUMENTO)){
			tipoDocumento = (DominioTipoDocumento)parametros.get(TIPO_DOCUMENTO);
		}
		
		Boolean certificacaoDigital = false;
		String valorParametroCertificacaoDigital = parametroFacade.buscarValorTexto(AghuParametrosEnum.P_AGHU_CERTIFICACAO_DIGITAL); 
		
		if (valorParametroCertificacaoDigital != null){
			certificacaoDigital =  DominioSimNao.valueOf(valorParametroCertificacaoDigital).isSim();
		}

		if (certificacaoDigital) {
			documentoCertificado = this.verificarRelatorioNecessitaAssinatura(nomeRelatorio, tipoDocumento);

			if (documentoCertificado != null) {
//				entidadePai = this.getEntidadePai();
				if (tipoDocumento != null) {
					documentoCertificado.setTipo(tipoDocumento);
				}
				if (!EnumTipoImpressao.REIMPRESSAO.equals(tipoImpressao)) {
					//#46654
					this.setServidorLogado(servidorLogadoFacade.obterServidorLogado());
					this.setDocumentoCertificado(documentoCertificado);
					this.gerarPendenciaAssinatura();
				}
			} else if (documentoCertificado == null) {
				LOG.warn("O relatório " + nomeRelatorio + " não está registrado para geração de pendencia de assinatura. Pendência de assinatura não será gerada.");
			} 			
		}else{
			LOG.info("Esse hospital não está habilitado para certificação digital, consequentemente a geração de pendências de assinatura não será executada");
		}
	}
	
	private String gerarAprazamentoString(List<String> gerarAprazamento) {
		StringBuilder aprazamento = new StringBuilder("");

		if (gerarAprazamento != null) {
			for (String string : gerarAprazamento) {
				aprazamento.append(string);
				aprazamento.append("          ");
			}
		}
		return aprazamento.toString();
	}

	/**
	 * Prepara novas vias para serem impressas
	 */
	protected void prepararImprimirNovasVias(RelatorioConfirmacaoItensPrescricaoVOPai voPai, Collection<RelatorioConfirmacaoItensPrescricaoVOPai> colVOPai, Byte nroViasPme) {

		voPai.setOrdemTela(1);
		Integer ordemTela = 2;
		for (int i = 0; i < nroViasPme - 1; i++) {
			RelatorioConfirmacaoItensPrescricaoVOPai voPaiNovaVia = voPai.copiar();
			voPaiNovaVia.setOrdemTela(ordemTela);
			colVOPai.add(voPaiNovaVia);
			ordemTela++;
		}
	}
	
	/**
	 * Método que verifica se foi solicitado uma impressão ou reimpressão
	 */
	protected Boolean verificarReimpressao(EnumTipoImpressao tipoImpressao, RapServidores servidorValida) {
		Boolean retorno = false;
		if (tipoImpressao.equals(EnumTipoImpressao.IMPRESSAO) || tipoImpressao.equals(EnumTipoImpressao.SEM_IMPRESSAO)) {
			retorno = prescricaoMedicaFacade.verificarPrimeiraImpressao(servidorValida);
			
		} else if (tipoImpressao.equals(EnumTipoImpressao.REIMPRESSAO) || tipoImpressao.equals(EnumTipoImpressao.VISUALIZAR_RELATORIO)) {
			retorno = true;
		}
		return retorno;
	}
	
	protected BaseEntity getEntidadePai() {
		return prescricao.getAtendimento();
	}
	
	public Boolean getContraCheque() {
		return contraCheque;
	}

	public void setContraCheque(Boolean contraCheque) {
		this.contraCheque = contraCheque;
	}

	public MpmPrescricaoMedica getPrescricao() {
		return prescricao;
	}

	public void setPrescricao(MpmPrescricaoMedica prescricao) {
		this.prescricao = prescricao;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public EnumTipoImpressao getTipoImpressao() {
		return tipoImpressao;
	}

	public void setTipoImpressao(EnumTipoImpressao tipoImpressao) {
		this.tipoImpressao = tipoImpressao;
	}

	public List<ItemPrescricaoMedica> getItensDaPrescricaoConfirmacao() {
		return itensDaPrescricaoConfirmacao;
	}

	public void setItensDaPrescricaoConfirmacao(
			List<ItemPrescricaoMedica> itensDaPrescricaoConfirmacao) {
		this.itensDaPrescricaoConfirmacao = itensDaPrescricaoConfirmacao;
	}

	public Boolean getImpressaoTotal() {
		return impressaoTotal;
	}

	public void setImpressaoTotal(Boolean impressaoTotal) {
		this.impressaoTotal = impressaoTotal;
	}

	public RapServidores getServidorValida() {
		return servidorValida;
	}

	public void setServidorValida(RapServidores servidorValida) {
		this.servidorValida = servidorValida;
	}

	public void setDataMovimento(Date dataMovimento) {
		this.dataMovimento = dataMovimento;
	}
	
	public Boolean getPrescricaoMedicaRascunho() {
		return prescricaoMedicaRascunho;
	}

	public void setPrescricaoMedicaRascunho(Boolean prescricaoMedicaRascunho) {
		this.prescricaoMedicaRascunho = prescricaoMedicaRascunho;
	}	
}
