package MODEL.utils;

import com.sun.net.httpserver.HttpExchange;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HttpHelper
{

    public static void enableCORS(HttpExchange exchange)
    {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");//permets à tout le monde d'accéder à l'API
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE ,OPTIONS");//méthodes autorisées
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization");// Autorisation des headers

    }

    public static Map<String, String> parseQueryParams(String query)
    {
        Map<String, String> queryParams = new HashMap<>();
        if (query != null)
        {
            String[] params = query.split("&");
            for (String param : params)
            {
                String[] keyValue = param.split("=");
                if (keyValue.length == 2)
                {
                    queryParams.put(keyValue[0], keyValue[1]);
                }
            }
        }
        return queryParams;
    }

    public static String readRequestBody(HttpExchange exchange) throws IOException
    {
        BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));
        StringBuilder requestBody = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null)
        {
            requestBody.append(line);
        }
        reader.close();
        return requestBody.toString();
    }

    public static void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException
    {
        exchange.getResponseHeaders().set("Content-Type", "text/plain");
        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody())
        {
            os.write(bytes);
        }
    }

    public static void sendJsonResponse(HttpExchange exchange, int statusCode, String jsonResponse) throws IOException
    {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        byte[] bytes = jsonResponse.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody())
        {
            os.write(bytes);
        }
    }

    public static void sendJsonErrorResponse(HttpExchange exchange, int statusCode, String message) throws IOException
    {
        JSONObject error = new JSONObject();
        error.put("error", message);
        error.put("status", statusCode);
        sendJsonResponse(exchange, statusCode, error.toString());
    }

}
