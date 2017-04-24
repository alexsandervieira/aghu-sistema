package br.gov.mec.aghu.internacao.vo;




/**
 * Classe responsável por agrupar informações a serem exibidos no relatório
 * de altas do dia.
 * 
 * 
 */
public class RelatorioAltasObitoVO {
	
	//Paciente
	private String prontuario;
	private String obito;
	private String nomePaciente;
	private String leito;
	private String siglaEspecialidade;
	private String crm;
	private String nomeMedico;
	private String dataInt;
	private String dataAnt;
	private String dataAlta;
	private String dataNascimento;
	private String senha;
	private String codigoConvenio;
	private String descricaoConvenioData;
	private String nomeMae;
	private Integer idade;
	private String sexo;
	private String cor;
	private String grauInstrucao;
	private String ocupacao;
	private String estadoCivil;
	private String rua;
	private Integer numero;
	private String complemento;
	private String bairro;
	private String municipio;
	private Integer codigoIBGE;
	private Integer cep;
	private String telefone;
	private String cid;
	private String clinicaInternacao;
	private String nomeEspecialidade;
	private String tipoAltaMedica;
	private String clinicaObito;
	private String uf;
	private String cartaoSaude;
	private String registroHospital;
	private String procedimentoCirurgico;
	
	// GETs e SETs
	public String getProntuario() {
		return prontuario;
	}
	public void setProntuario(String prontuario) {
		this.prontuario = prontuario;
	}
	public String getObito() {
		return obito;
	}
	public void setObito(String obito) {
		this.obito = obito;
	}
	public String getNomePaciente() {
		return nomePaciente;
	}
	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}
	public String getLeito() {
		return leito;
	}
	public void setLeito(String leito) {
		this.leito = leito;
	}
	public String getSiglaEspecialidade() {
		return siglaEspecialidade;
	}
	public void setSiglaEspecialidade(String siglaEspecialidade) {
		this.siglaEspecialidade = siglaEspecialidade;
	}
	public String getCrm() {
		return crm;
	}
	public void setCrm(String crm) {
		this.crm = crm;
	}
	public String getNomeMedico() {
		return nomeMedico;
	}
	public void setNomeMedico(String nomeMedico) {
		this.nomeMedico = nomeMedico;
	}
	public String getDataInt() {
		return dataInt;
	}
	public void setDataInt(String dataInt) {
		this.dataInt = dataInt;
	}
	public String getDataAnt() {
		return dataAnt;
	}
	public void setDataAnt(String dataAnt) {
		this.dataAnt = dataAnt;
	}
	public String getSenha() {
		return senha;
	}
	public void setSenha(String senha) {
		this.senha = senha;
	}
	public String getCodigoConvenio() {
		return codigoConvenio;
	}
	public void setCodigoConvenio(String codigoConvenio) {
		this.codigoConvenio = codigoConvenio;
	}
	public String getDescricaoConvenioData() {
		return descricaoConvenioData;
	}
	public void setDescricaoConvenioData(String descricaoConvenioData) {
		this.descricaoConvenioData = descricaoConvenioData;
	}
	public String getDataAlta() {
		return dataAlta;
	}
	public void setDataAlta(String dataAlta) {
		this.dataAlta = dataAlta;
	}
	public String getDataNascimento() {
		return dataNascimento;
	}
	public void setDataNascimento(String dataNascimento) {
		this.dataNascimento = dataNascimento;
	}
	public String getNomeMae() {
		return nomeMae;
	}
	public void setNomeMae(String nomeMae) {
		this.nomeMae = nomeMae;
	}
	public Integer getIdade() {
		return idade;
	}
	public void setIdade(Integer idade) {
		this.idade = idade;
	}
	public String getSexo() {
		return sexo;
	}
	public void setSexo(String sexo) {
		this.sexo = sexo;
	}
	public String getCor() {
		return cor;
	}
	public void setCor(String cor) {
		this.cor = cor;
	}
	public String getGrauInstrucao() {
		return grauInstrucao;
	}
	public void setGrauInstrucao(String grauInstrucao) {
		this.grauInstrucao = grauInstrucao;
	}
	public String getOcupacao() {
		return ocupacao;
	}
	public void setOcupacao(String ocupacao) {
		this.ocupacao = ocupacao;
	}
	public String getEstadoCivil() {
		return estadoCivil;
	}
	public void setEstadoCivil(String estadoCivil) {
		this.estadoCivil = estadoCivil;
	}
	public String getRua() {
		return rua;
	}
	public void setRua(String rua) {
		this.rua = rua;
	}
	public Integer getNumero() {
		return numero;
	}
	public void setNumero(Integer numero) {
		this.numero = numero;
	}
	public String getComplemento() {
		return complemento;
	}
	public void setComplemento(String complemento) {
		this.complemento = complemento;
	}
	public String getBairro() {
		return bairro;
	}
	public void setBairro(String bairro) {
		this.bairro = bairro;
	}
	public String getMunicipio() {
		return municipio;
	}
	public void setMunicipio(String municipio) {
		this.municipio = municipio;
	}
	public Integer getCodigoIBGE() {
		return codigoIBGE;
	}
	public void setCodigoIBGE(Integer codigoIBGE) {
		this.codigoIBGE = codigoIBGE;
	}
	public Integer getCep() {
		return cep;
	}
	public void setCep(Integer cep) {
		this.cep = cep;
	}
	public String getTelefone() {
		return telefone;
	}
	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}
	public String getCid() {
		return cid;
	}
	public void setCid(String cid) {
		this.cid = cid;
	}
	public String getClinicaInternacao() {
		return clinicaInternacao;
	}
	public void setClinicaInternacao(String clinicaInternacao) {
		this.clinicaInternacao = clinicaInternacao;
	}
	public String getNomeEspecialidade() {
		return nomeEspecialidade;
	}
	public void setNomeEspecialidade(String nomeEspecialidade) {
		this.nomeEspecialidade = nomeEspecialidade;
	}
	public String getTipoAltaMedica() {
		return tipoAltaMedica;
	}
	public void setTipoAltaMedica(String tipoAltaMedica) {
		this.tipoAltaMedica = tipoAltaMedica;
	}
	public String getClinicaObito() {
		return clinicaObito;
	}
	public void setClinicaObito(String clinicaObito) {
		this.clinicaObito = clinicaObito;
	}
	public String getUf() {
		return uf;
	}
	public void setUf(String uf) {
		this.uf = uf;
	}
	public String getCartaoSaude() {
		return cartaoSaude;
	}
	public void setCartaoSaude(String cartaoSaude) {
		this.cartaoSaude = cartaoSaude;
	}
	public String getRegistroHospital() {
		return registroHospital;
	}
	public void setRegistroHospital(String registroHospital) {
		this.registroHospital = registroHospital;
	}
	public String getProcedimentoCirurgico() {
		return procedimentoCirurgico;
	}
	public void setProcedimentoCirurgico(String procedimentoCirurgico) {
		this.procedimentoCirurgico = procedimentoCirurgico;
	}
	
}
