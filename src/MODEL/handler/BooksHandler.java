package MODEL.handler;

import MODEL.DAO.BookDAO;
import MODEL.entity.Book;
import MODEL.utils.HttpHelper;
import MODEL.utils.JsonHelper;
import com.sun.net.httpserver.*;
import java.io.*;
import java.util.*;

import org.json.JSONArray;
import org.json.JSONObject;

public class BooksHandler implements HttpHandler
{

    @Override
    public void handle(HttpExchange exchange) throws IOException
    {
        System.out.println("BooksHandler called");
        // Activer CORS
        HttpHelper.enableCORS(exchange);

        // Traiter les requêtes
        String requestPath = exchange.getRequestURI().getPath();
        String requestMethod = exchange.getRequestMethod();

        System.out.println("(BooksHandler)-> methode: " + requestMethod + "\t" + "requestPath " + requestPath);

        switch (requestMethod)
        {
            case "GET":
                handleGetBook(exchange);
                break;
            case "POST":
                handlePostBook(exchange);
                break;
            case "PUT":
                handlePutBook(exchange);
                break;
            case "DELETE":
                handleDeleteBook(exchange);
                break;
            case "OPTIONS":
                handleOptions(exchange);
                break;
            default:
                HttpHelper.sendResponse(exchange, 405, "Method Not Allowed");
                break;
        }
    }

    private void handleGetBook(HttpExchange exchange) throws IOException
    {
        Map<String, String> queryParams = HttpHelper.parseQueryParams(exchange.getRequestURI().getQuery());
        BookDAO bookDAO = new BookDAO();
        JSONArray books = new JSONArray();
        List<Book> booksList = null;

        System.out.println("(handleGetBook)queryParams: "+queryParams);


        // Recherche par ID
        if (queryParams.containsKey("id"))
        {
            Book book = bookDAO.findById(Integer.parseInt(queryParams.get("id")));
            if (book == null)
            {
                HttpHelper.sendJsonErrorResponse(exchange, 404, "Book not found");
                return;
            }
            //books.put(JsonHelper.toJson(book));
            for (Book b: booksList)
            {
                books.put(JsonHelper.toJson(b));
            }
        }
        // Recherche par ISBN
        if (queryParams.containsKey("isbn"))
        {
            Book book = bookDAO.findIsbn(queryParams.get("isbn"));
            if (book == null)
            {
                HttpHelper.sendJsonErrorResponse(exchange, 404, "Book not found");
                return;
            }

            books.put(JsonHelper.toJson(book));
        }
        // Recherche par titre
        if (queryParams.containsKey("title"))
        {
            booksList = bookDAO.findByTitle(queryParams.get("title"));

            if (booksList == null)
            {
                HttpHelper.sendJsonErrorResponse(exchange, 404, "Book not found");
                return;
            }
            //books.put(JsonHelper.booksToJson(booksList));
            for(Book b: booksList)
            {
                books.put(JsonHelper.toJson(b));
            }
        }
        // Recherche par nom et prénom d'auteur
        if (queryParams.containsKey("authorLastName") && queryParams.containsKey("authorFirstName"))
        {
            booksList = bookDAO.findByAuthorLastNameFirstName(queryParams.get("authorLastName"), queryParams.get("authorFirstName"));

            if (booksList == null)
            {
                HttpHelper.sendJsonErrorResponse(exchange, 404, "Book not found");
                return;
            }

            //books.put(JsonHelper.booksToJson(booksList));
            for(Book b: booksList)
            {
                books.put(JsonHelper.toJson(b));
            }
        }
        // Recherche par ID d'auteur
        if (queryParams.containsKey("authorId"))
        {
            booksList = bookDAO.findByAuthorId(Integer.parseInt(queryParams.get("authorId")));

            if (booksList == null)
            {
                HttpHelper.sendJsonErrorResponse(exchange, 404, "Book not found");
                return;
            }

            //books.put(JsonHelper.booksToJson(booksList));
            for(Book b: booksList)
            {
                books.put(JsonHelper.toJson(b));
            }
        }
        // Recherche par nom de sujet
        if (queryParams.containsKey("subjectName"))
        {
            booksList = bookDAO.findBySubjectName(queryParams.get("subjectName"));

            if (booksList == null)
            {
                HttpHelper.sendJsonErrorResponse(exchange, 404, "Book not found");
                return;
            }

            //books.put(JsonHelper.booksToJson(booksList));
            for(Book b: booksList)
            {
                books.put(JsonHelper.toJson(b));
            }
        }
        // Recherche par ID de sujet
        if (queryParams.containsKey("subjectId"))
        {
            booksList = bookDAO.findBySubjectId(Integer.parseInt(queryParams.get("subjectId")));

            if (booksList == null)
            {
                HttpHelper.sendJsonErrorResponse(exchange, 404, "Book not found");
                return;
            }

            //books.put(JsonHelper.booksToJson(booksList));
            for(Book b: booksList)
            {
                books.put(JsonHelper.toJson(b));
            }
        }
        // Recherche par prix maximum
        if (queryParams.containsKey("maxPrice"))
        {
            booksList = bookDAO.findByPrice(Float.parseFloat(queryParams.get("maxPrice")));

            if (booksList == null)
            {
                HttpHelper.sendJsonErrorResponse(exchange, 404, "Book not found");
                return;
            }

            //books.put(JsonHelper.booksToJson(booksList));
            for(Book b: booksList)
            {
                books.put(JsonHelper.toJson(b));
            }
        }
        // Aucun critère -> tous les livres
        else if(queryParams.isEmpty())
        {
            booksList = bookDAO.findAll();

            if (booksList == null)
            {
                HttpHelper.sendJsonErrorResponse(exchange, 404, "Book not found");
                return;
            }

            //books.put(JsonHelper.booksToJson(booksList));
            for(Book b: booksList)
            {
                books.put(JsonHelper.toJson(b));
            }
        }

        HttpHelper.sendJsonResponse(exchange, 200, books.toString());
        System.out.println("(handleGetBook)Aucune erreur, tout est ok, message envoyé: "+books.toString());

    }

    private void handlePostBook(HttpExchange exchange) throws IOException
    {
        String body = HttpHelper.readRequestBody(exchange);
        JSONObject json = new JSONObject(body);

        System.out.println("(handlePostBook)body: "+body);

        // Valider les champs requis
        if (!json.has("title") || !json.has("isbn") || !json.has("authorId") || !json.has("subjectId") || !json.has("pageCount") || !json.has("stockQuantity") || !json.has("price") || !json.has("publishYear"))
        {
            HttpHelper.sendResponse(exchange, 400, "Missing required fields");
            return;
        }

        // Conversion du JSON en objet Book
        Book book = JsonHelper.toBook(json);

        // Création du livre
        BookDAO bookDAO = new BookDAO();
        Book createdBook = bookDAO.create(book);

        if (createdBook == null)
        {
            HttpHelper.sendJsonErrorResponse(exchange, 500, "Failed to create book");
            return;
        }

        HttpHelper.sendResponse(exchange, 201, "Book created successfully");
        System.out.println("(handlePostBook)Aucune erreur, tout est ok, message envoyé: "+"Book created successfully");

    }

    private void handlePutBook(HttpExchange exchange) throws IOException {
        Map<String, String> queryParams = HttpHelper.parseQueryParams(exchange.getRequestURI().getQuery());
        System.out.println("(handlePutBook)queryParams: "+queryParams);

        if (!queryParams.containsKey("id"))
        {
            HttpHelper.sendResponse(exchange, 400, "Missing id parameter");
            return;
        }

        String body = HttpHelper.readRequestBody(exchange);
        JSONObject json = new JSONObject(body);
        json.put("id", Integer.parseInt(queryParams.get("id")));
        System.out.println("(handlePutBook)body: "+body);
        System.out.println("(handlePutBook)json :"+json.toString());

        Book book = JsonHelper.toBook(json);
        System.out.println("(handlePutBook)Book avec info a update: "+book);
        BookDAO bookDAO = new BookDAO();

        boolean updated = bookDAO.update(book);
        if (!updated)
        {
            HttpHelper.sendJsonErrorResponse(exchange, 500, "Failed to update book");
            return;
        }

        HttpHelper.sendResponse(exchange, 200, "Book updated successfully");
        System.out.println("(handlePutBook)Aucune erreur, tout est ok, message envoyé: "+"Book updated successfully");

    }

    private void handleDeleteBook(HttpExchange exchange) throws IOException
    {
        //recup les paramètres de la requête
        Map<String, String> queryParams = HttpHelper.parseQueryParams(exchange.getRequestURI().getQuery());
        System.out.println("(handleDeleteBook)queryParams: "+queryParams);

        //traiter la requête
        if (queryParams.containsKey("id"))
        {
            //recupérer le sujet par id
            String id = queryParams.get("id");
            //traiter la requête
            BookDAO bookDAO = new BookDAO();
            boolean res = bookDAO.delete(Integer.parseInt(id));
            if (!res)
            {
                HttpHelper.sendResponse(exchange, 500, "Failed to delete book");
                return;
            }

            HttpHelper.sendResponse(exchange, 200, "Book deleted successfully");
            System.out.println("(handleDeleteBook)Aucune erreur, tout est ok, message envoyé: "+"Book deleted successfully");

        }
        else
        {
            HttpHelper.sendResponse(exchange, 400, "Missing id parameter");
        }
    }

    private void handleOptions(HttpExchange exchange) throws IOException
    {
        exchange.sendResponseHeaders(204, -1);  // Réponse sans contenu (No Content)
    }
}