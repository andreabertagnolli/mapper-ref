package ndr.brt.mapper;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import static java.util.Arrays.stream;

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

            stream(sourceClass.getDeclaredFields())
                .map(c -> extractNameAndValue(c, source))
                .map(this::applyConfiguration)
                .forEach(nv -> {
                    try {
                        Field destinationField = destinationClass.getDeclaredField(nv.getName());
                        destinationField.setAccessible(true);
                        destinationField.set(destination, nv.getValue());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });

            return destination;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private NameAndValue applyConfiguration(NameAndValue nv) {
        String sourceName = nv.getName();

        if (configuration.containsKey(sourceName)) {
            return new NameAndValue(configuration.get(sourceName), nv.getValue());
        }
        else {
            return nv;
        }

    }

    private NameAndValue extractNameAndValue(Field c, S source) {
        c.setAccessible(true);
        try {
            return new NameAndValue(c.getName(), c.get(source));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void configure(String source, String destination) {
        configuration.put(source, destination);
    }

    private class NameAndValue {
        private final String name;
        private final Object value;

        public NameAndValue(String name, Object value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public Object getValue() {
            return value;
        }
    }
}
