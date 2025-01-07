package MODEL.handler;


import MODEL.DAO.SubjectDAO;
import MODEL.entity.Subject;
import MODEL.utils.HttpHelper;
import MODEL.utils.JsonHelper;
import com.sun.net.httpserver.*;
import java.io.*;
import java.util.*;

import org.json.JSONArray;
import org.json.JSONObject;

public class SubjectsHandler implements HttpHandler
{


    @Override
    public void handle(HttpExchange exchange) throws IOException
    {
        System.out.println("SubjectsHandler called");
        //activer CORS
        HttpHelper.enableCORS(exchange);

        //traiter les requêtes
        String requestPath=exchange.getRequestURI().getPath();
        String requestMethod = exchange.getRequestMethod();

        System.out.println("(SubjectsHandler)-> methode: "+requestMethod +"\t" +"requestPath " + requestPath);

        switch (requestMethod)
        {
            case "GET":
                handleGetSubject(exchange);
                break;
            case "POST":
                handlePostSubject(exchange);
                break;
            case "PUT":
                handlePutSubject(exchange);
                break;
            case "DELETE":
                handleDeleteSubject(exchange);
                break;
            default:
                HttpHelper.sendResponse(exchange,405,"Method not allowed");

                break;
        }


    }

    /*juste un select pour le sujet*/
    private void handleGetSubject(HttpExchange exchange) throws IOException
    {
        //recup les paramètres de la requête
        Map<String, String> queryParams = HttpHelper.parseQueryParams(exchange.getRequestURI().getQuery());

        SubjectDAO subjectDAO = new SubjectDAO();
        JSONArray subjects = new JSONArray();

        System.out.println("queryParams: "+queryParams.toString());


        //traiter la requête
        if(queryParams.containsKey("id"))
        {
            //recupérer le sujet par id
            String id = queryParams.get("id");
            //traiter la requête
            Subject subject = subjectDAO.findById(Integer.parseInt(id));
            if(subject==null)
            {
                HttpHelper.sendJsonErrorResponse(exchange, 404, "Subject not found");
                return;
            }

            subjects.put(JsonHelper.toJson(subject));
        }
        if(queryParams.containsKey("name"))
        {
            //recupérer le sujet par nom
            String name = queryParams.get("name");
            //traiter la requête
            Subject subject = subjectDAO.findByName(name);

            if(subject==null)
            {
                HttpHelper.sendJsonErrorResponse(exchange, 404, "Subject not found");
                return;
            }

            subjects.put(JsonHelper.toJson(subject));
        }
        else if (queryParams.isEmpty())
        {
            //recupérer tous les sujets
            //traiter la requête
            List<Subject> allSubjects = subjectDAO.findAll();

            subjects.put(JsonHelper.subjectsToJson(allSubjects));

        }

        HttpHelper.sendJsonResponse(exchange, 200, subjects.toString());
        System.out.println("(handleGetSubject)Aucune erreur, tout est ok, message envoyé: "+subjects.toString());
    }

    /*juste un insert pour le sujet*/
    private void handlePostSubject(HttpExchange exchange) throws IOException
    {
        //recupérer le corps de la requête
        String body = HttpHelper.readRequestBody(exchange);
        System.out.println("body: "+body);
        JSONObject json = new JSONObject(body);



        //valider le corps de la requête
        if(!json.has("name"))
        {
            HttpHelper.sendResponse(exchange, 400, "Missing name parameter");
            return;
        }

        //traiter la requête
        SubjectDAO subjectDAO = new SubjectDAO();
        Subject subject = new Subject();
        subject.setName(json.getString("name"));

        Subject res= subjectDAO.create(subject);

        if(res==null)
        {
            HttpHelper.sendJsonErrorResponse(exchange, 500, "Failed to create subject");
            return;
        }

        HttpHelper.sendResponse(exchange, 201, "Subject created successfully");
        System.out.println("(handlePostSubject)Aucune erreur, tout est ok, message envoyé: "+"Subject created successfully");

    }

    /*juste un update pour le sujet*/
    private void handlePutSubject(HttpExchange exchange) throws IOException
    {
        //recup les paramètres de la requête
        Map<String, String> queryParams = HttpHelper.parseQueryParams(exchange.getRequestURI().getQuery());

        System.out.println("queryParams: "+queryParams.toString());


        //traiter la requête
        if(queryParams.containsKey("id"))
        {
            //recupérer le sujet par id
            String id = queryParams.get("id");
            //traiter la requête
            SubjectDAO subjectDAO = new SubjectDAO();
            String body = HttpHelper.readRequestBody(exchange);
            JSONObject json = new JSONObject(body);

            System.out.println("body: "+body);

            //valider le corps de la requête
            if(!json.has("name"))
            {
                HttpHelper.sendResponse(exchange, 400, "Missing name parameter");
                return;
            }

            Subject subject = new Subject();
            subject.setId(Integer.parseInt(id));
            subject.setName(json.getString("name"));

            boolean res= subjectDAO.update(subject);

            if(!res)
            {
                HttpHelper.sendResponse(exchange, 500, "Failed to update subject");
                return;
            }

            HttpHelper.sendResponse(exchange, 200, "Subject updated successfully");
            System.out.println("(handlePutSubject)Aucune erreur, tout est ok, message envoyé: "+"Subject updated successfully");
        }
        else
        {
            HttpHelper.sendResponse(exchange, 400, "Missing id parameter");
        }

    }

    /*juste un delete pour le sujet*/
    private void handleDeleteSubject(HttpExchange exchange) throws IOException
    {
        //recup les paramètres de la requête
        Map<String, String> queryParams = HttpHelper.parseQueryParams(exchange.getRequestURI().getQuery());

        //traiter la requête
        if(queryParams.containsKey("id"))
        {
            //recupérer le sujet par id
            String id = queryParams.get("id");
            //traiter la requête
            SubjectDAO subjectDAO = new SubjectDAO();
            boolean res= subjectDAO.delete(Integer.parseInt(id));

            if(!res)
            {
                HttpHelper.sendResponse(exchange, 500, "Failed to delete subject");
                return;
            }

            HttpHelper.sendResponse(exchange, 200, "Subject deleted successfully");
            System.out.println("(handleDeleteSubject)Aucune erreur, tout est ok, message envoyé: "+"Subject deleted successfully");
        }
        else
        {
            HttpHelper.sendResponse(exchange, 400, "Missing id parameter");
        }
    }

}
