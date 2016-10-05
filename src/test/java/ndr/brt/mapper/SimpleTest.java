package ndr.brt.mapper;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SimpleTest {

    @Test(expected = RuntimeException.class)
    public void when_destination_has_no_explicit_default_constructor_throws_exception() throws Exception {
        Mapper mapper = new Mapper<>(Object.class, DestinationWithoutDefaultConstructor.class);

        mapper.map(new Object());
    }

    @Test
    public void map_objects_with_same_fields() throws Exception {
        Source source = new Source();
        source.setField("value");
        Mapper<Source, Destination> mapper = new Mapper<>(Source.class, Destination.class);
        Destination destination = mapper.map(source);

        assertEquals("value", destination.getField());
    }

    @Test
    public void map_with_different_field_name() throws Exception {
        Source source = new Source();
        source.setField("value");
        Mapper<Source, DestinationWithOtherField> mapper = new Mapper<>(Source.class, DestinationWithOtherField.class);
        mapper.configure("field", "other");

        DestinationWithOtherField destination = mapper.map(source);

        assertEquals("value", destination.getOther());
    }

    @Test
    public void map_enclosed_to_enclosed() throws Exception {
        Source source = new Source();
        source.setField("value");
        SourceWithEnclosed sourceWithEnclosed = new SourceWithEnclosed(source);
        Mapper<SourceWithEnclosed, DestinationWithEnclosed> mapper = new Mapper<>(SourceWithEnclosed.class, DestinationWithEnclosed.class);

        DestinationWithEnclosed destinationWithEnclosed = mapper.map(sourceWithEnclosed);

        assertEquals("value", destinationWithEnclosed.getEnclosed().getField());
    }

    private class DestinationWithoutDefaultConstructor { }
}
