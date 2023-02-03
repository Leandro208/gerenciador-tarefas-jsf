package io.github.Leandro208.projetoESIG.util;

import javax.faces.context.FacesContext;


import io.github.Leandro208.projetoESIG.entities.Responsavel;

public class UsuarioUtils {

	public static Responsavel getLogado() {
		return (Responsavel) FacesContext.getCurrentInstance()
				.getExternalContext().getSessionMap().get("responsavel");
	}
}
