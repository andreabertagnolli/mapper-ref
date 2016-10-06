package ndr.brt.mapper;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import static org.apache.commons.lang3.ClassUtils.isPrimitiveOrWrapper;

public class Mapper<S, D> {
    private final Class<S> sourceClass;
    private final Class<D> destinationClass;
    private final ConfigurationMapper configurationMapper = new ConfigurationMapper();
    private Map<String, String> configuration = new HashMap<>();

    public Mapper(Class<S> sourceClass, Class<D> destinationClass) {
        this.sourceClass = sourceClass;
        this.destinationClass = destinationClass;
    }

    public D map(S source) {
        try {
            D destination = destinationClass.newInstance();
            mapDefaults(source, destination);
            configurationMapper.map(source, destination, configuration);
            return destination;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void mapDefaults(Object source, Object destination) {
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
                    mapDefaults(sourceValue, newDestination);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private boolean isSortOfPrimitive(Field field, Object sourceValue) {
        return isPrimitiveOrWrapper(field.getClass()) || String.class.equals(sourceValue.getClass());
    }

    public void configure(String source, String destination) {
        configuration.put(source, destination);
    }

}
