package dev.grcq.nitrolib.core.database.impl.relational;

import dev.grcq.nitrolib.core.database.Condition;
import dev.grcq.nitrolib.core.database.RelationalDatabase;
import dev.grcq.nitrolib.core.utils.KeyValue;
import org.jetbrains.annotations.Nullable;

import java.sql.ResultSet;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class PostgreSQL implements RelationalDatabase {

    @Override
    public ResultSet execute(String query, Object... params) {
        return null;
    }

    @Override
    public void updateQuery(String query, Object... params) {

    }

    @Override
    public void createTable(String name, List<KeyValue<String, String>> columns, boolean ifNotExists) {

    }

    @Override
    public void createTableORM(Class<?> clazz) {

    }

    @Override
    public void dropTable(String name) {

    }

    @Override
    public <T> void insert(T object) {

    }

    @Override
    public <T> T create(Class<T> clazz, List<KeyValue<String, Object>> columns) {
        return null;
    }

    @Override
    public <T> void update(T object, @Nullable Condition[] where) {

    }

    @Override
    public void delete(String table, @Nullable Condition[] where) {

    }

    @Override
    public void connect() {

    }

    @Override
    public void disconnect() {

    }

    @Override
    public <T> T selectOne(Class<T> clazz, @Nullable Condition[] where) {
        return null;
    }

    @Override
    public <T> Collection<T> selectAll(Class<T> clazz, @Nullable Condition[] where) {
        return Collections.emptyList();
    }
}
