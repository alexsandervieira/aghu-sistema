package br.gov.mec.aghu.ambulatorio.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.model.AacPermissaoAgendamentoConsultas;

/**
 * Classe responsável por controlar as ações da listagem de Permissões/Restrição
 * no Agendamento/Marcação de Consultas.
 * 
 * @author gzapalaglio
 */


public class ManterPermissoesAgendamentoConsultaPaginatorController extends ActionController {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	 limparPesquisa();
	}

	private static final long serialVersionUID = 3456278292631896035L;
	
	private static final String PAGE_MANTER_PERMISSOES_AGENDAMENTO_CONSULTA_CRUD = "manterPermissoesAgendamentoConsultaCRUD";

	@EJB
	private IAmbulatorioFacade ambulatorioFacade;

	private AacPermissaoAgendamentoConsultas parametroSelecionado;
	
	List<AacPermissaoAgendamentoConsultas> listaPermissoes;
	/**
	 * Atributos dos campo de filtro da pesquisa da condição de atendimento.
	 */
	private Short vinculoServidor;
	private Integer matriculaServidor;
	private String nomeServidor;
	private boolean pesquisaAtiva;

	public void pesquisar() {
		listaPermissoes = listarPermissoes();
		this.pesquisaAtiva = true;
	}

	public void limparPesquisa() {
		this.vinculoServidor = null;
		this.matriculaServidor = null;
		this.nomeServidor = null;
		this.listaPermissoes = null;
		this.pesquisaAtiva = false;
	}

	private List<AacPermissaoAgendamentoConsultas> listarPermissoes() {
		
		List<AacPermissaoAgendamentoConsultas> result = this.ambulatorioFacade.pesquisarPermissoesAgendamentoConsulta(vinculoServidor, matriculaServidor,	nomeServidor);
		
		List<AacPermissaoAgendamentoConsultas> listaFinal = new ArrayList<AacPermissaoAgendamentoConsultas>();
		AacPermissaoAgendamentoConsultas aux = null;
		int count = 0;
		
		for(AacPermissaoAgendamentoConsultas perm: result){
			if(aux != null){
				if (perm.getServidor().equals(aux.getServidor())){
					count++;
					continue;
				} else {
					aux.setQtdPermissoes(count);
					listaFinal.add(aux);
					count = 0;
				}
			}
			aux = perm;
			count++;
		}
		//Adicionando o Ultimo registro
		if (result != null && !result.isEmpty()){
			aux.setQtdPermissoes(count);
			listaFinal.add(aux);
		} else {
			listaFinal = null;
		}
		
		return listaFinal;
	}
	
	public String inserirEditar() {
		return PAGE_MANTER_PERMISSOES_AGENDAMENTO_CONSULTA_CRUD;
	}
	
	/************************* getters and setters *************************************/

	public AacPermissaoAgendamentoConsultas getParametroSelecionado() {
		return parametroSelecionado;
	}

	public List<AacPermissaoAgendamentoConsultas> getListaPermissoes() {
		return listaPermissoes;
	}

	public void setListaPermissoes(
			List<AacPermissaoAgendamentoConsultas> listaPermissoes) {
		this.listaPermissoes = listaPermissoes;
	}

	public void setParametroSelecionado(AacPermissaoAgendamentoConsultas parametroSelecionado) {
		this.parametroSelecionado = parametroSelecionado;
	}

	public Short getVinculoServidor() {
		return vinculoServidor;
	}

	public void setVinculoServidor(Short vinculoServidor) {
		this.vinculoServidor = vinculoServidor;
	}

	public Integer getMatriculaServidor() {
		return matriculaServidor;
	}

	public void setMatriculaServidor(Integer matriculaServidor) {
		this.matriculaServidor = matriculaServidor;
	}

	public String getNomeServidor() {
		return nomeServidor;
	}

	public void setNomeServidor(String nomeServidor) {
		this.nomeServidor = nomeServidor;
	}

	public boolean isPesquisaAtiva() {
		return pesquisaAtiva;
	}

	public void setPesquisaAtiva(boolean pesquisaAtiva) {
		this.pesquisaAtiva = pesquisaAtiva;
	}

}
