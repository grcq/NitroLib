package dev.grcq.nitrolib.core.database;

import com.mongodb.lang.Nullable;
import dev.grcq.nitrolib.core.annotations.orm.Entity;
import dev.grcq.nitrolib.core.utils.KeyValue;

import java.sql.ResultSet;
import java.util.Collection;
import java.util.List;

public interface RelationalDatabase extends IDatabase {

    ResultSet execute(String query, Object... params);
    default ResultSet execute(QueryBuilder builder, Object... params) {
        return execute(builder.build(), params);
    }

    void updateQuery(String query, Object... params);
    default void updateQuery(QueryBuilder builder, Object... params) {
        updateQuery(builder.build(), params);
    }

    default void createTable(String name, List<KeyValue<String, String>> columns) {
        createTable(name, columns, false);
    }
    void createTable(String name, List<KeyValue<String, String>> columns, boolean ifNotExists);
    void createTableORM(Class<?> clazz);
    void dropTable(String name);

    <T> T selectOne(Class<T> clazz, @Nullable Condition... where);
    <T> Collection<T> selectAll(Class<T> clazz, @Nullable Condition... where);
    <T> T create(Class<T> clazz, List<KeyValue<String, Object>> columns);

    <T> void insert(T object);
    <T> void update(T object, @Nullable Condition... where);
    void delete(String table, @Nullable Condition... where);

    class QueryBuilder {
        private final StringBuilder query;

        private QueryBuilder() {
            query = new StringBuilder();
        }

        public static QueryBuilder builder() {
            return new QueryBuilder();
        }

        public QueryBuilder createTable(String name, List<KeyValue<String, String>> columns) {
            return createTable(name, columns, false);
        }

        public QueryBuilder createTable(String name, List<KeyValue<String, String>> columns, boolean ifNotExists) {
            query.append("CREATE TABLE ");
            if (ifNotExists) query.append("IF NOT EXISTS ");

            query.append(name).append(" (");
            for (int i = 0; i < columns.size(); i++) {
                KeyValue<String, String> column = columns.get(i);
                query.append(column.getKey()).append(" ").append(column.getValue());
                if (i < columns.size() - 1) {
                    query.append(", ");
                }
            }
            query.append(");");
            return this;
        }

        public QueryBuilder dropTable(String name) {
            query.append("DROP TABLE ").append(name).append(";");
            return this;
        }

        public QueryBuilder select(String... columns) {
            query.append("SELECT ");
            appendColumns(columns);
            return this;
        }

        public QueryBuilder from(String table) {
            query.append(" FROM ").append(table);
            return this;
        }

        public QueryBuilder where(Condition[] conditions) {
            query.append(" WHERE ");
            for (int i = 0; i < conditions.length; i++) {
                Condition condition = conditions[i];
                query.append(condition);
                if (i < conditions.length - 1) {
                    query.append(condition.isNextIsOr() ? " OR " : " AND ");
                }
            }
            return this;
        }

        public QueryBuilder orderBy(String column, boolean ascending) {
            query.append(" ORDER BY ").append(column).append(ascending ? " ASC" : " DESC");
            return this;
        }

        public QueryBuilder limit(int limit) {
            query.append(" LIMIT ").append(limit);
            return this;
        }

        public QueryBuilder offset(int offset) {
            query.append(" OFFSET ").append(offset);
            return this;
        }

        public QueryBuilder insert(String table, String... columns) {
            query.append("INSERT INTO ").append(table).append(" (");
            appendColumns(columns);
            query.append(") ");
            return this;
        }

        public QueryBuilder values(Object... values) {
            query.append("VALUES (");
            for (int i = 0; i < values.length; i++) {
                if (values[i] instanceof String || values[i] instanceof Character) {
                    // Escape single and double quotes for security reasons
                    String value = values[i].toString()
                            .replaceAll("'", "\\\\'")
                            .replaceAll("\"", "\\\\\"");
                    query.append("'").append(value).append("'");
                } else {
                    query.append(values[i]);
                }

                if (i < values.length - 1) {
                    query.append(", ");
                }
            }
            query.append(") ");
            return this;
        }

        public QueryBuilder update(String table, KeyValue<String, Object>[] columns) {
            query.append("UPDATE ").append(table).append(" SET ");
            for (int i = 0; i < columns.length; i++) {
                KeyValue<String, Object> column = columns[i];
                query.append("`").append(column.getKey()).append("`").append(" = ");
                if (column.getValue() instanceof String || column.getValue() instanceof Character) {
                    // Escape single and double quotes for security reasons
                    String value = column.getValue().toString()
                            .replaceAll("'", "\\\\'")
                            .replaceAll("\"", "\\\\\"");
                    query.append("'").append(value).append("'");
                } else {
                    query.append(column.getValue());
                }

                if (i < columns.length - 1) {
                    query.append(", ");
                }
            }
            return this;
        }

        public QueryBuilder delete() {
            query.append("DELETE ");
            return this;
        }

        public String build() {
            return query.toString().trim() + ";";
        }

        private void appendColumns(Object[] columns) {
            for (int i = 0; i < columns.length; i++) {
                query.append(columns[i]);
                if (i < columns.length - 1) {
                    query.append(", ");
                }
            }
        }

    }
}
