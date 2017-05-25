package br.gov.mec.aghu.core.mail;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.mail.ByteArrayDataSource;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.HtmlEmail;
import org.apache.commons.mail.MultiPartEmail;
import org.apache.commons.mail.SimpleEmail;

import br.gov.mec.aghu.core.commons.Parametro;

/**
 * @author dlaks
 * 
 *      gmneto -   A versão original desta classe, que serviu de base para esta, usa o
 *         seam mail para criar os emails. É preciso avaliar uma solução
 *         alternativa.
 * 
 */
public class EmailUtil implements Serializable {

	
	private static final long serialVersionUID = -520826407924558421L;

	private static final Log LOG = LogFactory.getLog(EmailUtil.class);
	
	public static String ofuscarEmail(String email) {
		if (email == null || "".equalsIgnoreCase(email.trim())) {
			return null;
		}
		
		if (!isEmailValid(email)) {
			return null;
		}
		
		String user = email.substring(0, email.indexOf('@'));
		String twocharIni = user.substring(0,2);
		String twocharEnd = "";
		int tamanhoFinal = user.length();
		if (user.length() > 8) {
			twocharEnd = user.substring(user.length()-2, user.length());
			tamanhoFinal = user.length()-2;
		}
		
		StringBuilder ofuscadaFinal = new StringBuilder("");
		for (int i = 2; i < tamanhoFinal; i++) {
			ofuscadaFinal.append('*');
		}
		
		String ofuscada = email.replace(user, (twocharIni + ofuscadaFinal.toString() + twocharEnd)); 
		return ofuscada;
	}

	public static boolean isEmailValid(String email) {
        if ((email == null) || ("".equalsIgnoreCase(email.trim()))) {
        	return false;
        }

        String emailPattern = "\\b(^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@([A-Za-z0-9-])+(\\.[A-Za-z0-9-]+)*((\\.[A-Za-z0-9]{2,})|(\\.[A-Za-z0-9]{2,}\\.[A-Za-z0-9]{2,}))$)\\b";
        Pattern pattern = Pattern.compile(emailPattern, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
	
	
	private EmailInformation emailInformation;
	
	@Inject @Parametro("mail_session_host")
	private String hostName;

	/**
	 * @deprecated usar metodo enviarEmail
	 * @see EmailUtil#enviarEmail(String, String, String, String, String, AnexoEmail...)
	 * 
	 * @param remetente
	 * @param destinatario
	 * @param destinatarioOculto
	 * @param assunto
	 * @param conteudo
	 * @param anexos
	 */
	public void enviaEmail(String remetente, String destinatario,
			String destinatarioOculto, String assunto, String conteudo,
			AnexoEmail... anexos) {
		//TODO reavaliar uso deste metodo
	}
	
	/**
	 * Metodo responsavel por envio de e-mail.
	 * 
	 * @param remetente
	 * @param destinatario
	 * @param destinatarioOculto
	 * @param assunto
	 * @param conteudo
	 * @param anexos
	 */
	public void enviarEmail(String remetente, String destinatario,
			String destinatarioOculto, String assunto, String conteudo,
			AnexoEmail... anexos) {
		List<ContatoEmail> destinatarios = new ArrayList<ContatoEmail>(1);
		if (destinatario != null) {
			destinatarios.add(new ContatoEmail(destinatario));
		}

		List<ContatoEmail> destinatariosOcultos = new ArrayList<ContatoEmail>(1);
		if (destinatarioOculto != null) {
			destinatariosOcultos.add(new ContatoEmail(destinatarioOculto));
		}

		this.doEnviaEmail(new ContatoEmail(remetente), destinatarios,
				destinatariosOcultos, assunto, conteudo, anexos);
	}

	
	private void enviaEmail(String remetente, List<String> destinatarios,
			List<String> destinatariosOcultos, String assunto, String conteudo,
			Boolean isHtml, AnexoEmail... anexos) {
		ContatoEmail auxRemetente = new ContatoEmail(remetente);
		List<ContatoEmail> auxDestinatarios = new ArrayList<ContatoEmail>();
		if (destinatarios != null) {
			for (String auxDdestinatario : destinatarios) {
				auxDestinatarios.add(new ContatoEmail(auxDdestinatario));
			}
		}

		List<ContatoEmail> auxDestinatariosOcultos = new ArrayList<ContatoEmail>();
		if (destinatariosOcultos != null) {
			for (String auxDestinatarioOculto : destinatariosOcultos) {
				auxDestinatariosOcultos.add(new ContatoEmail(
						auxDestinatarioOculto));
			}
		}

		if (isHtml){
			this.enviaEmailHtml(auxRemetente, auxDestinatarios,
					auxDestinatariosOcultos, assunto, conteudo, anexos);
		}
		else {
		    this.doEnviaEmail(auxRemetente, auxDestinatarios,
				auxDestinatariosOcultos, assunto, conteudo, anexos);
		}
	}
	
	/**
	 * 
	 * @param remetente
	 * @param destinatarios
	 * @param destinatariosOcultos
	 * @param assunto
	 * @param conteudo
	 * @param anexos
	 */
	public void enviarEmail(String remetente, List<String> destinatarios,
			List<String> destinatariosOcultos, String assunto, String conteudo,
			AnexoEmail... anexos) {
		this.enviaEmail(remetente, destinatarios, destinatariosOcultos, assunto, conteudo, false, anexos);
	}
	
	/**
	 * @deprecated usar enviarEmail
	 * @see EmailUtil#enviarEmail(String, List, List, String, String, AnexoEmail...)
	 * 
	 * @param remetente
	 * @param destinatarios
	 * @param destinatariosOcultos
	 * @param assunto
	 * @param conteudo
	 * @param anexos
	 */
	public void enviaEmail(String remetente, List<String> destinatarios,
			List<String> destinatariosOcultos, String assunto, String conteudo,
			AnexoEmail... anexos) {
		//TODO reavaliar uso deste metodo
	}

	/**
	 * Enviar email HTML.
	 * 
	 * @param remetente
	 * @param destinatarios
	 * @param destinatariosOcultos
	 * @param assunto
	 * @param conteudo
	 * @param anexos
	 */
	public void enviarEmailHtml(String remetente, List<String> destinatarios,
			List<String> destinatariosOcultos, String assunto, String conteudo,
			AnexoEmail... anexos) {
		this.enviaEmail(remetente, destinatarios, destinatariosOcultos, assunto, conteudo, true, anexos);
	}
	
	/**
	 * @deprecated usar enviarEmailHtml
	 * @see EmailUtil#enviarEmailHtml(String, List, List, String, String, AnexoEmail...)
	 * 
	 * @param remetente
	 * @param destinatarios
	 * @param destinatariosOcultos
	 * @param assunto
	 * @param conteudo
	 * @param anexos
	 */
	public void enviaEmailHtml(String remetente, List<String> destinatarios,
			List<String> destinatariosOcultos, String assunto, String conteudo,
			AnexoEmail... anexos) {
		//TODO reavaliar uso deste metodo
	}

	private void enviaEmailHtml(ContatoEmail remetente,
			List<ContatoEmail> destinatarios,
			List<ContatoEmail> destinatariosOcultos, String assunto,
			String conteudo, AnexoEmail... anexos) {
		try {			
			
			emailInformation = new EmailInformation(remetente, destinatarios,
					destinatariosOcultos, assunto, conteudo, anexos);
			
			HtmlEmail email;
			
			email = new HtmlEmail();
						
			if (!ArrayUtils.isEmpty(anexos)){
				for (AnexoEmail anexoEmail : anexos){					
					 email.attach(new ByteArrayDataSource(anexoEmail.getByteArray(), "application/pdf"), anexoEmail.getFileName(), "anexo");
				}
			}
						
			// Utilize o hostname do seu provedor de email
			email.setHostName(hostName);
			// Adicione os destinatários
			for (ContatoEmail destinatario: destinatarios){
				email.addTo(destinatario.getEmail(), destinatario.getNome());
			}
			// Configure o seu email do qual enviará
			email.setFrom(remetente.getEmail(), remetente.getNome());
			// Adicione um assunto
			email.setSubject(assunto);
			// Adicione a mensagem do email
			email.setHtmlMsg("<html>"+ conteudo + "</html>");	
			email.setTextMsg("Seu servidor de e-mail não suporta mensagem HTML");
			// Para autenticar no servidor é necessário chamar os dois métodos
			// abaixo
//			System.out.println("autenticando...");
//			email.setSSL(true);
//			email.setAuthentication("username", "senha");
//			System.out.println("enviando...");
			email.send();
			
		} catch (Exception e) {
			LOG.error("Erro ao enviar o e-mail de " + remetente + " para "
					+ listaDestinatarios(destinatarios), e);
		}
	}
	
	/**
	 * Utilizado para enviar email.
	 * 
	 * @param remetente
	 * @param destinatarios
	 * @param destinatariosOcultos
	 * @param assunto
	 * @param conteudo
	 * @param anexos
	 */
	public void enviarEmail(ContatoEmail remetente,
			List<ContatoEmail> destinatarios,
			List<ContatoEmail> destinatariosOcultos, String assunto,
			String conteudo, AnexoEmail... anexos) {
		doEnviaEmail(remetente, destinatarios, destinatariosOcultos, assunto, conteudo, anexos);		
	}
	
	/**
	 * @deprecated usar enviarEmail
	 * @see EmailUtil#enviarEmail(ContatoEmail, List, List, String, String, AnexoEmail...)
	 * 
	 * @param remetente
	 * @param destinatarios
	 * @param destinatariosOcultos
	 * @param assunto
	 * @param conteudo
	 * @param anexos
	 */
	public void enviaEmail(ContatoEmail remetente,
			List<ContatoEmail> destinatarios,
			List<ContatoEmail> destinatariosOcultos, String assunto,
			String conteudo, AnexoEmail... anexos) {
		//TODO reavaliar uso deste metodo
	}
	
	private void doEnviaEmail(ContatoEmail remetente,
			List<ContatoEmail> destinatarios,
			List<ContatoEmail> destinatariosOcultos, String assunto,
			String conteudo, AnexoEmail... anexos) {
		try {			
			
			emailInformation = new EmailInformation(remetente, destinatarios,
					destinatariosOcultos, assunto, conteudo, anexos);
			
			Email email;
						
			if (ArrayUtils.isEmpty(anexos)){
				email = new SimpleEmail();
			}else{
				
				email =  new MultiPartEmail();
				for (AnexoEmail anexoEmail : anexos){
					((MultiPartEmail) email).attach(new ByteArrayDataSource(anexoEmail.getByteArray(), "application/pdf"), anexoEmail.getFileName(), "anexo");
				}
			}
						
			// Utilize o hostname do seu provedor de email
			email.setHostName(hostName);
			// Adicione os destinatários
			for (ContatoEmail destinatario: destinatarios){
				email.addTo(destinatario.getEmail(), destinatario.getNome());
			}
			// Configure o seu email do qual enviará
			email.setFrom(remetente.getEmail(), remetente.getNome());
			// Adicione um assunto
			email.setSubject(assunto);
			// Adicione a mensagem do email
			email.setMsg(conteudo);
			// Para autenticar no servidor é necessário chamar os dois métodos
			// abaixo
//			System.out.println("autenticando...");
//			email.setSSL(true);
//			email.setAuthentication("username", "senha");
//			System.out.println("enviando...");
			email.send();
			
		} catch (Exception e) {
			LOG.error("Erro ao enviar o e-mail de " + remetente + " para "
					+ listaDestinatarios(destinatarios), e);
		}
	}

	private String listaDestinatarios(List<ContatoEmail> destinatarios) {
		StringBuffer sb = new StringBuffer();

		for (ContatoEmail destinatario : destinatarios) {
			if (sb.length() > 0) {
				sb.append(',');
			}

			sb.append(destinatario);
		}

		return sb.toString();
	}

	

	public EmailInformation getEmailInformation() {
		return emailInformation;
	}

	public void setEmailInformation(EmailInformation emailInformation) {
		this.emailInformation = emailInformation;
	}

}
