package ndr.brt.mapper;

import com.sun.xml.internal.ws.util.StringUtils;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

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
            Arrays.stream(sourceClass.getMethods())
                .filter(c -> c.getName().startsWith("get"))
                .filter(c -> !"getClass".equals(c.getName()))
                .forEach(c -> {
                    String methodName = c.getName();
                    String name = methodName.substring(3);
                    String decapitalizedName = StringUtils.decapitalize(name);
                    if (configuration.containsKey(decapitalizedName)) {
                        name = StringUtils.capitalize(configuration.get(decapitalizedName));
                    }
                    try {
                        Method set = destinationClass.getMethod("set" + name, String.class);
                        Object defaultValue = c.invoke(source);
                        set.invoke(destination, defaultValue);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
            return destination;
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void configure(String source, String destination) {
        configuration.put(source, destination);
    }
}
