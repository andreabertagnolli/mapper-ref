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
        Mapper<Source, Destination> mapper = new Mapper<>(Source.class, Destination.class);
        Destination destination = mapper.map(new Source("value"));

        assertEquals("value", destination.getField());
    }

    @Test
    public void map_with_different_field_name() throws Exception {
        Mapper<Source, DestinationWithOtherField> mapper = new Mapper<>(Source.class, DestinationWithOtherField.class);
        mapper.configure("field", "other");

        DestinationWithOtherField destination = mapper.map(new Source("value"));

        assertEquals("value", destination.getOther());
    }

    @Test
    public void map_enclosed_to_enclosed() throws Exception {
        SourceWithEnclosed sourceWithEnclosed = new SourceWithEnclosed(new Source("value"));
        Mapper<SourceWithEnclosed, DestinationWithEnclosed> mapper = new Mapper<>(SourceWithEnclosed.class, DestinationWithEnclosed.class);

        DestinationWithEnclosed destinationWithEnclosed = mapper.map(sourceWithEnclosed);

        assertEquals("value", destinationWithEnclosed.getEnclosed().getField());
    }

    @Test
    public void map_enclosed_to_enclosed_with_another_name() throws Exception {
        SourceWithEnclosed sourceWithEnclosed = new SourceWithEnclosed(new Source("value"));
        Mapper<SourceWithEnclosed, DestinationWithEnclosedWithOtherName> mapper = new Mapper<>(SourceWithEnclosed.class, DestinationWithEnclosedWithOtherName.class);
        mapper.configure("enclosed.field", "other.field");

        DestinationWithEnclosedWithOtherName destinationWithEnclosedWithOtherName = mapper.map(sourceWithEnclosed);

        assertEquals("value", destinationWithEnclosedWithOtherName.getOther().getField());
    }

    @Test
    public void map_enclose_with_different_field_name() throws Exception {
        SourceWithEnclosed sourceWithEnclosed = new SourceWithEnclosed(new Source("value"));
        Mapper<SourceWithEnclosed, DestinationWithEnclosedWithOtherFieldName> mapper = new Mapper<>(SourceWithEnclosed.class, DestinationWithEnclosedWithOtherFieldName.class);
        mapper.configure("enclosed.field", "enclosed.other");

        DestinationWithEnclosedWithOtherFieldName destinationWithEnclosedWithOtherFieldName = mapper.map(sourceWithEnclosed);

        assertEquals("value", destinationWithEnclosedWithOtherFieldName.getEnclosed().getOther());
    }

    @Test
    public void map_enclose_with_different_name_and_different_field_name() throws Exception {
        SourceWithEnclosed sourceWithEnclosed = new SourceWithEnclosed(new Source("value"));
        Mapper<SourceWithEnclosed, DestinationWithEnclosedDifferentNameDirrerentField> mapper = new Mapper<>(SourceWithEnclosed.class, DestinationWithEnclosedDifferentNameDirrerentField.class);
        mapper.configure("enclosed.field", "other.other");

        DestinationWithEnclosedDifferentNameDirrerentField destination = mapper.map(sourceWithEnclosed);

        assertEquals("value", destination.getOther().getOther());
    }

    private class DestinationWithoutDefaultConstructor { }
}
