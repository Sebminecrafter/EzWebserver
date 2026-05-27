package io.github.sebminecrafter.ezWebserver;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.*;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileRequestHandler implements HttpHandler {

    private static final Set<String> TEXT_EXTENSIONS = Set.of(
            ".html", ".htm", ".txt", ".css", ".js", ".json", ".xml"
    );

    // Minecraft usernames: 3-16 chars, only letters, digits, underscores
    private static final java.util.regex.Pattern VALID_USERNAME =
            java.util.regex.Pattern.compile("^[a-zA-Z0-9_]{3,16}$");

    private final Path rootDir;
    private final Logger logger;

    public FileRequestHandler(Path rootDir, Logger logger) {
        this.rootDir = rootDir;
        this.logger = logger;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String uriPath = exchange.getRequestURI().getPath();

        if (uriPath.equals("/")) {
            uriPath = "/index.html";
        }

        Path filePath = rootDir.resolve(uriPath.substring(1)).normalize();
        if (!filePath.startsWith(rootDir)) {
            sendError(exchange, 403, "Forbidden");
            return;
        }

        if (!Files.exists(filePath) || Files.isDirectory(filePath)) {
            sendError(exchange, 404, "Not Found");
            return;
        }

        byte[] responseBytes;
        String extension = getExtension(filePath.getFileName().toString());

        if (TEXT_EXTENSIONS.contains(extension)) {
            // Resolve player from query param if provided
            OfflinePlayer player = resolvePlayer(exchange.getRequestURI().getQuery());

            String content = Files.readString(filePath);
            content = PlaceholderProcessor.process(content, player);
            responseBytes = content.getBytes();
        } else {
            responseBytes = Files.readAllBytes(filePath);
        }

        String contentType = getContentType(extension);
        exchange.getResponseHeaders().set("Content-Type", contentType);
        exchange.sendResponseHeaders(200, responseBytes.length);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBytes);
        }

        logger.log(Level.INFO, exchange.getRemoteAddress() + " GET " + uriPath + " 200");
    }

    /**
     * Parses the query string for a "player=" parameter, sanitizes it,
     * and returns the matching OfflinePlayer — or null if absent/invalid.
     */
    private OfflinePlayer resolvePlayer(String query) {
        if (query == null || query.isBlank()) return null;

        for (String param : query.split("&")) {
            String[] kv = param.split("=", 2);
            if (kv.length == 2 && kv[0].equalsIgnoreCase("player")) {
                String username = kv[1].trim();

                // Reject anything that isn't a valid Minecraft username
                if (!VALID_USERNAME.matcher(username).matches()) {
                    logger.log(Level.WARNING, "Rejected invalid player param: \"" + username + "\"");
                    return null;
                }

                // Prefer an online player (has full profile loaded)
                // Falls back to OfflinePlayer for PAPI placeholders that support it
                var online = Bukkit.getPlayerExact(username);
                if (online != null) return online;

                // getOfflinePlayer() never returns null but may have no data
                // if the player has never joined — PAPI handles that gracefully
                OfflinePlayer offline = Bukkit.getOfflinePlayer(username);
                if (!offline.hasPlayedBefore()) {
                    logger.log(Level.WARNING, "Player param \"" + username + "\" has never joined.");
                    return null;
                }

                return offline;
            }
        }

        return null;
    }

    private void sendError(HttpExchange exchange, int code, String message) throws IOException {
        byte[] bytes = message.getBytes();
        exchange.sendResponseHeaders(code, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private String getExtension(String filename) {
        int dot = filename.lastIndexOf('.');
        return dot == -1 ? "" : filename.substring(dot).toLowerCase();
    }

    private String getContentType(String extension) {
        return switch (extension) {
            case ".html", ".htm" -> "text/html; charset=UTF-8";
            case ".css"          -> "text/css; charset=UTF-8";
            case ".js"           -> "application/javascript; charset=UTF-8";
            case ".json"         -> "application/json; charset=UTF-8";
            case ".png"          -> "image/png";
            case ".jpg", ".jpeg" -> "image/jpeg";
            case ".gif"          -> "image/gif";
            case ".ico"          -> "image/x-icon";
            default              -> "application/octet-stream";
        };
    }
}