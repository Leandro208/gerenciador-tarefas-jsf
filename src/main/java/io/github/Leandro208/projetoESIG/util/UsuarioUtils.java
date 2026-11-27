package io.github.Leandro208.projetoESIG.util;

import javax.faces.context.FacesContext;


import io.github.Leandro208.projetoESIG.dto.UsuarioDTO;

public class UsuarioUtils {

	public static UsuarioDTO getLogado() {
		FacesContext context = FacesContext.getCurrentInstance();
		return context == null ? null : (UsuarioDTO) context.getExternalContext().getSessionMap().get("usuario");
	}
	
	public static boolean usuarioTemEquipe() {
		UsuarioDTO usuario = getLogado();
		return usuario != null && usuario.getEquipe() != null;
	}
}
