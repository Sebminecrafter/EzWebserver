package io.github.sebminecrafter.ezWebserver;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.NonNull;

import java.util.logging.Level;
import java.util.logging.Logger;

import static org.bukkit.plugin.java.JavaPlugin.getPlugin;

public class Commands implements CommandExecutor {

    private static Logger logger;
    private static JavaPlugin plugin;

    public static void init(JavaPlugin plugin) {
        logger = plugin.getLogger();
        Commands.plugin = getPlugin(EzWebserver.class);
    }

    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label, String @NonNull [] args) {
        if (args.length == 0) {
            sender.sendMessage("Usage: /ezwebserver <reload|stop|start>");
            return true;
        }

        EzWebserver instance = (EzWebserver) plugin;

        switch (args[0].toLowerCase()) {
            case "reload" -> {
                logger.log(Level.INFO, "Reloading EzWebserver...");
                sender.sendMessage("Reloading EzWebserver...");
                instance.restartServer();
                logger.log(Level.INFO, "EzWebserver has been reloaded.");
                sender.sendMessage("EzWebserver has been reloaded.");
            }
            case "stop" -> {
                if (EzWebserver.server == null) {
                    sender.sendMessage("EzWebserver is not running.");
                    return true;
                }
                sender.sendMessage("Stopping EzWebserver...");
                EzWebserver.server.stop();
                EzWebserver.server = null;
                sender.sendMessage("EzWebserver has been stopped.");
            }
            case "start" -> {
                sender.sendMessage("Starting EzWebserver...");
                instance.restartServer();
                sender.sendMessage("EzWebserver has been started.");
            }
            default -> {
                sender.sendMessage("Unknown subcommand. Usage: /ezwebserver <reload|stop|start>");
                return true;
            }
        }
        return true;
    }
}
