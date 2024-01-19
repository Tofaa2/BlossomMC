package net.blossom.dbm;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.*;
import com.mongodb.client.model.ReplaceOptions;
import org.bson.Document;
import org.bson.UuidRepresentation;
import org.bson.conversions.Bson;
import org.jetbrains.annotations.Blocking;

@Blocking
public final class BlossomDatabase {
    public static final ReplaceOptions UPSERT = new ReplaceOptions().upsert(true);

    private MongoClient client;
    private MongoDatabase database;

    public BlossomDatabase(DatabaseSettings settings) {
        this.client = MongoClients.create(
                MongoClientSettings.builder()
                        .applyConnectionString(new ConnectionString(settings.mongoUri()))
                        .uuidRepresentation(UuidRepresentation.STANDARD)
                        .build()
        );
        this.database = this.client.getDatabase(settings.databaseName());
    }

    public void replaceOne(String collection, Bson filter, Document document, ReplaceOptions options) {
        MongoCollection<Document> coll = this.database.getCollection(collection);
        coll.replaceOne(filter, document, options);
    }

    public void replaceOne(String collection, Bson filter, Document document) {
        this.replaceOne(collection, filter, document, UPSERT);
    }

    public FindIterable<Document> find(String collection, Bson filter) {
        MongoCollection<Document> coll = this.database.getCollection(collection);
        return coll.find(filter);
    }

    public Document findOne(String collection, Bson filter) {
        return find(collection, filter).first();
    }

    public void insertOne(String collection, Document document) {
        MongoCollection<Document> coll = this.database.getCollection(collection);
        coll.insertOne(document);
    }

    public void deleteOne(String collection, Bson filter) {
        MongoCollection<Document> coll = this.database.getCollection(collection);
        coll.deleteOne(filter);
    }

    public void deleteMany(String collection, Bson filter) {
        MongoCollection<Document> coll = this.database.getCollection(collection);
        coll.deleteMany(filter);
    }

    public void updateOne(String collection, Bson filter, Bson update) {
        MongoCollection<Document> coll = this.database.getCollection(collection);
        coll.updateOne(filter, update);
    }

    public MongoCollection<Document> getCollection(String name) {
        return this.database.getCollection(name);
    }

    public void close() {
        this.client.close();
    }

    public MongoClient getClient() {
        return this.client;
    }

    public MongoDatabase getDatabase() {
        return database;
    }
}
