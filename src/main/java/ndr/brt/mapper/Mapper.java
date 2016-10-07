package ndr.brt.mapper;

import java.util.HashMap;
import java.util.Map;

public class Mapper<S, D> {
    private final Class<D> destinationClass;
    private final ConfigurationMapper configurationMapper = new ConfigurationMapper();
    private final DefaultMapper defaultMapper = new DefaultMapper();
    private Map<String, String> configuration = new HashMap<>();

    public Mapper(Class<D> destinationClass) {
        this.destinationClass = destinationClass;
    }

    public D map(S source) {
        try {
            D destination = destinationClass.newInstance();
            defaultMapper.map(source, destination);
            configurationMapper.map(source, destination, configuration);
            return destination;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void configure(String source, String destination) {
        configuration.put(source, destination);
    }

}
