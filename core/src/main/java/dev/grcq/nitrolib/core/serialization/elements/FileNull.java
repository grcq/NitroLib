package dev.grcq.nitrolib.core.serialization.elements;

public class FileNull extends FileElement {

    @Override
    public FileElement copy() {
        return new FileNull();
    }

    @Override
    public String toString() {
        return "null";
    }
}
