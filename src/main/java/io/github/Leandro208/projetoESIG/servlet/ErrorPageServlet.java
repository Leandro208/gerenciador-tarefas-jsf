package io.github.Leandro208.projetoESIG.servlet;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ErrorPageServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(ErrorPageServlet.class.getName());
	private static final String ERROR_EXCEPTION = "javax.servlet.error.exception";
	private static final String ERROR_STATUS_CODE = "javax.servlet.error.status_code";
	private static final String ERROR_REQUEST_URI = "javax.servlet.error.request_uri";

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		boolean pageNotFound = getErrorStatus(request) == HttpServletResponse.SC_NOT_FOUND;
		request.setAttribute("errorTitle", pageNotFound ? "Página não encontrada" : "Ops, algo deu errado");
		request.setAttribute("errorMessage", pageNotFound
				? "O endereço que você acessou não existe ou foi alterado. Confira o link ou volte ao início."
				: "Não foi possível concluir sua solicitação. Tente novamente em alguns instantes.");
		logError(request);
		RequestDispatcher dispatcher = request.getRequestDispatcher("/erro.jsp");
		dispatcher.forward(request, response);
	}

	private int getErrorStatus(HttpServletRequest request) {
		Object statusAttribute = request.getAttribute(ERROR_STATUS_CODE);
		return statusAttribute instanceof Integer ? (Integer) statusAttribute : HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
	}

	private void logError(HttpServletRequest request) {
		Object exceptionAttribute = request.getAttribute(ERROR_EXCEPTION);
		Throwable exception = exceptionAttribute instanceof Throwable ? (Throwable) exceptionAttribute : null;
		int status = getErrorStatus(request);
		Object uriAttribute = request.getAttribute(ERROR_REQUEST_URI);
		String uri = uriAttribute instanceof String ? (String) uriAttribute : "desconhecida";

		if (exception != null) {
			LOGGER.log(Level.SEVERE, "Erro não tratado ao processar " + uri, exception);
		} else if (status >= 500) {
			LOGGER.severe("Erro HTTP " + status + " ao processar " + uri);
		} else {
			LOGGER.info("Erro HTTP " + status + " ao processar " + uri);
		}
	}
}
