package dev.grcq.nitrolib.core.database.impl.relational;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import dev.grcq.nitrolib.core.annotations.orm.Column;
import dev.grcq.nitrolib.core.annotations.orm.Entity;
import dev.grcq.nitrolib.core.annotations.orm.Id;
import dev.grcq.nitrolib.core.database.RelationalDatabase;
import dev.grcq.nitrolib.core.utils.KeyValue;
import dev.grcq.nitrolib.core.utils.LogUtil;
import lombok.Getter;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class MySQL implements RelationalDatabase {

    @Getter
    private Connection connection;

    private String host;
    private String user;
    private String password;
    private List<KeyValue<String, String>> params;

    public MySQL(String host, int port, String user, String password) {
        this(host + ":" + port, user, password, Collections.emptyList());
    }

    public MySQL(String host, String user, String password) {
        this(host, user, password, Collections.emptyList());
    }

    public MySQL(String host, int port, String user, String password, List<KeyValue<String, String>> params) {
        this(host + ":" + port, user, password, params);
    }

    public MySQL(String host, String user, String password, List<KeyValue<String, String>> params) {
        this.host = host;
        this.user = user;
        this.password = password;
        this.params = params;
    }

    @Override
    public void connect() {
        StringBuilder params = new StringBuilder();
        for (int i = 0; i < this.params.size(); i++) {
            KeyValue<String, String> param = this.params.get(i);
            params.append(param.getKey()).append("=").append(param.getValue());
            if (i < this.params.size() - 1) {
                params.append("&");
            }
        }
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.connection = DriverManager.getConnection("jdbc:mysql://" + host + "?" + params, user, password);
        } catch (ClassNotFoundException | SQLException e) {
            LogUtil.handleException("Failed to connect to MySQL database", e);
        }
    }

    @Override
    public void disconnect() {
        try {
            this.connection.close();
            this.connection = null;
        } catch (SQLException e) {
            LogUtil.handleException("Failed to disconnect from MySQL database", e);
        }
    }

    @Override
    public void execute(String query, Object... params) {
        Preconditions.checkNotNull(this.connection, "Connection is null");
        try {
            PreparedStatement statement = this.connection.prepareStatement(query);
            for (int i = 0; i < params.length; i++) {
                statement.setObject(i + 1, params[i]);
            }

            statement.execute();
            statement.close();
        } catch (SQLException e) {
            LogUtil.handleException("Failed to execute query", e);
        }
    }

    @Override
    public void createTable(String name, List<KeyValue<String, String>> columns) {
        QueryBuilder builder = QueryBuilder.builder()
                .createTable(name, columns);
        execute(builder);
    }

    @Override
    public void createTableORM(Class<?> clazz) {
        if (!clazz.isAnnotationPresent(Entity.class)) {
            LogUtil.error("Class " + clazz.getName() + " is not annotated with @Entity");
            return;
        }

        Entity entity = clazz.getAnnotation(Entity.class);
        String tableName = entity.value().isEmpty() ? clazz.getSimpleName().toLowerCase() : entity.value();

        List<KeyValue<String, String>> columns = Lists.newArrayList();
        for (Field field : clazz.getDeclaredFields()) {
            if (!field.isAnnotationPresent(Column.class)) continue;

            Column column = field.getAnnotation(Column.class);
            String columnName = column.value().isEmpty() ? field.getName().toLowerCase() : column.value();
            boolean nullable = column.nullable();

            switch (field.getType().getSimpleName()) {
                case "String":
                    columns.add(KeyValue.of(columnName, "VARCHAR(255)" + (nullable ? "" : " NOT NULL")));
                    break;
                case "int":
                case "Integer":
                    columns.add(KeyValue.of(columnName, "INT" + (nullable ? "" : " NOT NULL")));
                    break;
                case "long":
                case "Long":
                    columns.add(KeyValue.of(columnName, "BIGINT" + (nullable ? "" : " NOT NULL")));
                    break;
                case "boolean":
                case "Boolean":
                    columns.add(KeyValue.of(columnName, "BOOLEAN" + (nullable ? "" : " NOT NULL")));
                    break;
                case "double":
                case "Double":
                    columns.add(KeyValue.of(columnName, "DOUBLE" + (nullable ? "" : " NOT NULL")));
                    break;
                case "float":
                case "Float":
                    columns.add(KeyValue.of(columnName, "FLOAT" + (nullable ? "" : " NOT NULL")));
                    break;
                case "short":
                case "Short":
                    columns.add(KeyValue.of(columnName, "SMALLINT" + (nullable ? "" : " NOT NULL")));
                    break;
                case "byte":
                case "Byte":
                    columns.add(KeyValue.of(columnName, "TINYINT" + (nullable ? "" : " NOT NULL")));
                    break;
                case "char":
                case "Character":
                    columns.add(KeyValue.of(columnName, "CHAR(1)" + (nullable ? "" : " NOT NULL")));
                    break;
                default:
                    if (field.getType().isAnnotationPresent(Entity.class)) {
                        LogUtil.warn("Recursive entities are not yet supported, it will be added in the future.");
                        break;
                    }

                    LogUtil.warn("Unsupported column type: " + field.getType().getSimpleName());
                    break;
            }
        }

        createTable(tableName, columns);
    }

    @Override
    public void dropTable(String name) {
        QueryBuilder builder = QueryBuilder.builder()
                .dropTable(name);
        execute(builder);
    }

    @Override
    public <T> void insert(String table, T object) {
        QueryBuilder builder = QueryBuilder.builder()
                .insert(table);


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

    @Override
    public <T> Collection<T> selectAll(String table, String where) {
        return Collections.emptyList();
    }
}
