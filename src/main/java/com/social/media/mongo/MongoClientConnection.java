package com.social.media.mongo;

import com.mongodb.*;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.social.media.model.entity.Message;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;

@Slf4j
public class MongoClientConnection {
    public static void connectToMongoDb(String connectionString) {
        try (MongoClient mongoClient = MongoClients.create(connectionString)) {
            MongoDatabase database = mongoClient.getDatabase("messages");
            MongoCollection<Message> messageMongoCollection = database.getCollection("messages", Message.class);

            // it`s for having all-time limited number of objects, and not added already existing objects which have differences only with an id.
            messageMongoCollection.deleteMany(new Document());
            log.info("Pinged your deployment. You successfully connected to MongoDB!");
        } catch (MongoException e) {
            log.error("Error connecting to MongoDB: {}", e.getMessage(), e);
        }
    }
}
