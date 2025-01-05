package dev.grcq.nitrolib.core.serialization.adapters;

import dev.grcq.nitrolib.core.serialization.elements.FileElement;

public interface Serializer<T> {

    FileElement serialize(T object, AdapterContext context);

}
