package io.github.Leandro208.projetoESIG.filter;

import java.io.IOException;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import io.github.Leandro208.projetoESIG.entities.Responsavel;
import io.github.Leandro208.projetoESIG.enums.Funcao;

public class LoginFilter implements Filter {

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// TODO Auto-generated method stub

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		HttpSession session = req.getSession();
		
		String url = req.getRequestURL().toString();
		Responsavel responsavel = (Responsavel) session.getAttribute("responsavel");
		
		if(url.contains("/restricted") && (responsavel == null || responsavel.getId() == null)) {
			res.sendRedirect(req.getServletContext().getContextPath() + "/login.jsf");
		} 
		
		else if(url.contains("/adm") && (responsavel.getFuncao().equals(Funcao.USER))) {
			res.sendRedirect(req.getServletContext().getContextPath() + "/restricted/index.jsf");
			
		} 
		
		
		else {
			chain.doFilter(request, response);
		}
		
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

}
