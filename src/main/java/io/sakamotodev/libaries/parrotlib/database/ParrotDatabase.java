package io.sakamotodev.libaries.parrotlib.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.CompletableFuture;

public class ParrotDatabase {

    private final Plugin plugin;
    private final ParrotDatabaseConfig config;
    private HikariDataSource source;

    public ParrotDatabase(Plugin plugin, ParrotDatabaseConfig config) {
        this.plugin = plugin;
        this.config = config;
        setup();
    }

    private void setup() {
        HikariConfig hikari = new HikariConfig();

        switch (config.type) {
            case MYSQL -> {
                hikari.setJdbcUrl("jdbc:mysql://" + config.host + ":" + config.port + "/" + config.database + "?useSSL=false");
                hikari.setUsername(config.username);
                hikari.setPassword(config.password);
            }
            case SQLITE -> {
                File file = new File(plugin.getDataFolder(), config.sqliteFile);
                hikari.setJdbcUrl("jdbc:sqlite:" + file.getAbsolutePath());
            }
        }

        hikari.setMaximumPoolSize(config.poolSize);
        hikari.setConnectionTimeout(8000);
        hikari.setIdleTimeout(60000);
        hikari.setMaxLifetime(600000);

        this.source = new HikariDataSource(hikari);
    }

    public Connection connection() {
        try {
            return source.getConnection();
        } catch (Exception e) {
            throw new RuntimeException("Could not get database connection", e);
        }
    }

    public void close() {
        if (source != null && !source.isClosed()) source.close();
    }

    public void update(String sql, Object... params) {
        try (Connection con = connection();
             PreparedStatement st = prepare(con, sql, params)) {
            st.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public <T> CompletableFuture<T> queryAsync(String sql, ResultExtractor<T> extractor, Object... params) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection con = connection();
                 PreparedStatement st = prepare(con, sql, params);
                 ResultSet rs = st.executeQuery()) {
                return extractor.extract(rs);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }, ParrotExecutors.db());
    }

    private PreparedStatement prepare(Connection con, String sql, Object... params) throws Exception {
        PreparedStatement st = con.prepareStatement(sql);
        int i = 1;
        for (Object obj : params) st.setObject(i++, obj);
        return st;
    }
}
