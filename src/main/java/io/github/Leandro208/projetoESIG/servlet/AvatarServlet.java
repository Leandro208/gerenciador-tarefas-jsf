package io.github.Leandro208.projetoESIG.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import io.github.Leandro208.projetoESIG.dao.DAOException;
import io.github.Leandro208.projetoESIG.dto.FotoPerfilDTO;
import io.github.Leandro208.projetoESIG.dto.UsuarioDTO;
import io.github.Leandro208.projetoESIG.services.PerfilService;

public class AvatarServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || !(session.getAttribute("usuario") instanceof UsuarioDTO)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        Long idUsuario;
        try {
            idUsuario = Long.valueOf(request.getParameter("id"));
        } catch (Exception e) {
            enviarPlaceholder(request, response);
            return;
        }

        PerfilService service = new PerfilService();
        try {
            FotoPerfilDTO foto = service.buscarFoto(idUsuario);
            if (foto == null) {
                enviarPlaceholder(request, response);
                return;
            }
            response.setContentType(foto.getTipoMime());
            response.setHeader("X-Content-Type-Options", "nosniff");
            response.setHeader("Cache-Control", "private, no-store, max-age=0");
            response.setContentLength(foto.getConteudo().length);
            response.getOutputStream().write(foto.getConteudo());
        } catch (DAOException e) {
            getServletContext().log("Falha ao carregar foto de perfil", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void enviarPlaceholder(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.sendRedirect(response.encodeRedirectURL(
                request.getContextPath() + "/resources/img/profile-placeholder.svg"));
    }
}
