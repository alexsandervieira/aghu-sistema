package br.gov.mec.aghu.core.action;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.core.commons.Parametro;

/**
 * Classe que mantém em cache o endereço de rede do host remoto (IP ou nome do
 * computador, dependendo do parâmetro do components.properties) e o InetAddress
 * do host remoto.
 * 
 * <br>
 * <b>Deve ser utilizado via métodos das controllers</b>
 * 
 * @author lcmoura
 * 
 */
@SessionScoped
public class HostRemotoCache implements Serializable {
	private static final long serialVersionUID = 628213165227040009L;
	
	private static final Log LOG = LogFactory.getLog(HostRemotoCache.class);

	@Inject
	@Parametro("identificar_host_remoto_por_ip")
	private String indentificarHostRemotoPorIp;

	protected boolean isIdentificarHostRemotoPorIp() {
		return Boolean.parseBoolean(indentificarHostRemotoPorIp);
	}

	/**
	 * Retorna endereço de rede do host remoto (IP ou nome do computador,
	 * dependendo do parâmetro do components.properties).
	 * 
	 * @return
	 * @throws UnknownHostException
	 */
	public String getEnderecoRedeHostRemoto() throws UnknownHostException {
		// --[BUSCA CONTEXTO PARA ADQUIRIR IP DO CLIENTE]
        FacesContext fc = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) fc
                .getExternalContext().getRequest();

        // Caso não tenha conseguido obter pela forma canônica ou normal,
        // tenta obter o IP pelo atributo X-Forwarded-For do header HTTP.
        // Se houver um valor retornado neste atributo e se ele for um IPv4
        // válido então este será forçado como o endereço do host. Isto serve
        // para casos onde o AGHU rode com proxy web na frente do servidor
        // de aplicação.
		LOG.debug("HostRemotoCache.getEnderecoRedeHostRemoto()");
		String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()
                && validarEnderecoIPv4(xForwardedFor)) {
            LOG.debug("Obteve o seguinte IP pelo atributo X-Forwarded-For do header HTTP: "
                    + xForwardedFor);
            return xForwardedFor;
        }
        
		InetAddress hostRemoto = getHostRemoto();

		if (this.isIdentificarHostRemotoPorIp()) {
			return hostRemoto.getHostAddress();
		}

		return hostRemoto.getHostName();
	}
	/**
     * Valida se um endereço de rede está no padrão IPv4. Endereços retornados
     * como válidos por este método incluem: 0.0.0.0 127.0.0.1 255.255.255.255
     * 192.168.0.1 192.00.0.1 001.0.0.1
     *
     * @param enderecoIPv4
     *            Um endereço de rede padrão IPv4
     * @return Verdadeiro caso o endereço esteja no padrão IPv4, falso caso
     *         contrário
     */
    private boolean validarEnderecoIPv4(String enderecoIPv4) {
        Pattern pattern = Pattern
                .compile("(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)");

        Matcher matcher = pattern.matcher(enderecoIPv4);
        return matcher.find() && matcher.group().equals(enderecoIPv4);
    }
	/**
	 * Retorna o InetAddress do cliente recuperado a partir do requestfornecido.<br />
	 * Não resolve o endereço e nome do localhost para permitir testes em
	 * desenvolvimento.
	 * 
	 * @see InetAddress
	 * @param request
	 * @return endereço de rede no formato IPv4
	 * @throws UnknownHostException
	 */
	public InetAddress getEnderecoIPv4HostRemoto(ServletRequest request)
			throws UnknownHostException {
		LOG.debug("HostRemotoCache.getEnderecoIPv4HostRemoto(request)");
		return HostRemotoUtil.getHostRemoto(request);
	}

	/**
	 * Retorna o InetAddress do cliente recuperado a partir do request do faces
	 * context.<br />
	 * Não resolve o endereço e nome do localhost para permitir testes em
	 * desenvolvimento.
	 * 
	 * @see InetAddress
	 * @return endereço de rede no formato IPv4
	 * @throws UnknownHostException
	 */
	public InetAddress getEnderecoIPv4HostRemoto() throws UnknownHostException {
		LOG.debug("HostRemotoCache.getEnderecoIPv4HostRemoto()");
		return this.getHostRemoto();
	}

	private InetAddress getHostRemoto() throws UnknownHostException {
		FacesContext fc = FacesContext.getCurrentInstance();
		HttpServletRequest request = (HttpServletRequest) fc
				.getExternalContext().getRequest();
		return HostRemotoUtil.getHostRemoto(request);
	}

}