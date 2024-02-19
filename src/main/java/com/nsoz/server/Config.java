package com.nsoz.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import com.nsoz.util.Log;
import com.nsoz.util.StringUtils;
import lombok.Getter;

@Getter
public class Config {
    private static final Config instance = new Config();

    public static Config getInstance() {
        return instance;
    }

    // Server
    private int serverId;
    private int port;
    private boolean showLog = true;

    // Version
    private int dataVersion = 26;
    private int itemVersion = 26;
    private int mapVersion = 26;
    private int skillVersion = 26;

    // MySql
    private String dbHost;
    private int dbPort;
    private String dbUser;
    private String dbPassword;
    private String dbName;
    private String dbDriver;
    private int dbMinConnections;
    private int dbMaxConnections;

    // MongoDB
    private String mongodbHost;
    private int mongodbPort;
    private String mongodbName;
    private String mongodbUser;
    private String mongodbPassword;

    // Game
    private double maxPercentAdd;
    private int sale;
    private boolean shinwa;
    private int shinwaFee;
    private int auctionMax;
    private int shinwaMax;
    private boolean arena;
    private int ipAddressLimit;
    private int maxQuantity;
    private String serverDir;
    private String notification;
    private int messageSizeMax;
    private String event;

    public boolean load() {
        try {
            FileInputStream fileInputStream = new FileInputStream(new File("config.properties"));
            Properties properties = new Properties();
            properties.load(new InputStreamReader(fileInputStream, StandardCharsets.UTF_8));
            properties.forEach((t, u) -> {
                Log.info(String.format("Config - %s: %s", t, u));
            });

            // Server
            serverId = Integer.parseInt(properties.getProperty("server.id"));
            port = Integer.parseInt(properties.getProperty("server.port"));

            // Version
            dataVersion = Integer.parseInt(properties.getProperty("game.data.version"));
            itemVersion = Integer.parseInt(properties.getProperty("game.item.version"));
            mapVersion = Integer.parseInt(properties.getProperty("game.map.version"));
            skillVersion = Integer.parseInt(properties.getProperty("game.skill.version"));

            // MySql
            dbHost = properties.getProperty("db.host");
            dbPort = Integer.parseInt(properties.getProperty("db.port"));
            dbUser = properties.getProperty("db.user");
            dbPassword = properties.getProperty("db.password");
            dbName = properties.getProperty("db.dbname");
            dbDriver = properties.getProperty("db.driver");
            dbMaxConnections = Integer.parseInt(properties.getProperty("db.maxconnections"));
            dbMinConnections = Integer.parseInt(properties.getProperty("db.minconnections"));

            // MongoDB
            mongodbHost = properties.getProperty("mongodb.host");
            mongodbPort = Integer.parseInt(properties.getProperty("mongodb.port"));
            mongodbName = properties.getProperty("mongodb.dbname");
            mongodbUser = properties.getProperty("mongodb.user");
            mongodbPassword = properties.getProperty("mongodb.password");

            // Game
            maxPercentAdd = Integer.parseInt(properties.getProperty("game.upgrade.percent.add"));
            sale = Integer.parseInt(properties.getProperty("game.store.discount"));
            shinwa = Boolean.parseBoolean(properties.getProperty("game.shinwa.active"));
            shinwaFee = Integer.parseInt(properties.getProperty("game.shinwa.fee"));
            auctionMax = Integer.parseInt(properties.getProperty("game.shinwa.max"));
            shinwaMax = Integer.parseInt(properties.getProperty("game.shinwa.player.max"));
            arena = Boolean.parseBoolean(properties.getProperty("game.arena.active"));
            ipAddressLimit = Integer.parseInt(properties.getProperty("game.login.limit"));
            maxQuantity = Integer.parseInt(properties.getProperty("game.quantity.display.max"));
            notification = properties.getProperty("server.notification");
            messageSizeMax = Integer.parseInt(properties.getProperty("client.data.size.max"));
            event = properties.getProperty("game.event");

        } catch (Exception e) {
            Log.error("Load config error: " + e.getMessage());
            return false;
        }

        return true;
    }

    public String getJdbcUrl() {
        return "jdbc:mysql://" + dbHost + ":" + dbPort + "/" + dbName;
    }

    public String getMongodbUrl() {
        if (!StringUtils.isNullOrEmpty(mongodbUser) && !StringUtils.isNullOrEmpty(mongodbPassword)) {
            return String.format("mongodb://%s:%s@%s:%d/%s", mongodbUser, mongodbPassword, mongodbHost, mongodbPort, mongodbName);
        }
        return String.format("mongodb://%s:%d", mongodbHost, mongodbPort);
    }
}
