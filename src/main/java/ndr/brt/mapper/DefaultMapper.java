package ndr.brt.mapper;

import java.lang.reflect.Field;

import static org.apache.commons.lang3.ClassUtils.isPrimitiveOrWrapper;

public class DefaultMapper {

    public void map(Object source, Object destination) {
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
                    map(sourceValue, newDestination);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private boolean isSortOfPrimitive(Field field, Object sourceValue) {
        return isPrimitiveOrWrapper(field.getClass()) || String.class.equals(sourceValue.getClass());
    }
}