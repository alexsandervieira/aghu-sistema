package br.gov.mec.controller;

public class PreLoggedOutEvent {
	
	private String username;
	private String mensagem;
	
	public PreLoggedOutEvent(String username, String mensagem){
		this.username = username;
		this.mensagem = mensagem;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getMensagem() {
		return mensagem;
	}

	public void setMensagem(String mensagem) {
		this.mensagem = mensagem;
	}

}
