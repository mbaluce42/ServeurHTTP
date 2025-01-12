package MODEL.utils;

import MODEL.entity.Author;
import MODEL.entity.Book;
import MODEL.entity.Subject;
import org.json.JSONObject;

import java.util.List;

public class JsonHelper
{

    public static JSONObject toJson(Subject subject)
    {
        JSONObject json = new JSONObject();
        json.put("id", subject.getId());
        json.put("name", subject.getName());
        return json;
    }

    public static JSONObject subjectsToJson(List<Subject> subjects)
    {
        JSONObject json = new JSONObject();
        for (Subject subject : subjects)
        {
            json.put(String.valueOf(subject.getId()), toJson(subject));
        }
        return json;
    }

    public static Subject toSubject(JSONObject json) {
        Subject subject = new Subject();
        if (json.has("id"))
        {
            subject.setId(json.getInt("id"));
        }
        if (json.has("name"))
        {
            subject.setName(json.getString("name"));
        }
        return subject;
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

    public static JSONObject authorsToJson(List<Author> authors)
    {
        JSONObject json = new JSONObject();
        for (Author author : authors)
        {
            json.put(String.valueOf(author.getId()), toJson(author));
        }
        return json;
    }

    public static Author toAuthor(JSONObject json)
    {
        Author author = new Author();
        if (json.has("id"))
        {
            author.setId(json.getInt("id"));
        }
        if (json.has("firstName"))
        {
            author.setFirstName(json.getString("firstName"));
        }
        if (json.has("lastName"))
        {
            author.setLastName(json.getString("lastName"));
        }
        return author;
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

    public static JSONObject booksToJson(List<Book> books)
    {
        JSONObject json = new JSONObject();
        for (Book book : books)
        {
            json.put(String.valueOf(book.getId()), toJson(book));
        }
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
