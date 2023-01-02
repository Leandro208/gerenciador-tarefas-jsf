package io.github.Leandro208.projetoESIG.conversores;

import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import io.github.Leandro208.projetoESIG.enums.PrioridadeEnum;

@FacesConverter("prioridadeConverter")
public class PrioridadeConverter implements Converter {
	
	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		return PrioridadeEnum.valueOf(Integer.valueOf(value));
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value == null) {
			return null;
		}
		PrioridadeEnum prioridade = (PrioridadeEnum) value;
		return String.valueOf(prioridade.getCodigo());
	}
}
