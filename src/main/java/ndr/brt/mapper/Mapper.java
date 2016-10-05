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
            recursiveMap(source, destination);
            return destination;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void recursiveMap(Object source, Object destination) {
        Field[] sourceFields = source.getClass().getDeclaredFields();
        Class<?> destinationClass = destination.getClass();
        for (Field field : sourceFields) {
            field.setAccessible(true);
            String fieldName = field.getName();
            try {
                if (configuration.containsKey(fieldName)) {
                    fieldName = configuration.get(fieldName);
                }

                Field destinationField = destinationClass.getDeclaredField(fieldName);
                destinationField.setAccessible(true);

                Object sourceValue = field.get(source);
                if (isPrimitiveOrWrapper(field.getClass()) || String.class.equals(sourceValue.getClass())) {
                    destinationField.set(destination, sourceValue);
                } else {
                    Class<?> type = destinationField.getType();
                    Object newDestination = type.newInstance();
                    destinationField.set(destination, newDestination);
                    recursiveMap(sourceValue, newDestination);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void configure(String source, String destination) {
        configuration.put(source, destination);
    }

}
