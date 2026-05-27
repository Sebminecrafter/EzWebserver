package io.github.sebminecrafter.ezWebserver;

import com.sun.net.httpserver.HttpServer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Path;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class EzWebserver extends JavaPlugin {

    public static HttpServer server;
    public static Logger logger;
    public static FileConfiguration config;
    public static File dataFolder;
    public static Path dir;

    public void restartServer() {
        if (server != null) {
            server.stop(0);
        }
        InetSocketAddress addr = new InetSocketAddress(
                Objects.requireNonNull(config.getString("hostname")),
                config.getInt("port")
        );
        try {
            server = HttpServer.create(addr, 0);
            server.createContext("/", new FileRequestHandler(dir, logger));
            server.setExecutor(null);
            server.start();
        } catch (IOException e) {
            logger.severe("Failed to restart server: " + e.getMessage());
        }
    }

    @Override
    public void onEnable() {

        // Set up logger
        logger = getLogger();
        logger.log(Level.INFO, "Starting EzWebserver...");

        // Init
        Commands.init(this);
        Objects.requireNonNull(getCommand("ezwebserver")).setExecutor(new Commands());
        PlaceholderProcessor.init();

        // Save config
        this.saveDefaultConfig();

        // Variables
        config = getConfig();
        dataFolder = getDataFolder();
        dataFolder.mkdirs();
        dir = dataFolder.toPath();

        // Public folder
        if (dir.resolve("public").toFile().mkdirs()) {
            this.saveResource("public/index.html", false);
        }

        dir = dir.resolve("public");
        // Config
        config.options().copyDefaults(true);

        dir = dir.toAbsolutePath();

        restartServer();
        logger.log(Level.INFO, "EzWebserver has been started.");

    }

    @Override
    public void onDisable() {
        // Stop server
        if (server != null) {
            server.stop(0);
        }
        logger.log(Level.INFO, "EzWebserver has been stopped.");
    }
}
