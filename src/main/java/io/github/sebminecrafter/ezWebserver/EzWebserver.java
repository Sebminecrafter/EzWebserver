package io.github.sebminecrafter.ezWebserver;

import io.undertow.Undertow;
import io.undertow.UndertowOptions;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.nio.file.Path;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class EzWebserver extends JavaPlugin {

    public static Undertow server;
    public static Logger logger;
    public static FileConfiguration config;
    public static File dataFolder;
    public static Path dir;

    public void restartServer() {
        if (server != null) {
            server.stop();
            server = null;
        }
        try {
            server = Undertow.builder()
                    .addHttpListener(
                            config.getInt("port"),
                            Objects.requireNonNull(config.getString("hostname"))
                    )
                    .setIoThreads(2)
                    .setWorkerThreads(8)
                    .setServerOption(UndertowOptions.SHUTDOWN_TIMEOUT, 1000)
                    .setHandler(new FileRequestHandler(dir, logger))
                    .build();
            server.start();
        } catch (RuntimeException e) {
            // Undertow reports bind failures (port in use, bad address, ...) as RuntimeExceptions
            server = null;
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
        boolean _ = dataFolder.mkdirs();
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
            server.stop();
            server = null;
        }
        logger.log(Level.INFO, "EzWebserver has been stopped.");
    }
}
