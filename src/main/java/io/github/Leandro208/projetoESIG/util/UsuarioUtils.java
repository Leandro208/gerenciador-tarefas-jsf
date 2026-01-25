package io.github.Leandro208.projetoESIG.util;

import javax.faces.context.FacesContext;


import io.github.Leandro208.projetoESIG.dominio.Usuario;
import io.github.Leandro208.projetoESIG.dto.UsuarioDTO;

public class UsuarioUtils {

	public static UsuarioDTO getLogado() {
		return (UsuarioDTO) FacesContext.getCurrentInstance()
				.getExternalContext().getSessionMap().get("usuario");
	}
	
	public static boolean usuarioTemEquipe() {
		return getLogado().getEquipe() != null;
	}
}
