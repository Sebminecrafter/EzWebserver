package io.github.sebminecrafter.ezWebserver;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class PlaceholderProcessor {

    private static boolean papiEnabled = false;

    /**
     * Call this once during onEnable() after Commands.init()
     */
    public static void init() {
        papiEnabled = Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;
    }

    public static String process(String content, OfflinePlayer player) {
        // Apply built-in placeholders first
        for (Map.Entry<String, String> entry : getPlaceholders().entrySet()) {
            content = content.replace("{{" + entry.getKey() + "}}", entry.getValue());
        }

        if (papiEnabled) {
            content = PapiHook.apply(player, content);
        }

        return content;
    }

    // Overload for when there's no relevant player context (most web requests)
    public static String process(String content) {
        return process(content, null);
    }

    private static Map<String, String> getPlaceholders() {
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("player_count",   String.valueOf(Bukkit.getOnlinePlayers().size()));
        placeholders.put("max_players",    String.valueOf(Bukkit.getMaxPlayers()));
        placeholders.put("server_name",    Bukkit.getServer().getName());
        placeholders.put("server_version", Bukkit.getVersion());
        placeholders.put("motd",           Bukkit.getMotd());
        placeholders.put("player_list",    Bukkit.getOnlinePlayers().stream()
                .map(Player::getName)
                .collect(Collectors.joining(", ")));
        return placeholders;
    }
}