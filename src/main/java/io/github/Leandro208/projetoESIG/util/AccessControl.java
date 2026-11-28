package io.github.Leandro208.projetoESIG.util;

import io.github.Leandro208.projetoESIG.dto.UsuarioDTO;
import io.github.Leandro208.projetoESIG.dao.UsuarioDAO;
import io.github.Leandro208.projetoESIG.dominio.Equipe;
import io.github.Leandro208.projetoESIG.enums.Funcao;
import io.github.Leandro208.projetoESIG.dto.FormConsultaTarefaDto;

/** Server-side authorization checks for JSF actions and persistence commands. */
public final class AccessControl {

	private AccessControl() {
	}

	public static UsuarioDTO requireUser() {
		return requireUser(UsuarioUtils.getLogado());
	}

	/** Refreshes authorization context for the authenticated session user outside JSF requests too. */
	public static UsuarioDTO requireUser(UsuarioDTO user) {
		if (user == null || user.getId() == null) {
			throw new SecurityException("Autenticação necessária.");
		}
		UsuarioDAO dao = new UsuarioDAO();
		try {
			Object[] contexto = dao.buscarContextoAutorizacao(user.getId());
			if (contexto == null) throw new SecurityException("Conta indisponível.");
			user.setFuncao((Funcao) contexto[0]);
			user.setIdResponsavel((Long) contexto[1]);
			if (contexto[2] == null) {
				user.setEquipe(null);
			} else {
				Equipe equipe = new Equipe();
				equipe.setId((Long) contexto[2]);
				equipe.setNome((String) contexto[3]);
				user.setEquipe(equipe);
			}
		} catch (SecurityException e) {
			throw e;
		} catch (Exception e) {
			throw new IllegalStateException("Não foi possível validar o usuário atual.", e);
		} finally {
			dao.close();
		}
		return user;
	}

	public static UsuarioDTO requireAdmin() {
		UsuarioDTO user = requireUser();
		if (!user.isAdm()) {
			throw new SecurityException("Ação permitida somente para administradores.");
		}
		return user;
	}

	public static boolean podeAcessarEquipe(UsuarioDTO user, Long idEquipe) {
		return user != null && (user.isAdm()
				|| (user.getEquipe() != null && idEquipe != null
						&& idEquipe.equals(user.getEquipe().getId())));
	}

	public static void limitarConsultaDeTarefas(UsuarioDTO user, FormConsultaTarefaDto consulta) {
		if (user == null || consulta == null) throw new SecurityException("Consulta não autorizada.");
		if (!user.isAdm()) {
			Equipe equipe = user.getEquipe();
			if (equipe == null) {
				equipe = new Equipe();
				equipe.setId(-1L);
			}
			consulta.setEquipe(equipe);
		}
	}
}
