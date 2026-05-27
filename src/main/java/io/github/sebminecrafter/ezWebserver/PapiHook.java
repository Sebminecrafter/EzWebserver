package io.github.sebminecrafter.ezWebserver;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.OfflinePlayer;

/**
 * Isolated so the PlaceholderAPI import is only class loaded
 * when PAPI is confirmed present. Never reference this class
 * directly, only call it through PlaceholderProcessor.
 */
class PapiHook {
    static String apply(OfflinePlayer player, String content) {
        return PlaceholderAPI.setPlaceholders(player, content);
    }
}