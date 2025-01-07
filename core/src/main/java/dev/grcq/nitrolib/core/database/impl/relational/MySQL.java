package dev.grcq.nitrolib.core.database.impl.relational;

import dev.grcq.nitrolib.core.database.RelationalDatabase;

public class MySQL implements RelationalDatabase {

    @Override
    public void connect() {

    }

    @Override
    public void disconnect() {

    }

    @Override
    public void execute(String query) {

    }

    @Override
    public void execute(String query, Object... params) {

    }

    @Override
    public void createTable(String name) {

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
    public <T> T selectOne(String table, String where) {
        return null;
    }
}
