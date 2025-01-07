import MODEL.handler.AuthorsHandler;
import MODEL.handler.BooksHandler;
import MODEL.handler.SubjectsHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Main_ServeurHTTP
{
    private static final int PORT = 8080;

    public static void main(String[] args)
    {
        HttpServer server = null;
        try
        {
            server = HttpServer.create(new InetSocketAddress(PORT), 0);


        // Configurer les contextes pour les différentes routes
        server.createContext("/api/subjects", new SubjectsHandler());
        server.createContext("/api/authors", new AuthorsHandler());
        server.createContext("/api/books", new BooksHandler());

        // Définir le nombre de threads dans le pool
        server.setExecutor(java.util.concurrent.Executors.newFixedThreadPool(10));

        server.start();
        System.out.println("Server started on port " + PORT);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}