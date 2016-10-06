package ndr.brt.mapper;

import java.lang.reflect.Field;
import java.util.Map;

public class ConfigurationMapper<S, D> {

    public void map(S source, D destination, Map<String, String> configuration) {
        configuration.entrySet().forEach(e -> {
            Object sourceValue = getValueAt(e.getKey(), source);
            if (sourceValue != null) {
                setFieldAt(e.getValue(), destination, sourceValue);
            }
        });
    }

    private Field setFieldAt(String path, Object destination, Object value) {
        String fieldName = getFirstLevel(path);
        try {
            Field field = destination.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            Object fieldValue = field.get(destination);
            if (fieldValue == null) {
                fieldValue = field.getType().newInstance();
                field.set(destination, fieldValue);
            }

            if (isLastLevel(path)) {
                field.set(destination, value);
                return field;
            }
            else {
                return setFieldAt(removeFirstLevel(path), fieldValue, value);
            }

        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Object getValueAt(String path, Object source) {
        String fieldName = getFirstLevel(path);
        Object value = null;
        try {
            Field field = source.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            value = field.get(source);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (isLastLevel(path)) {
            return value;
        }
        else {
            return getValueAt(removeFirstLevel(path), value);
        }
    }

    private String removeFirstLevel(String path) {
        return path.substring(path.indexOf(".") + 1);
    }

    private boolean isLastLevel(String path) {
        return !path.contains(".");
    }

    private String getFirstLevel(String path) {
        int firstDotIndex = path.indexOf(".");
        if (firstDotIndex < 0) {
            return path;
        }
        else {
            return path.substring(0, firstDotIndex);
        }
    }
}