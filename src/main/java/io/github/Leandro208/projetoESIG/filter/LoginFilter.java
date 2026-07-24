package io.github.Leandro208.projetoESIG.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import io.github.Leandro208.projetoESIG.dto.UsuarioDTO;
import io.github.Leandro208.projetoESIG.enums.Funcao;

public class LoginFilter implements Filter {

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		HttpSession session = req.getSession();
		
		String url = req.getRequestURL().toString();
		UsuarioDTO usuario = (UsuarioDTO) session.getAttribute("usuario");
		
		if (usuario == null || usuario.getId() == null) {
			if (url.contains("/restricted")) {
				res.sendRedirect(req.getServletContext().getContextPath() + "/login.jsf");
				return;
			}
		} else if (url.contains("/adm") && usuario.getFuncao().equals(Funcao.USER)) {
			res.sendRedirect(req.getServletContext().getContextPath() + "/restricted/index.jsf");
			return;
		}
		
		chain.doFilter(request, response);
	}

	@Override
	public void destroy() {
	}

}
