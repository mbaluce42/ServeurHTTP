package MODEL.handler;

import MODEL.utils.HttpHelper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class StaticHandler implements HttpHandler
{
    private String webRoot;
    private Map<String, String> mimeTypes;

    public StaticHandler(String webRoot) {
        this.webRoot = webRoot;
        initMimeTypes();
    }

    private void initMimeTypes() {
        mimeTypes = new HashMap<>();
        mimeTypes.put("html", "text/html");
        mimeTypes.put("css", "text/css");
        mimeTypes.put("js", "application/javascript");
        mimeTypes.put("png", "image/png");
        mimeTypes.put("jpg", "image/jpeg");
        mimeTypes.put("jpeg", "image/jpeg");
        mimeTypes.put("gif", "image/gif");
        mimeTypes.put("ico", "image/x-icon");
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String requestPath = exchange.getRequestURI().getPath();

        // Si c'est la racine, servir index.html
        if (requestPath.equals("/")) {
            requestPath = "/index.html";
        }

        Path filePath = Paths.get(webRoot, requestPath);

        // Vérifier si le fichier existe et est dans le répertoire webRoot
        if (!Files.exists(filePath) || !filePath.normalize().startsWith(Paths.get(webRoot))) {
            HttpHelper.sendResponse(exchange, 404, "File not found");
            return;
        }

        // Déterminer le type MIME
        String contentType = getMimeType(filePath.toString());
        exchange.getResponseHeaders().set("Content-Type", contentType);

        // Lire et envoyer le fichier
        byte[] fileBytes = Files.readAllBytes(filePath);
        exchange.sendResponseHeaders(200, fileBytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(fileBytes);
        }
    }

    private String getMimeType(String path) {
        int lastDot = path.lastIndexOf('.');
        if (lastDot > 0) {
            String ext = path.substring(lastDot + 1).toLowerCase();
            return mimeTypes.getOrDefault(ext, "application/octet-stream");
        }
        return "application/octet-stream";
    }
}