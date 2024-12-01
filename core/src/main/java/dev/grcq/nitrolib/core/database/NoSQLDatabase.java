package dev.grcq.nitrolib.core.database;

import org.bson.conversions.Bson;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface NoSQLDatabase extends IDatabase {

    <T> void insert(@NotNull String collection, @NotNull T object);
    <T> void update(@NotNull String collection, @NotNull T object, @NotNull Bson filter);
    void delete(@NotNull String collection, @Nullable Bson filter);
    <T> List<T> find(@NotNull String collection, @Nullable Bson filter, @NotNull Class<T> clazz);

}
