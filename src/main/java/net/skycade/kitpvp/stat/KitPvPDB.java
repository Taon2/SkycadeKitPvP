package net.skycade.kitpvp.stat;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.Collections;

public class KitPvPDB {

    private final MongoClient mongo;
    private final MongoDatabase db;
    private final MongoCollection<Document> memberCollection;
    private final MongoCollection<Document> kitpvpCollection;

    private final String host = "ds133260.mlab.com";
    private final int port = 33260;
    private final String databaseName = "kitpvpdb";
    private final String username = "admin";
    private final String password = "ripevo17";

    private static KitPvPDB instance;

    private KitPvPDB() {
        this.mongo = new MongoClient(new ServerAddress(host, port), Collections.singletonList(MongoCredential.createCredential(username, databaseName, password.toCharArray())));
        this.db = mongo.getDatabase(databaseName);
        this.memberCollection = db.getCollection("members");
        this.kitpvpCollection = db.getCollection("kitpvp");
    }

    public MongoClient getMongo() {
        return mongo;
    }

    public MongoDatabase getDatabase() {
        return db;
    }

    public MongoCollection<Document> getMemberCollection() {
        return memberCollection;
    }

    //Only has 1 document with setting of the gamemode
    public MongoCollection<Document> getKitpvpCollection() {
        return kitpvpCollection;
    }

    public static KitPvPDB getInstance() {
        if (instance == null)
            instance = new KitPvPDB();
        return instance;
    }

}
