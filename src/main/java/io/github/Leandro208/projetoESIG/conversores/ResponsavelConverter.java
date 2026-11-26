package io.github.Leandro208.projetoESIG.conversores;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import io.github.Leandro208.projetoESIG.dao.DAOException;
import io.github.Leandro208.projetoESIG.dominio.Responsavel;
import io.github.Leandro208.projetoESIG.services.ResponsavelService;


@FacesConverter("responsavelConverter")
public class ResponsavelConverter implements Converter {

	private final ResponsavelService service;

	public ResponsavelConverter() {
		service = new ResponsavelService();
	}

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
        try {
            return (Responsavel) service.buscarPorId(Long.valueOf(value));
        } catch (DAOException e) {
            throw new RuntimeException(e);
        }
    }

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if(value == null || ((Responsavel) value).getId() == null) {
			return "";
		}
		return ((Responsavel) value).getId().toString();
	}
}
