package io.github.sebminecrafter.ezWebserver;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.profile.PlayerProfile;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

/**
 * Isolated so the PlaceholderAPI import is only class loaded
 * when PAPI is confirmed present. Never reference this class
 * directly, only call it through PlaceholderProcessor.
 */
class PapiHook {
    static OfflinePlayer fakePlayer() {
        return new OfflinePlayer() {
            @Override
            public boolean isOp() {
                return false;
            }

            @Override
            public void setOp(boolean value) {
            }

            @Override
            public Map<String, Object> serialize() {
                return Map.of();
            }

            @Override
            public boolean isOnline() {
                return false;
            }

            @Override
            public String getName() {
                return "";
            }

            @Override
            public UUID getUniqueId() {
                return null;
            }

            @Override
            public PlayerProfile getPlayerProfile() {
                return null;
            }

            @Override
            public boolean isBanned() {
                return false;
            }

            @Override
            public BanEntry<PlayerProfile> ban(String r, Date e, String s) {
                return null;
            }

            @Override
            public BanEntry<PlayerProfile> ban(String r, Instant e, String s) {
                return null;
            }

            @Override
            public BanEntry<PlayerProfile> ban(String r, Duration d, String s) {
                return null;
            }

            @Override
            public boolean isWhitelisted() {
                return false;
            }

            @Override
            public void setWhitelisted(boolean value) {
            }

            @Override
            public Player getPlayer() {
                return null;
            }

            @Override
            public long getFirstPlayed() {
                return 0;
            }

            @Override
            public long getLastPlayed() {
                return 0;
            }

            @Override
            public boolean hasPlayedBefore() {
                return false;
            }

            @Override
            public Location getBedSpawnLocation() {
                return null;
            }

            @Override
            public Location getRespawnLocation() {
                return null;
            }

            @Override
            public void incrementStatistic(Statistic s) throws IllegalArgumentException {
            }

            @Override
            public void decrementStatistic(Statistic s) throws IllegalArgumentException {
            }

            @Override
            public void incrementStatistic(Statistic s, int a) throws IllegalArgumentException {
            }

            @Override
            public void decrementStatistic(Statistic s, int a) throws IllegalArgumentException {
            }

            @Override
            public void setStatistic(Statistic s, int n) throws IllegalArgumentException {
            }

            @Override
            public int getStatistic(Statistic s) throws IllegalArgumentException {
                return 0;
            }

            @Override
            public void incrementStatistic(Statistic s, Material m) throws IllegalArgumentException {
            }

            @Override
            public void decrementStatistic(Statistic s, Material m) throws IllegalArgumentException {
            }

            @Override
            public int getStatistic(Statistic s, Material m) throws IllegalArgumentException {
                return 0;
            }

            @Override
            public void incrementStatistic(Statistic s, Material m, int a) throws IllegalArgumentException {
            }

            @Override
            public void decrementStatistic(Statistic s, Material m, int a) throws IllegalArgumentException {
            }

            @Override
            public void setStatistic(Statistic s, Material m, int n) throws IllegalArgumentException {
            }

            @Override
            public void incrementStatistic(Statistic s, EntityType e) throws IllegalArgumentException {
            }

            @Override
            public void decrementStatistic(Statistic s, EntityType e) throws IllegalArgumentException {
            }

            @Override
            public int getStatistic(Statistic s, EntityType e) throws IllegalArgumentException {
                return 0;
            }

            @Override
            public void incrementStatistic(Statistic s, EntityType e, int a) throws IllegalArgumentException {
            }

            @Override
            public void decrementStatistic(Statistic s, EntityType e, int a) {
            }

            @Override
            public void setStatistic(Statistic s, EntityType e, int n) {
            }

            @Override
            public Location getLastDeathLocation() {
                return null;
            }

            @Override
            public Location getLocation() {
                return null;
            }
        };
    }
    static String apply(OfflinePlayer player, String content) {
        if (player != null) {
            return PlaceholderAPI.setPlaceholders(player, content);
        } else {
            OfflinePlayer offlineP = fakePlayer();
            return PlaceholderAPI.setPlaceholders(offlineP, content);
        }
    }
}