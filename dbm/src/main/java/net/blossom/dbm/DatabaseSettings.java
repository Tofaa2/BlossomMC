package net.blossom.dbm;

public record DatabaseSettings(
        String mongoUri,
        String databaseName
) {

    public static final String DEFAULT_MONGO_URI = "mongodb://localhost:27017";

}
