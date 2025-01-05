package dev.grcq.nitrolib.core.serialization.adapters;

import dev.grcq.nitrolib.core.serialization.elements.FileElement;

public interface Deserializer<T> {

    T deserialize(FileElement object, AdapterContext context);

}
