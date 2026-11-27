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
import io.github.Leandro208.projetoESIG.dao.UsuarioDAO;

public class LoginFilter implements Filter {

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		HttpSession session = req.getSession(false);
		String path = req.getServletPath();
		UsuarioDTO usuario = session == null ? null : (UsuarioDTO) session.getAttribute("usuario");
		
		if (usuario == null || usuario.getId() == null) {
			if (path.startsWith("/restricted/")) {
				res.sendRedirect(req.getServletContext().getContextPath() + "/login.jsf");
				return;
			}
		} else if (path.startsWith("/restricted/adm/") && !usuarioEhAdministrador(usuario.getId())) {
			res.sendRedirect(req.getServletContext().getContextPath() + "/restricted/index.jsf");
			return;
		}
		
		chain.doFilter(request, response);
	}

	private boolean usuarioEhAdministrador(Long idUsuario) {
		UsuarioDAO dao = new UsuarioDAO();
		try {
			Object[] contexto = dao.buscarContextoAutorizacao(idUsuario);
			return contexto != null && contexto[0] == Funcao.ADM;
		} catch (RuntimeException e) {
			return false;
		} finally {
			dao.close();
		}
	}

	@Override
	public void destroy() {
	}

}
