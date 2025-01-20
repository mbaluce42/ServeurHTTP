package MODEL.utils;

import MODEL.entity.Author;
import MODEL.entity.Book;
import MODEL.entity.Subject;
import org.json.JSONObject;


public class JsonHelper
{

    public static JSONObject toJson(Subject subject)
    {
        JSONObject json = new JSONObject();
        json.put("id", subject.getId());
        json.put("name", subject.getName());
        return json;
    }


    //------------------------------------------------------------------------------------------------
    public static JSONObject toJson(Author author)
    {
        JSONObject json = new JSONObject();
        json.put("id", author.getId());
        json.put("firstName", author.getFirstName());
        json.put("lastName", author.getLastName());
        json.put("birthDate", author.getBirthDate());

        return json;
    }


    //------------------------------------------------------------------------------------------------

    public static JSONObject toJson(Book book)
    {
        JSONObject json = new JSONObject();
        json.put("id", book.getId());
        json.put("author", toJson(book.getAuthor()));
        json.put("subject", toJson(book.getSubject()));
        json.put("title", book.getTitle());
        json.put("isbn", book.getIsbn());
        json.put("pageCount", book.getPageCount());
        json.put("stockQuantity", book.getStockQuantity());
        json.put("price", book.getPrice());
        json.put("publishYear", book.getPublishYear());
        return json;
    }


    public static Book toBook(JSONObject json)
    {
        Book book = new Book();
        if (json.has("id"))
        {
            book.setId(json.getInt("id"));
        }
        if (json.has("authorId"))
        {
            book.getAuthor().setId(json.getInt("authorId"));
        }
        if (json.has("subjectId"))
        {
            book.getSubject().setId(json.getInt("subjectId"));
        }
        if (json.has("title"))
        {
            book.setTitle(json.getString("title"));
        }
        if (json.has("isbn"))
        {
            book.setIsbn(json.getString("isbn"));
        }
        if (json.has("pageCount"))
        {
            book.setPageCount(json.getInt("pageCount"));
        }
        if (json.has("stockQuantity"))
        {
            book.setStockQuantity(json.getInt("stockQuantity"));
        }
        if (json.has("price"))
        {
            book.setPrice((float) json.getDouble("price"));
        }
        if (json.has("publishYear"))
        {
            book.setPublishYear(json.getInt("publishYear"));
        }
        return book;
    }

    //------------------------------------------------------------------------------------------------


}
