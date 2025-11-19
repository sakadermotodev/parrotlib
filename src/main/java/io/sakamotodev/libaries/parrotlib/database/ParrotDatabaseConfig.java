package io.sakamotodev.libaries.parrotlib.database;

public class ParrotDatabaseConfig {

    public ParrotDatabaseType type = ParrotDatabaseType.MYSQL;

    public String host = "localhost";
    public int port = 3306;
    public String database = "plugin";
    public String username = "root";
    public String password = "";

    public String sqliteFile = "database.db";

    public int poolSize = 5;
}
