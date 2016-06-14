package br.com.ecodif.security;

import java.io.IOException;
import java.util.Enumeration;

import javax.inject.Inject;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import br.com.ecodif.service.AccessTokenService;

/**
 * Classe responsável por filtrar as requisições feitas a API.
 * @author Andreza Lima.
 *
 */
public class Filter implements javax.servlet.Filter {

	/**
	 * Referência para a classe de serviço de um Token de acesso.
	 * 
	 * @see br.com.ecodif.service.AccessTokenService
	 */
	@Inject
	private AccessTokenService accessTokenService;

	@Override
	public void destroy() {

	}

	/**
	 * Método responsável por filtrar as requisições, verificando  se o token
	 * é passado e se é valido.
	 */
	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {

		HttpServletRequest httpRequest = (HttpServletRequest) request;
		Enumeration<String> headerNames = httpRequest.getHeaderNames();
		while (headerNames.hasMoreElements()) {
			if (headerNames.nextElement().equals("token")) {
				String token = (String) httpRequest.getHeader("token");
				String user = accessTokenService.validateToken(token);
				if (user != null) {
					HttpServletRequest req = (HttpServletRequest) request;
					req.setAttribute("user", user);
					chain.doFilter(req, response);
				}
			}
		}
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {

	}

}
