package dev.grcq.nitrolib.core.database.impl.relational;

import dev.grcq.nitrolib.core.database.RelationalDatabase;
import dev.grcq.nitrolib.core.utils.KeyValue;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class PostgreSQL implements RelationalDatabase {

    @Override
    public void execute(String query, Object... params) {

    }

    @Override
    public void createTable(String name, List<KeyValue<String, String>> columns) {

    }

    @Override
    public void createTableORM(Class<?> clazz) {

    }

    @Override
    public void dropTable(String name) {

    }

    @Override
    public <T> void insert(String table, T object) {

    }

    @Override
    public <T> void update(String table, T object, String where) {

    }

    @Override
    public void delete(String table, String where) {

    }

    @Override
    public void connect() {

    }

    @Override
    public void disconnect() {

    }

    @Override
    public <T> T selectOne(String table, String where) {
        return null;
    }

    @Override
    public <T> Collection<T> selectAll(String table, String where) {
        return Collections.emptyList();
    }
}
