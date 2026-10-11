package io.github.sebminecrafter.ezWebserver;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
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
    public void handleRequest(HttpServerExchange exchange) throws IOException {
        // Make sure it doesnt block
        if (exchange.isInIoThread()) {
            exchange.dispatch(this);
            return;
        }

        String uriPath = exchange.getRequestPath();

        if (uriPath.equals("/")) {
            uriPath = "/index.html";
        }

        Path filePath;
        try {
            filePath = rootDir.resolve(uriPath.substring(1)).normalize();
        } catch (InvalidPathException e) {
            sendError(exchange, 400, "Bad Request");
            return;
        }
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
            OfflinePlayer player = resolvePlayer(exchange.getQueryString());

            String content = Files.readString(filePath);
            content = PlaceholderProcessor.process(content, player);
            responseBytes = content.getBytes();
        } else {
            responseBytes = Files.readAllBytes(filePath);
        }

        String contentType = getContentType(extension);
        exchange.setStatusCode(200);
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, contentType);
        exchange.setResponseContentLength(responseBytes.length);
        exchange.getResponseSender().send(ByteBuffer.wrap(responseBytes));

        logger.log(Level.INFO, exchange.getSourceAddress() + " GET " + uriPath + " 200");
    }

    /**
     * Parses the query string for a "player=" parameter, sanitizes it,
     * and returns the OfflinePlayer or null if missing or wrong
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
                Player online = Bukkit.getPlayerExact(username);
                if (online != null) return online;

                OfflinePlayer offline = Bukkit.getOfflinePlayer(username);

                return offline.hasPlayedBefore() ? offline : null;
            }
        }

        return null;
    }

    private void sendError(HttpServerExchange exchange, int code, String message) {
        exchange.setStatusCode(code);
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain; charset=UTF-8");
        exchange.getResponseSender().send(message);
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