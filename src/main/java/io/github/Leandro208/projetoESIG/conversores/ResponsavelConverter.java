package io.github.Leandro208.projetoESIG.conversores;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import io.github.Leandro208.projetoESIG.entities.Responsavel;
import io.github.Leandro208.projetoESIG.services.ResponsavelService;


@FacesConverter("responsavelConverter")
public class ResponsavelConverter implements Converter {

	private final ResponsavelService service;

	public ResponsavelConverter() {
		service = new ResponsavelService();
	}

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		return (Responsavel) service.buscarPorId(Long.valueOf(value));
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if(value == null || ((Responsavel) value).getId() == null) {
			return "";
		}
		return ((Responsavel) value).getId().toString();
	}
}
