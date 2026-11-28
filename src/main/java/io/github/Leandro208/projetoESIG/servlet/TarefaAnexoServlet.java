package io.github.Leandro208.projetoESIG.servlet;

import java.io.IOException;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import io.github.Leandro208.projetoESIG.dto.TarefaAnexoArquivoDTO;
import io.github.Leandro208.projetoESIG.dto.UsuarioDTO;
import io.github.Leandro208.projetoESIG.services.TarefaAnexoService;

public class TarefaAnexoServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		Object sessaoUsuario = session == null ? null : session.getAttribute("usuario");
		if (!(sessaoUsuario instanceof UsuarioDTO)) {
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}

		Long idAnexo;
		try {
			idAnexo = Long.valueOf(request.getParameter("id"));
		} catch (Exception e) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}

		try {
			TarefaAnexoArquivoDTO anexo = new TarefaAnexoService()
					.buscarArquivoPermitido(idAnexo, (UsuarioDTO) sessaoUsuario);
			if (anexo == null) {
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
				return;
			}
			boolean imagemInline = "true".equals(request.getParameter("inline"))
					&& ("image/jpeg".equals(anexo.getTipoMime()) || "image/png".equals(anexo.getTipoMime()));
			String disposition = imagemInline ? "inline" : "attachment";
			String fallback = anexo.getNomeArquivo().replaceAll("[^A-Za-z0-9._-]", "_");
			String encodedName = URLEncoder.encode(anexo.getNomeArquivo(), "UTF-8").replace("+", "%20");
			response.setContentType(anexo.getTipoMime());
			response.setHeader("X-Content-Type-Options", "nosniff");
			response.setHeader("Cache-Control", "private, no-store, max-age=0");
			response.setHeader("Content-Disposition", disposition + "; filename=\"" + fallback
					+ "\"; filename*=UTF-8''" + encodedName);
			response.setContentLength(anexo.getConteudo().length);
			response.getOutputStream().write(anexo.getConteudo());
		} catch (SecurityException e) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
		} catch (RuntimeException e) {
			getServletContext().log("Falha ao carregar anexo de tarefa", e);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}
}
