package MODEL.handler;

import MODEL.DAO.AuthorDAO;
import MODEL.entity.Author;
import MODEL.utils.HttpHelper;
import MODEL.utils.JsonHelper;
import com.sun.net.httpserver.*;
import java.io.*;
import java.util.*;

import org.json.JSONArray;
import org.json.JSONObject;

public class AuthorsHandler implements HttpHandler
{
    @Override
    public void handle(HttpExchange exchange) throws IOException
    {
        System.out.println("AuthorsHandler called");
        //activer CORS
        HttpHelper.enableCORS(exchange);

        //traiter les requêtes
        String requestPath=exchange.getRequestURI().getPath();
        String requestMethod = exchange.getRequestMethod();

        System.out.println("(AuthorsHandler)-> methode: "+requestMethod +"\t" +"requestPath " + requestPath);

        switch (requestMethod)
        {
            case "GET":
                handleGetAuthor(exchange);
                break;
            case "POST":
                handlePostAuthor(exchange);
                break;
            case "PUT":
                handlePutAuthor(exchange);
                break;
            case "DELETE":
                handleDeleteAuthor(exchange);
                break;
            case "OPTIONS":
                handleOptions(exchange);
                break;
            default:
                //traiter les autres requêtes
                HttpHelper.sendResponse(exchange, 405, "Method Not Allowed");
                break;
        }
    }

    /*juste un select pour le author*/
    private void handleGetAuthor(HttpExchange exchange) throws IOException
    {
        //recup les paramètres de la requête
        Map<String, String> queryParams = HttpHelper.parseQueryParams(exchange.getRequestURI().getQuery());

        AuthorDAO authorDAO = new AuthorDAO();
        JSONArray authors = new JSONArray();

        System.out.println("queryParams: "+queryParams);

        //traiter la requête
        if(queryParams.containsKey("id"))
        {
            //recupérer le sujet par id
            String id = queryParams.get("id");
            //traiter la requête
            Author author = authorDAO.findById(Integer.parseInt(id));
            if(author==null)
            {
                HttpHelper.sendJsonErrorResponse(exchange, 404, "Author not found");
                return;
            }

            authors.put(JsonHelper.toJson(author));
        }
        else if(queryParams.containsKey("lastName") && queryParams.containsKey("firstName"))
        {
            //recupérer le sujet par nom
            String lastName = queryParams.get("lastName");
            String firstName = queryParams.get("firstName");
            //traiter la requête
            Author author = authorDAO.findByNomPrenom(lastName, firstName);
            if(author==null)
            {
                HttpHelper.sendJsonErrorResponse(exchange, 404, "Author not found");
                return;
            }

            authors.put(JsonHelper.toJson(author));
        }
        else
        {
            //recupérer tous les auteurs
            //traiter la requête
            List<Author> allAuthors = authorDAO.findAll();

            for (Author author : allAuthors)
            {
                authors.put(JsonHelper.toJson(author));
            }
        }

        HttpHelper.sendJsonResponse(exchange, 200, authors.toString());
        System.out.println("(handleGetAuthor)Aucune erreur, tout est ok, message envoyé: "+authors.toString());

    }

    /*juste un insert pour le author*/
    private void handlePostAuthor(HttpExchange exchange) throws IOException
    {
        //recupérer le corps de la requête
        String body = HttpHelper.readRequestBody(exchange);
        System.out.println("body: "+body);
        JSONObject json = new JSONObject(body);

        //valider le corps de la requête
        if(!json.has("lastName") || !json.has("firstName") || !json.has("birthDate"))
        {
            HttpHelper.sendResponse(exchange, 400, "Missing parameters");
            return;
        }

        //traiter la requête
        AuthorDAO authorDAO = new AuthorDAO();
        Author author = new Author();
        author.setFirstName(json.getString("firstName"));
        author.setLastName(json.getString("lastName"));
        author.setBirthDate(json.getString("birthDate"));

        Author res= authorDAO.create(author);
        if(res==null)
        {
            HttpHelper.sendJsonErrorResponse(exchange, 500, "Failed to create author");
            return;
        }

        HttpHelper.sendResponse(exchange, 201, "Author created successfully");
        System.out.println("(handlePostAuthor)Aucune erreur, tout est ok, message envoyé: "+"Author created successfully");
    }

    /*juste un update pour le author*/
    private void handlePutAuthor(HttpExchange exchange) throws IOException
    {
        //recup les paramètres de la requête
        Map<String, String> queryParams = HttpHelper.parseQueryParams(exchange.getRequestURI().getQuery());
        System.out.println("queryParams: "+queryParams);

        //traiter la requête
        if(queryParams.containsKey("id"))
        {
            //recupérer le sujet par id
            String id = queryParams.get("id");
            //traiter la requête
            AuthorDAO authorDAO = new AuthorDAO();
            String body = HttpHelper.readRequestBody(exchange);
            JSONObject json = new JSONObject(body);
            System.out.println("body: "+body);

            Author author= new Author();
            author.setId(Integer.parseInt(id));

            // Mettre à jour uniquement les champs présents dans le JSON
            if (json.has("firstName"))
            {
                author.setFirstName(json.getString("firstName"));
            }
            if (json.has("lastName"))
            {
                author.setLastName(json.getString("lastName"));
            }
            if (json.has("birthDate"))
            {
                author.setBirthDate(json.getString("birthDate"));
            }

            System.out.println("author a modif: "+author);

            boolean res = authorDAO.update(author);
            System.out.println("res: "+res);
            if (!res)
            {
                HttpHelper.sendResponse(exchange, 500, "Failed to update author");
                return;
            }

            HttpHelper.sendResponse(exchange, 200, "Author updated successfully");
            System.out.println("(handlePutAuthor)Aucune erreur, tout est ok, message envoyé: "+"Author updated successfully");
        }
        else
        {
            HttpHelper.sendResponse(exchange, 400, "Missing id parameter");
        }
    }

    /*juste un delete pour le author*/
    private void handleDeleteAuthor(HttpExchange exchange) throws IOException
    {
        //recup les paramètres de la requête
        Map<String, String> queryParams = HttpHelper.parseQueryParams(exchange.getRequestURI().getQuery());
        System.out.println("queryParams: "+queryParams);

        //traiter la requête
        if(queryParams.containsKey("id"))
        {
            //recupérer le sujet par id
            String id = queryParams.get("id");
            //traiter la requête
            AuthorDAO authorDAO = new AuthorDAO();
            boolean res= authorDAO.delete(Integer.parseInt(id));
            if(!res)
            {
                HttpHelper.sendResponse(exchange, 500, "Failed to delete author");
                return;
            }

            HttpHelper.sendResponse(exchange, 200, "Author deleted successfully");
            System.out.println("(handleDeleteAuthor)Aucune erreur, tout est ok, message envoyé: "+"Author deleted successfully");
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
