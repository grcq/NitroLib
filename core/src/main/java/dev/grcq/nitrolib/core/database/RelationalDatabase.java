package dev.grcq.nitrolib.core.database;

public interface RelationalDatabase extends IDatabase {

    void execute(String query);
    void execute(String query, Object... params);
    default void execute(QueryBuilder builder) {
        execute(builder, new Object[0]);
    }
    default void execute(QueryBuilder builder, Object... params) {
        execute(builder.build(), params);
    }

    void createTable(String name);
    void dropTable(String name);

    <T> void insert(String table, T object);
    <T> void update(String table, T object, String where);
    void delete(String table, String where);

    class QueryBuilder {
        private final StringBuilder query;

        private QueryBuilder() {
            query = new StringBuilder();
        }

        public static QueryBuilder builder() {
            return new QueryBuilder();
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
            appendColumns(values);
            query.append(") ");
            return this;
        }

        public QueryBuilder update(String table, String... columns) {
            query.append("UPDATE ").append(table).append(" SET ");
            appendColumns(columns);
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
