package io.github.Leandro208.projetoESIG.util;

import java.util.Collection;
import java.util.Map;

import io.github.Leandro208.projetoESIG.entities.BaseEntity;

public class ValidatorUtils {

	public static final boolean isEmpty(Object o) {
		if (o == null)
			return true;
		if (o instanceof String)
			return isEmpty( (String) o);
		if (o instanceof Number) {
			Number i = (Number) o;
			return (i.doubleValue() == 0.0);
		}
		if (o instanceof BaseEntity)
			return ((BaseEntity) o).getId() == 0;
		if (o instanceof Object[])
			return ((Object[]) o).length == 0;
		if (o instanceof int[])
			return ((int[]) o).length == 0;
		if (o instanceof Collection<?>)
			return ((Collection<?>) o).size() == 0;
		if (o instanceof Map<?, ?>)
			return ((Map<?, ?>) o).size() == 0;
		return false;
	}
}
