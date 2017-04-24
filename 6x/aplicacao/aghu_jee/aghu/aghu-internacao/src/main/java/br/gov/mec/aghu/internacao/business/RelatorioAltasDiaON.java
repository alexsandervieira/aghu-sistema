package br.gov.mec.aghu.internacao.business;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.dominio.DominioCor;
import br.gov.mec.aghu.dominio.DominioEstadoCivil;
import br.gov.mec.aghu.dominio.DominioGrauInstrucao;
import br.gov.mec.aghu.dominio.DominioPrioridadeCid;
import br.gov.mec.aghu.dominio.DominioSexo;
import br.gov.mec.aghu.internacao.dao.AinMovimentoInternacaoDAO;
import br.gov.mec.aghu.internacao.pesquisa.business.IPesquisaInternacaoFacade;
import br.gov.mec.aghu.internacao.vo.RelatorioAltasDiaVO;
import br.gov.mec.aghu.internacao.vo.RelatorioAltasObitoVO;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AinCidsInternacao;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.AipCidades;
import br.gov.mec.aghu.model.AipEnderecosPacientes;
import br.gov.mec.aghu.model.AipOcupacoes;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.MbcProcEspPorCirurgias;

/**
 * Classe responsável por prover os métodos de negócio genéricos para o
 * relatório de Altas Dia.
 * 
 */
@Stateless
public class RelatorioAltasDiaON extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(RelatorioAltasDiaON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private AinMovimentoInternacaoDAO ainMovimentoInternacaoDAO;

@EJB
private IPesquisaInternacaoFacade pesquisaInternacaoFacade;

	private static final String SEPARADOR=";";
	private static final String ENCODE="ISO-8859-1";
	private static final String PREFIXO="OBITO_";	
	private static final String EXTENSAO=".csv";
	private static final String SEPARADOR_DE_LINHA="line.separator";
	SimpleDateFormat formatadorDataPadrao = new SimpleDateFormat("dd/MM/yyyy");

	/**
	 * 
	 */
	private static final long serialVersionUID = -6066171000865032864L;

	@SuppressWarnings("PMD.NPathComplexity")
	public List<RelatorioAltasDiaVO> pesquisa(Date dataAlta) {
		
		List<RelatorioAltasDiaVO> lista = new ArrayList<RelatorioAltasDiaVO>();
		
		List<Object[]> result = getAinMovimentoInternacaoDAO().pesquisaAltasDia(dataAlta);
		if (result != null && !result.isEmpty()) {
			
			Iterator<Object[]> it = result.iterator();

			while (it.hasNext()) {
				Object[] obj = it.next();
				RelatorioAltasDiaVO vo = new RelatorioAltasDiaVO();

				if (obj[0] != null) {
					vo.setCodigoConvenio((String) obj[0].toString());
				}

				if (obj[1] != null && obj[2] != null) {
					StringBuilder sb = new StringBuilder();
					Formatter formatter = new Formatter(sb, Locale.US);

					vo.setDescricaoConvenioData(formatter.format("%-50s", obj[1]).toString().concat("Data: ")
							.concat(formatadorDataPadrao.format(obj[2])));
				}

				if (obj[3] != null) {
					String prontAux = ((Integer) obj[3]).toString();
					vo.setProntuario(prontAux.substring(0, prontAux.length() - 1) + "/" + prontAux.charAt(prontAux.length() - 1));
				}

				if (obj[4] != null) {
					vo.setNomePaciente((String) obj[4]);
				}

				if (obj[5] != null) {
					String tipoObito = (String) obj[5];
					if (tipoObito.equals("C") || tipoObito.equals("D")) {
						vo.setObito("O");
					} else {
						vo.setObito("");
					}
				}

				if (obj[6] != null && obj[7] != null) {
					vo.setCrm(getPesquisaInternacaoFacade().buscarNroRegistroConselho((Short) obj[6], (Integer) obj[7]));
					vo.setNomeMedico(getPesquisaInternacaoFacade().buscarNomeUsual((Short) obj[6], (Integer) obj[7]));
				}

				if (obj[8] != null) {
					Date ultData = getPesquisaInternacaoFacade().buscaUltimaAlta((Integer) obj[8]);
					SimpleDateFormat formatador = new SimpleDateFormat("yyyy");

					if (ultData != null) {
						vo.setDataAnt((formatador.format(ultData).compareTo("2001") == 0 ? null : formatadorDataPadrao.format(ultData)));
					}
					vo.setSenha(getPesquisaInternacaoFacade().buscaSenhaInternacao((Integer) obj[8]));
				}

				if (obj[9] != null) {

					vo.setDataInt(formatadorDataPadrao.format((Date) obj[9]));
				}

				if (obj[10] != null) {
					vo.setSiglaEspecialidade((String) obj[10]);
				}

				if (obj[11] != null) { // Leito
					vo.setLeito((String) obj[11]);
				} else {
					if (obj[12] != null) { // Numero do quarto
						vo.setLeito((String) obj[12].toString());
					} else { // Andar
						if (obj[13] != null) {
							String strT = (String) obj[13].toString();

							vo.setLeito((String) ("0".concat(strT)).substring(strT.length() == 1 ? 0 : 1));
						}
					}
				}

				lista.add(vo);
			}
		}
		return lista;
	
	}

	protected AinMovimentoInternacaoDAO getAinMovimentoInternacaoDAO(){
		return ainMovimentoInternacaoDAO;
	}
	
	protected IPesquisaInternacaoFacade getPesquisaInternacaoFacade(){
		return pesquisaInternacaoFacade;
	}

	public List<RelatorioAltasObitoVO> pesquisaAltasObito(Date dataInicial,
			Date dataFinal) {
		
		List<Object[]> result = getAinMovimentoInternacaoDAO().pesquisaAltasObito(dataInicial, dataFinal);
		
		List<RelatorioAltasObitoVO> lista = popularDadosRelatorioAltasObito(result);
		
		return lista;
	}

	private List<RelatorioAltasObitoVO> popularDadosRelatorioAltasObito(List<Object[]> result) {
		List<RelatorioAltasObitoVO> lista = null;
		if (result != null && !result.isEmpty()) {
			
			lista = new ArrayList<RelatorioAltasObitoVO>();
			
			Iterator<Object[]> it = result.iterator();

			while (it.hasNext()) {
				Object[] obj = it.next();
				RelatorioAltasObitoVO vo = new RelatorioAltasObitoVO();

				adicionarConvenio(obj, vo);
				
				if(obj[2] != null){
					vo.setDataAlta(formatadorDataPadrao.format(obj[2]));
				}


				if (obj[10] != null) {

					vo.setDataInt(formatadorDataPadrao.format((Date) obj[10]));
				}

				if (obj[11] != null) {
					vo.setSiglaEspecialidade((String) obj[11]);
				}

				adicionarLeito(obj, vo);

				adicionarDadosPaciente(obj, vo);

				adicionarDadosInternacao(obj, vo);
				
				adicionarInformacoesObito(obj, vo);

				lista.add(vo);
			}
		}
		return lista;
	}

	private void adicionarConvenio(Object[] obj, RelatorioAltasObitoVO vo) {
		if (obj[0] != null) {
			vo.setCodigoConvenio((String) obj[0].toString());
		}

		if (obj[1] != null && obj[2] != null) {
			StringBuilder sb = new StringBuilder();
			Formatter formatter = new Formatter(sb, Locale.US);

			vo.setDescricaoConvenioData(formatter.format("%-50s", obj[1]).toString().concat("Data: ")
					.concat(formatadorDataPadrao.format(obj[2])));
		}
	}

	private void adicionarInformacoesObito(Object[] obj,
			RelatorioAltasObitoVO vo) {

		if (obj[6] != null) {
			String tipoObito = (String) obj[6];
			if (tipoObito.equals("C") || tipoObito.equals("D")) {
				vo.setObito("O");
			} else {
				vo.setObito("");
			}
		}

		if(obj[27] != null){
			vo.setClinicaInternacao(obj[27].toString());
		}

		if(obj[28] != null){
			vo.setNomeEspecialidade(obj[28].toString());
		}

		if(obj[29] != null){
			vo.setTipoAltaMedica(obj[29].toString());
		}

		if(obj[30] != null){
			vo.setClinicaObito(obj[30].toString());
		}

		if(obj[31] != null){
			vo.setRegistroHospital(obj[31].toString());
		}
	}

	private void adicionarLeito(Object[] obj, RelatorioAltasObitoVO vo) {
		if (obj[12] != null) { // Leito
			vo.setLeito((String) obj[12]);
		} else {
			if (obj[13] != null) { // Numero do quarto
				vo.setLeito((String) obj[13].toString());
			} else { // Andar
				if (obj[14] != null) {
					String strT = (String) obj[14].toString();

					vo.setLeito((String) ("0".concat(strT)).substring(strT.length() == 1 ? 0 : 1));
				}
			}
		}
	}

	private void adicionarDadosInternacao(Object[] obj, RelatorioAltasObitoVO vo) {
		if(obj[26] != null){
			AinInternacao internacao = (AinInternacao)obj[26];
			adicionarCid(vo, internacao);
			adicionarProcedimentoCirurgico(vo, internacao);
		}
	}

	private void adicionarProcedimentoCirurgico(RelatorioAltasObitoVO vo,
			AinInternacao internacao) {
		if(internacao.getAtendimento() != null){
			AghAtendimentos atendimento = internacao.getAtendimento();
			StringBuilder procedimento = new StringBuilder();
			for (MbcCirurgias cirurgia : atendimento.getCirurgias()) {
				for(MbcProcEspPorCirurgias proc: cirurgia.getProcEspPorCirurgias()){
					if (proc.getIndPrincipal() && proc.getProcedimentoCirurgico() != null){
						procedimento.append(proc.getProcedimentoCirurgico().getDescricao())
						.append(" - ");
						break;
					}
				}
			}
			if(StringUtils.isNotBlank(procedimento)){
				vo.setProcedimentoCirurgico(procedimento.substring(0,procedimento.length() -2));
			}
		}
	}

	private void adicionarCid(RelatorioAltasObitoVO vo, AinInternacao internacao) {
		for(AinCidsInternacao cid : internacao.getCidsInternacao()){
			if(DominioPrioridadeCid.P.equals(cid.getPrioridadeCid()) && cid.getCid() != null){
				vo.setCid(cid.getCid().getCodigoDescricaoCID());
				break;
			}
		}
	}

	private void adicionarDadosPaciente(Object[] obj, RelatorioAltasObitoVO vo) {

		adicionarDadosPrincipaisPaciente(obj, vo);

		if(obj[5] != null){
			Integer idade = null;
			Calendar dataNascimento = new GregorianCalendar();
			dataNascimento.setTime((Date)obj[5]);
			Calendar dataCalendario = new GregorianCalendar();
			dataCalendario.setTime(Calendar.getInstance().getTime());
			// Obtêm a idade baseado no ano
			idade = dataCalendario.get(Calendar.YEAR) - dataNascimento.get(Calendar.YEAR);
			dataNascimento.add(Calendar.YEAR, idade);
			// se a data de hoje é antes da data de Nascimento, então diminui 1(um)
			if (dataCalendario.before(dataNascimento)) {
				idade--;
			}
			vo.setIdade(idade);
		}

		if(obj[17] != null){
			vo.setSexo((DominioSexo.valueOf(obj[17].toString()).getDescricao()));
		}

		if(obj[18] != null){
			vo.setCor((DominioCor.valueOf(obj[18].toString()).getDescricao()));
		}

		if(obj[19] != null){
			vo.setGrauInstrucao((DominioGrauInstrucao.valueOf(obj[19].toString()).getDescricao()));
		}

		if(obj[20] != null){
			vo.setEstadoCivil((DominioEstadoCivil.valueOf(obj[20].toString()).getDescricao()));
		}

		adicionarEndereco(obj, vo);
		
		adicionarTelefone(obj, vo);
	}

	private void adicionarDadosPrincipaisPaciente(Object[] obj,
			RelatorioAltasObitoVO vo) {
		if (obj[3] != null) {
			String prontAux = ((Integer) obj[3]).toString();
			vo.setProntuario(prontAux.substring(0, prontAux.length() - 1) + "/" + prontAux.charAt(prontAux.length() - 1));
		}

		if (obj[4] != null) {
			vo.setNomePaciente((String) obj[4]);
		}
		
		if (obj[5] != null){
			vo.setDataNascimento(formatadorDataPadrao.format(obj[5]));
		}

		if(obj[15] != null){
			vo.setNomeMae(obj[15].toString());
		}

		if(obj[16] != null){
			vo.setCartaoSaude(obj[16].toString());
		}
	}

	private void adicionarEndereco(Object[] obj, RelatorioAltasObitoVO vo) {
		if(obj[21] != null){
			AipPacientes paciente = (AipPacientes)obj[21];
			AipEnderecosPacientes endereco = paciente.getEnderecoPadrao();
			AipOcupacoes ocupacao = paciente.getAipOcupacoes();
			if(ocupacao != null){
				vo.setOcupacao(ocupacao.getCodigo().toString().concat(" - ").concat(ocupacao.getDescricao()));
			}
			if (endereco!=null){
				AipCidades cidade = null;
				if (endereco.getAipBairrosCepLogradouro() != null){
					vo.setRua(endereco.getAipBairrosCepLogradouro().getCepLogradouro().getLogradouro().getNome());
					vo.setNumero(endereco.getNroLogradouro());
					vo.setComplemento(endereco.getComplLogradouro());
					vo.setBairro(endereco.getAipBairrosCepLogradouro().getAipBairro().getDescricao());
					cidade = endereco.getAipBairrosCepLogradouro().getCepLogradouro().getLogradouro().getAipCidade();
					vo.setMunicipio(cidade!=null?cidade.getNome():null);
					vo.setCodigoIBGE(cidade!=null?cidade.getCodIbge():null);
					vo.setCep(endereco.getAipBairrosCepLogradouro().getId().getCloCep());	
					vo.setUf(cidade!=null && cidade.getAipUf()!=null?cidade.getAipUf().getSigla():null);	
				}
				else{
					vo.setRua(endereco.getLogradouro());
					vo.setNumero(endereco.getNroLogradouro());
					vo.setComplemento(endereco.getComplLogradouro());
					vo.setBairro(endereco.getBairro());
					cidade = endereco.getAipCidade();
					vo.setMunicipio(cidade!=null?cidade.getNome():null);
					vo.setCodigoIBGE(cidade!=null?cidade.getCodIbge():null);
					vo.setCep(endereco.getCep());
					vo.setUf(cidade!=null && cidade.getAipUf()!=null?cidade.getAipUf().getSigla():null);	
				}	
			}
			
		}
	}

	private void adicionarTelefone(Object[] obj, RelatorioAltasObitoVO vo) {
		if(obj[22] != null){
			if (obj[23] != null){
				vo.setTelefone(obj[23].toString().concat("-").concat(obj[22].toString()));
			}else{
				vo.setTelefone(obj[22].toString());				
			}
		}else if(obj[24] != null){
			if (obj[25] != null){
				vo.setTelefone(obj[25].toString().concat("-").concat(obj[24].toString()));
			}else{
				vo.setTelefone(obj[25].toString());				
			}
		}
	}

	public File gerarArquivoAltasObito(Date dataInicial, Date dataFinal) throws IOException, ApplicationBusinessException {

		List<RelatorioAltasObitoVO> listaAltasObito = pesquisaAltasObito(dataInicial, dataFinal);
		if(listaAltasObito == null || listaAltasObito.isEmpty()){
			return null;
		}
		File file = File.createTempFile(PREFIXO, EXTENSAO);
		
		Writer out = new OutputStreamWriter(new FileOutputStream(file), ENCODE);
		out.write(geraCabecalho());
		if(listaAltasObito != null){
			for (RelatorioAltasObitoVO obito :listaAltasObito){
					
					out.write(System.getProperty(SEPARADOR_DE_LINHA));
					out.write(geraLinhaAltaObito(obito, out));
			}
		}	
		out.flush();
		out.close();
		
		return file;
	}
	
	public String geraCabecalho() throws ApplicationBusinessException{
		StringBuffer sb = new StringBuffer(900);
		
		sb.append("Cartão SUS").append(SEPARADOR)
		.append("Registro Hospital").append(SEPARADOR)
		.append("Nome").append(SEPARADOR)
		.append("Nome Mãe").append(SEPARADOR)
		.append("Data Nascimento").append(SEPARADOR)
		.append("Idade").append(SEPARADOR)
		.append("Gênero").append(SEPARADOR)
		.append("Cor").append(SEPARADOR)
		.append("Escolaridade").append(SEPARADOR)
		.append("Ocupação").append(SEPARADOR)
		.append("Estado Civil").append(SEPARADOR)
		.append("Rua").append(SEPARADOR)
		.append("Número").append(SEPARADOR)
		.append("Complemento").append(SEPARADOR)
		.append("Bairro").append(SEPARADOR)
		.append("Município").append(SEPARADOR)
		.append("Código Município IBGE").append(SEPARADOR)
		.append("CEP").append(SEPARADOR)
		.append("UF").append(SEPARADOR)
		.append("Telefone").append(SEPARADOR)
		.append("Data Internação").append(SEPARADOR)
		.append("CID 10").append(SEPARADOR)
		.append("Clínica Internação").append(SEPARADOR)
		.append("Especialidade").append(SEPARADOR)
		.append("Procedimento Cirúrgico").append(SEPARADOR)
		.append("Data Alta").append(SEPARADOR)
		.append("Tipo Alta").append(SEPARADOR)
		.append("Clínica Óbito").append(SEPARADOR);
		
		return sb.toString();
	}

	public String geraLinhaAltaObito(RelatorioAltasObitoVO obito, Writer out) throws IOException{
		StringBuilder texto = new StringBuilder();
		addText(obito.getCartaoSaude(), texto);
		addText(obito.getRegistroHospital(), texto);
		addText(obito.getNomePaciente(), texto);
		addText(obito.getNomeMae(), texto);
		addText(obito.getDataNascimento(), texto);
		addText(obito.getIdade(), texto);
		addText(obito.getSexo(), texto);
		addText(obito.getCor(), texto);
		addText(obito.getGrauInstrucao(), texto);
		addText(obito.getOcupacao(), texto);
		addText(obito.getEstadoCivil(), texto);
		addText(obito.getRua(), texto);
		addText(obito.getNumero(), texto);
		addText(obito.getComplemento(), texto);
		addText(obito.getBairro(), texto);
		addText(obito.getMunicipio(), texto);
		addText(obito.getCodigoIBGE(), texto);
		addText(obito.getCep(), texto);
		addText(obito.getUf(), texto);
		addText(obito.getTelefone(), texto);
		addText(obito.getDataInt(), texto);
		addText(obito.getCid(), texto);
		addText(obito.getClinicaInternacao(), texto);
		addText(obito.getNomeEspecialidade(), texto);
		addText(obito.getProcedimentoCirurgico(), texto);
		addText(obito.getDataAlta(), texto);
		addText(obito.getTipoAltaMedica(), texto);
		addText(obito.getClinicaObito(), texto);
		
		return texto.toString();
	}
	
	private void addText(Object texto, StringBuilder sb){
		if (texto != null) {
			sb.append(texto);
		}
		sb.append(SEPARADOR);
	}

	public List<RelatorioAltasObitoVO> pesquisaAltasObitoPaginado(
			Integer firstResult, Integer maxResult, String orderProperty, boolean asc, Date dataInicial, Date dataFinal) {
		List<Object[]> result = getAinMovimentoInternacaoDAO().pesquisaAltasObitoPaginator(firstResult, maxResult, orderProperty, asc, dataInicial, dataFinal);
		
		List<RelatorioAltasObitoVO> lista = popularDadosRelatorioAltasObito(result);
		
		return lista;
	}

	public Long pesquisaAltasObitoCount(Date dataInicial, Date dataFinal) {
		
		return getAinMovimentoInternacaoDAO().pesquisaAltasObitoCount(dataInicial, dataFinal);
	}

}
