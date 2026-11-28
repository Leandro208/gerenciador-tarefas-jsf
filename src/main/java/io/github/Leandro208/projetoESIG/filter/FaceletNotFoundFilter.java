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

public class FaceletNotFoundFilter implements Filter {

	private static final String JSF_RESOURCE_PATH = "/javax.faces.resource/";

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		String servletPath = httpRequest.getServletPath();

		if (servletPath != null && servletPath.endsWith(".jsf")
				&& !servletPath.startsWith(JSF_RESOURCE_PATH)) {
			String faceletPath = servletPath.substring(0, servletPath.length() - 4) + ".xhtml";
			if (httpRequest.getServletContext().getResource(faceletPath) == null) {
				httpResponse.sendError(HttpServletResponse.SC_NOT_FOUND);
				return;
			}
		}

		chain.doFilter(request, response);
	}

	@Override
	public void destroy() {
	}
}
