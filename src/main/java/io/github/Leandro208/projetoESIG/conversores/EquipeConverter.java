package io.github.Leandro208.projetoESIG.conversores;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import io.github.Leandro208.projetoESIG.entities.Equipe;
import io.github.Leandro208.projetoESIG.services.EquipeService;

@FacesConverter("equipeConverter")
public class EquipeConverter implements Converter{

	private final EquipeService service;
	
	public EquipeConverter() {
		service = new EquipeService();
	}
	
	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		return (Equipe) service.buscarPorId(Long.valueOf(value));
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		return ((Equipe) value).getId().toString();
	}

	
}
