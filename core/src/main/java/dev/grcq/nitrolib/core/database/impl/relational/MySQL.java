package dev.grcq.nitrolib.core.database.impl.relational;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import dev.grcq.nitrolib.core.annotations.orm.Column;
import dev.grcq.nitrolib.core.annotations.orm.Entity;
import dev.grcq.nitrolib.core.annotations.orm.Id;
import dev.grcq.nitrolib.core.database.Condition;
import dev.grcq.nitrolib.core.database.RelationalDatabase;
import dev.grcq.nitrolib.core.utils.KeyValue;
import dev.grcq.nitrolib.core.utils.LogUtil;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class MySQL implements RelationalDatabase {

    @Getter
    private Connection connection;

    private String host;
    private String user;
    private String password;
    private String database;
    private List<KeyValue<String, String>> params;

    public MySQL(String database, String host, int port, String user, String password) {
        this(database, host + ":" + port, user, password, Collections.emptyList());
    }

    public MySQL(String database, String host, String user, String password) {
        this(database, host, user, password, Collections.emptyList());
    }

    public MySQL(String database, String host, int port, String user, String password, List<KeyValue<String, String>> params) {
        this(database, host + ":" + port, user, password, params);
    }

    public MySQL(String database, String host, String user, String password, List<KeyValue<String, String>> params) {
        this.host = host.split(":").length == 1 ? host + ":3306" : host;
        this.user = user;
        this.password = password;
        this.params = params;
        this.database = database;
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
            this.connection = DriverManager.getConnection("jdbc:mysql://" + host + "/" + database + "?" + params, user, password);
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
    public ResultSet execute(String query, Object... params) {
        Preconditions.checkNotNull(this.connection, "Connection is null");
        try {
            PreparedStatement statement = this.connection.prepareStatement(query);
            for (int i = 0; i < params.length; i++) {
                statement.setObject(i + 1, params[i]);
            }

            return statement.executeQuery();
        } catch (SQLException e) {
            LogUtil.handleException("Failed to execute query", e);
            return null;
        }
    }

    @Override
    public void updateQuery(String query, Object... params) {
        Preconditions.checkNotNull(this.connection, "Connection is null");
        try {
            PreparedStatement statement = this.connection.prepareStatement(query);
            for (int i = 0; i < params.length; i++) {
                statement.setObject(i + 1, params[i]);
            }

            statement.executeUpdate();
        } catch (SQLException e) {
            LogUtil.handleException("Failed to execute update", e);
        }
    }

    @Override
    public void createTable(String name, List<KeyValue<String, String>> columns, boolean ifNotExists) {
        QueryBuilder builder = QueryBuilder.builder()
                .createTable(name, columns, ifNotExists);
        updateQuery(builder);
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

            KeyValue<String, String> id = parseField(field, false, false);
            if (id != null) {
                columns.add(id);
            }
        }

        createTable(tableName, columns, true);
    }

    @Override
    public void dropTable(String name) {
        QueryBuilder builder = QueryBuilder.builder()
                .dropTable(name);
        updateQuery(builder);
    }

    @Override
    public <T> void insert(T object) {
        if (!object.getClass().isAnnotationPresent(Entity.class)) {
            LogUtil.error("Class " + object.getClass().getName() + " is not annotated with @Entity");
            return;
        }

        Entity entity = object.getClass().getAnnotation(Entity.class);
        String table = entity.value().isEmpty() ? object.getClass().getSimpleName().toLowerCase() : entity.value();
        QueryBuilder builder = QueryBuilder.builder();

        List<String> columns = Lists.newArrayList();
        List<Object> values = Lists.newArrayList();
        for (Field field : object.getClass().getDeclaredFields()) {
            if (!field.isAnnotationPresent(Column.class)) continue;

            KeyValue<String, String> id = parseField(field, true, true);
            if (id != null) {
                columns.add(id.getKey());
                field.setAccessible(true);
                try {
                    values.add(field.get(object));
                } catch (IllegalAccessException e) {
                    LogUtil.handleException("Failed to access field", e);
                }
            }
        }

        builder.insert(table, columns.toArray(new String[0])).values(values.toArray());
        updateQuery(builder);
    }

    @Override
    public <T> T create(Class<T> clazz, List<KeyValue<String, Object>> columns) {
        try {
            Constructor<T> constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            T object = constructor.newInstance();
            for (KeyValue<String, Object> column : columns) {
                Field field = clazz.getDeclaredField(column.getKey());
                field.setAccessible(true);
                field.set(object, column.getValue());
            }

            insert(object);
            Condition[] where = columns.stream().map(kv -> Condition.eq(kv.getKey(), kv.getValue())).toArray(Condition[]::new);
            return selectOne(clazz, where);
        } catch (NoSuchMethodException e) {
            LogUtil.error("A no-args constructor is required for classes annotated with @Entity, " + clazz.getName() + " does not have one.");
        } catch (Exception e) {
            LogUtil.handleException("Failed to create object", e);
        }

        return null;
    }

    @Override
    public <T> void update(T object, @Nullable Condition[] where) {
        if (!object.getClass().isAnnotationPresent(Entity.class)) {
            LogUtil.error("Class " + object.getClass().getName() + " is not annotated with @Entity");
            return;
        }

        Entity entity = object.getClass().getAnnotation(Entity.class);
        String table = entity.value().isEmpty() ? object.getClass().getSimpleName().toLowerCase() : entity.value();
        QueryBuilder builder = QueryBuilder.builder();

        List<KeyValue<String, Object>> columns = Lists.newArrayList();
        for (Field field : object.getClass().getDeclaredFields()) {
            if (!field.isAnnotationPresent(Column.class)) continue;

            KeyValue<String, String> id = parseField(field, true, true);
            if (id != null) {
                field.setAccessible(true);
                try {
                    columns.add(KeyValue.of(id.getKey(), field.get(object)));
                } catch (IllegalAccessException e) {
                    LogUtil.handleException("Failed to access field", e);
                }
            }
        }

        builder.update(table, columns.toArray(new KeyValue[0]));
        if (where != null && where.length > 0) {
            builder.where(where);
        }

        updateQuery(builder);
    }

    @Override
    public void delete(String table, @Nullable Condition... where) {
        QueryBuilder builder = QueryBuilder.builder()
                .delete().from(table);
        if (where != null && where.length > 0) {
            builder.where(where);
        }

        updateQuery(builder);
    }

    @Override
    public <T> T selectOne(Class<T> clazz, @Nullable Condition... where) {
        Collection<T> objects = selectAll(clazz, where);
        return objects.isEmpty() ? null : objects.iterator().next();
    }

    @Override
    public <T> Collection<T> selectAll(Class<T> clazz, @Nullable Condition... where) {
        List<T> objects = Lists.newArrayList();
        if (!clazz.isAnnotationPresent(Entity.class)) {
            LogUtil.error("Class " + clazz.getName() + " is not annotated with @Entity");
            return objects;
        }

        Entity entity = clazz.getAnnotation(Entity.class);
        String table = entity.value().isEmpty() ? clazz.getSimpleName().toLowerCase() : entity.value();

        QueryBuilder builder = QueryBuilder.builder()
                .select("*").from(table);
        if (where != null && where.length > 0) {
            builder.where(where);
        }

        try (ResultSet rs = execute(builder)) {
            if (rs == null) return objects;

            while (rs.next()) {
                objects.add(parseClass(clazz, rs));
            }
        } catch (Exception e) {
            LogUtil.handleException("Failed to select from " + database + "." + table, e);
        }

        return objects;
    }

    private <T> T parseClass(Class<T> clazz, ResultSet rs) throws Exception {
        try {
            Constructor<T> constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            T object = constructor.newInstance();
            for (Field field : clazz.getDeclaredFields()) {
                if (!field.isAnnotationPresent(Column.class)) continue;

                field.setAccessible(true);
                Column column = field.getAnnotation(Column.class);
                String columnName = column.value().isEmpty() ? field.getName().toLowerCase() : column.value();
                Object value = rs.getObject(columnName);
                if (value != null) field.set(object, value);
            }

            return object;
        } catch (NoSuchMethodException e) {
            LogUtil.error("A no-args constructor is required for classes annotated with @Entity, " + clazz.getName() + " does not have one.");
            throw e;
        }
    }

    private KeyValue<String, String> parseField(Field field, boolean onlyTypeAsValue, boolean ignoreAutoIncrement) {
        Column column = field.getAnnotation(Column.class);
        String columnName = column.value().isEmpty() ? field.getName().toLowerCase() : column.value();
        boolean nullable = column.nullable();
        int length = column.length();

        KeyValue<String, String> id = null;
        switch (field.getType().getSimpleName()) {
            case "String":
                Preconditions.checkArgument(length > 0, "Column length must be greater than 0");
                id = KeyValue.of(columnName, (length == Integer.MAX_VALUE ? "TEXT" : "VARCHAR(" + length + ")") + (nullable ? "" : " NOT NULL"));
                break;
            case "int":
            case "Integer":
                id = KeyValue.of(columnName, "INT" + (nullable || onlyTypeAsValue ? "" : " NOT NULL"));
                break;
            case "long":
            case "Long":
                id = KeyValue.of(columnName, "BIGINT" + (nullable || onlyTypeAsValue ? "" : " NOT NULL"));
                break;
            case "boolean":
            case "Boolean":
                id = KeyValue.of(columnName, "BOOLEAN" + (nullable || onlyTypeAsValue ? "" : " NOT NULL"));
                break;
            case "double":
            case "Double":
                id = KeyValue.of(columnName, "DOUBLE" + (nullable || onlyTypeAsValue ? "" : " NOT NULL"));
                break;
            case "float":
            case "Float":
                id = KeyValue.of(columnName, "FLOAT" + (nullable || onlyTypeAsValue ? "" : " NOT NULL"));
                break;
            case "short":
            case "Short":
                id = KeyValue.of(columnName, "SMALLINT" + (nullable || onlyTypeAsValue ? "" : " NOT NULL"));
                break;
            case "byte":
            case "Byte":
                id = KeyValue.of(columnName, "TINYINT" + (nullable || onlyTypeAsValue ? "" : " NOT NULL"));
                break;
            case "char":
            case "Character":
                id = KeyValue.of(columnName, "CHAR(1)" + (nullable || onlyTypeAsValue ? "" : " NOT NULL"));
                break;
            default:
                if (Iterable.class.isAssignableFrom(field.getType())) {
                    LogUtil.warn("Iterables are not yet supported, it will be added in the future.");
                    break;
                }

                if (field.getType().isAnnotationPresent(Entity.class)) {
                    LogUtil.warn("Recursive entities are not yet supported, it will be added in the future.");
                    break;
                }

                LogUtil.warn("Unsupported column type: " + field.getType().getSimpleName());
                break;
        }

        if (id != null) {
            if (field.isAnnotationPresent(Id.class)) {
                if (ignoreAutoIncrement) return null;
                if (onlyTypeAsValue) return id;

                Id anno = field.getAnnotation(Id.class);
                id.setValue(id.getValue() + (anno.autoIncrement() ? " AUTO_INCREMENT " : " ") + "PRIMARY KEY");
            }
        }

        return id;
    }
}
