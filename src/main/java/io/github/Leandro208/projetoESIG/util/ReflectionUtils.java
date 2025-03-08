package io.github.Leandro208.projetoESIG.util;

import java.lang.reflect.Field;

public class ReflectionUtils {

    
    public static Field[] getFieldsByName(Object obj, String fieldName) {
        Field[] fields = obj.getClass().getDeclaredFields();
        return java.util.Arrays.stream(fields)
                .filter(field -> field.getName().equals(fieldName))
                .toArray(Field[]::new);
    }

    public static void setFieldValue(Object obj, Field field, Object value) {
        try {
            field.setAccessible(true);
            field.set(obj, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Erro ao definir valor do campo: " + field.getName(), e);
        }
    }
}
