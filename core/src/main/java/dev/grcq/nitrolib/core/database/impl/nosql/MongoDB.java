package dev.grcq.nitrolib.core.database.impl.nosql;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import dev.grcq.nitrolib.core.Constants;
import dev.grcq.nitrolib.core.database.NoSQLDatabase;
import dev.grcq.nitrolib.core.utils.Util;
import lombok.AllArgsConstructor;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class MongoDB implements NoSQLDatabase {

    private MongoClient client;
    private MongoDatabase database;
    private final String databaseName;
    private final String uri;

    public MongoDB(String uri) {
        this.uri = uri;
        this.databaseName = uri.substring(uri.lastIndexOf("/") + 1).split("\\?")[0];
    }

    public MongoDB(String host, int port, String database) {
        this(host, port, database, null, null);
    }

    public MongoDB(String address, String database, String username, String password) {
        this(address, address.contains(":") ? Integer.parseInt(address.split(":")[1]) : -1, database, username, password);
    }

    public MongoDB(String address, String database) {
        this(address, database, null, null);
    }

    public MongoDB(String host, int port, String database, String username, String password, boolean srv) {
        this("mongodb" + (srv ? "+srv" : "") + "://" + (username != null && password != null ? username + ":" + password + "@" : "") + host + (port > 1 ? ":" + port : "") + "/" + database);
    }

    public MongoDB(String host, int port, String database, String username, String password) {
        this(host, port, database, username, password, port > 0 && Util.isSrvAddress(host + ":" + port));
    }

    @Override
    public void connect() {
        this.client = MongoClients.create(this.uri);
        this.database = this.client.getDatabase(this.databaseName);
    }

    @Override
    public void disconnect() {
        this.client.close();
        this.client = null;
        this.database = null;
    }

    @Override
    public <T> void insert(@NotNull String collection, @NotNull T object) {
        Preconditions.checkNotNull(this.client, "Client is not connected");
        Preconditions.checkNotNull(this.database, "Database is not connected");

        MongoCollection<Document> mongoCollection = this.database.getCollection(collection);
        mongoCollection.insertOne(Document.parse(Constants.GSON.toJson(object)));
    }

    @Override
    public <T> void update(@NotNull String collection, @NotNull T object, @NotNull Bson filter) {
        Preconditions.checkNotNull(this.client, "Client is not connected");
        Preconditions.checkNotNull(this.database, "Database is not connected");

        MongoCollection<Document> mongoCollection = this.database.getCollection(collection);
        Document old = mongoCollection.find(filter).first();
        if (old == null) return;

        mongoCollection.replaceOne(old, Document.parse(Constants.GSON.toJson(object)));
    }

    @Override
    public void delete(@NotNull String collection, @Nullable Bson filter) {
        Preconditions.checkNotNull(this.client, "Client is not connected");
        Preconditions.checkNotNull(this.database, "Database is not connected");

        MongoCollection<Document> mongoCollection = this.database.getCollection(collection);
        if (filter == null)
            mongoCollection.deleteMany(new Document());
        else
            mongoCollection.deleteMany(filter);
    }

    @Override
    public <T> List<T> find(@NotNull String collection, @Nullable Bson filter, @NotNull Class<T> clazz) {
        Preconditions.checkNotNull(this.client, "Client is not connected");
        Preconditions.checkNotNull(this.database, "Database is not connected");

        MongoCollection<Document> mongoCollection = this.database.getCollection(collection);
        List<T> list = Lists.newArrayList();
        if (filter == null)
            for (Document document : mongoCollection.find())
                list.add(Constants.GSON.fromJson(document.toJson(), clazz));
        else
            for (Document document : mongoCollection.find(filter))
                list.add(Constants.GSON.fromJson(document.toJson(), clazz));
        return list;
    }

}
