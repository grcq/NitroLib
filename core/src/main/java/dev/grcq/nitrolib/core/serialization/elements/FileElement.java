package dev.grcq.nitrolib.core.serialization.elements;

public abstract class FileElement {

    public boolean isFileNull() {
        return this instanceof FileNull;
    }

    public boolean isFileArray() {
        return this instanceof FileArray;
    }

    public boolean isFileObject() {
        return this instanceof FileObject;
    }

    public boolean isFilePrimitive() {
        return this instanceof FilePrimitive;
    }

    public FileNull asFileNull() {
        if (isFileNull()) {
            return (FileNull) this;
        }

        throw new IllegalStateException("Element is not a FileNull");
    }

    public FileArray asFileArray() {
        if (isFileArray()) {
            return (FileArray) this;
        }

        throw new IllegalStateException("Element is not a FileArray");
    }

    public FileObject asFileObject() {
        if (isFileObject()) {
            return (FileObject) this;
        }

        throw new IllegalStateException("Element is not a FileObject");
    }

    public FilePrimitive asFilePrimitive() {
        if (isFilePrimitive()) {
            return (FilePrimitive) this;
        }

        throw new IllegalStateException("Element is not a FilePrimitive");
    }

    public abstract String toString();

    public abstract FileElement copy();

}
