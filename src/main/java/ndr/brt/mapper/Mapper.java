package ndr.brt.mapper;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import static org.apache.commons.lang3.ClassUtils.isPrimitiveOrWrapper;

public class Mapper<S, D> {
    private final Class<S> sourceClass;
    private final Class<D> destinationClass;
    private Map<String, String> configuration = new HashMap<>();

    public Mapper(Class<S> sourceClass, Class<D> destinationClass) {
        this.sourceClass = sourceClass;
        this.destinationClass = destinationClass;
    }

    public D map(S source) {
        try {
            D destination = destinationClass.newInstance();
            mapSameNameFields(source, destination);

            configuration.entrySet().forEach(e -> {
                Object sourceValue = getValueAt(e.getKey(), source);
                if (sourceValue != null) {
                    setFieldAt(e.getValue(), destination, sourceValue);
                }
            });
            return destination;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void mapSameNameFields(Object source, Object destination) {
        Field[] sourceFields = source.getClass().getDeclaredFields();
        Class<?> destinationClass = destination.getClass();
        for (Field field : sourceFields) {
            field.setAccessible(true);
            String fieldName = field.getName();
            try {
                Field destinationField = destinationClass.getDeclaredField(fieldName);
                destinationField.setAccessible(true);

                Object sourceValue = field.get(source);
                if (isSortOfPrimitive(field, sourceValue)) {
                    destinationField.set(destination, sourceValue);
                } else {
                    Class<?> type = destinationField.getType();
                    Object newDestination = type.newInstance();
                    destinationField.set(destination, newDestination);
                    mapSameNameFields(sourceValue, newDestination);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

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

    private boolean isSortOfPrimitive(Field field, Object sourceValue) {
        return isPrimitiveOrWrapper(field.getClass()) || String.class.equals(sourceValue.getClass());
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

    public void configure(String source, String destination) {
        configuration.put(source, destination);
    }

}
