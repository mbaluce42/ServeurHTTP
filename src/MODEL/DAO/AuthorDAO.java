package MODEL.DAO;


import MODEL.entity.Author;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AuthorDAO
{
    private Connection connection;
    private static final Logger LOGGER = Logger.getLogger(AuthorDAO.class.getName());

    public AuthorDAO() {
        connection = ConnectDB.getInstance().getConnection();
    }

    public Author create(Author author)
    {
        //verifier si l'auteur existe deja
        Author authorExist = findByNomPrenom(author.getLastName(), author.getFirstName());
        if (authorExist != null)
        {
            LOGGER.log(Level.INFO, "Auteur existe deja,pas besoin de le creer");
            return authorExist;

        }

        //met la 1ere lettre du prenom et du nom en majuscule
        author.setLastName(author.getLastName().substring(0, 1).toUpperCase() + author.getLastName().substring(1));
        author.setFirstName(author.getFirstName().substring(0, 1).toUpperCase() + author.getFirstName().substring(1));

        String sql = "INSERT INTO authors (last_name, first_name, birth_date) VALUES (?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, author.getLastName());
            stmt.setString(2, author.getFirstName());
            stmt.setString(3, author.getBirthDate());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Erreur lors de la creation de l'auteur, aucune ligne affectee.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    author.setId(generatedKeys.getInt(1));
                    return author;
                } else {
                    throw new SQLException("Erreur lors de la creation de l'auteur, aucun ID recupere.");
                }
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la creation de l'auteur", ex);
            return null;
        }
    }

    public Author findByNomPrenom(String lastName, String firstName)
    {
        String sql = "SELECT * FROM authors WHERE last_name = ? AND first_name = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, lastName);
            stmt.setString(2, firstName);

            try (ResultSet rs = stmt.executeQuery())
            {
                if (rs.next()) {
                    Author author = new Author();
                    author.setId(rs.getInt("id"));
                    author.setLastName(rs.getString("last_name"));
                    author.setFirstName(rs.getString("first_name"));
                    author.setBirthDate(rs.getString("birth_date"));
                    return author;
                }
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la recherche de l'auteur par nom et prenom", ex);
        }

        return null;
    }

    public List<Author> findAll()
    {
        List<Author> authors = new ArrayList<>();
        String sql = "SELECT * FROM authors";

        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql))
        {
            while (rs.next())
            {
                Author author = new Author();
                author.setId(rs.getInt("id"));
                author.setLastName(rs.getString("last_name"));
                author.setFirstName(rs.getString("first_name"));
                author.setBirthDate(rs.getString("birth_date"));
                authors.add(author);
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la recherche de tous les auteurs", ex);
        }

        return authors;
    }

    public Author findById(int id)
    {
        String sql = "SELECT * FROM authors WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql))
        {
            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery())
            {
                if (rs.next())
                {
                    Author author = new Author();
                    author.setId(rs.getInt("id"));
                    author.setLastName(rs.getString("last_name"));
                    author.setFirstName(rs.getString("first_name"));
                    author.setBirthDate(rs.getString("birth_date"));
                    return author;
                }
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la recherche de l'auteur par id", ex);
        }

        return null;
    }

    /*public boolean update(Author author)
    {
        //verifier si l'auteur existe
        Author authorExist = findById(author.getId());
        if (authorExist == null)
        {
            LOGGER.log(Level.INFO, "Erreur lors de la MAJ de l'auteur, l'auteur n'existe pas(id="+author.getId()+")");
            return false;
        }
        String sql = "UPDATE authors SET last_name = ?, first_name = ?, birth_date = ? WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql))
        {
            stmt.setString(1, author.getLastName());
            stmt.setString(2, author.getFirstName());
            stmt.setString(3, author.getBirthDate());
            stmt.setInt(4, author.getId());

            int affectedRows = stmt.executeUpdate();

            return affectedRows > 0;
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la MAJ de l'auteur", ex);
            return false;
        }
    }*/

    public boolean update(Author author)
    {
        // vérifier si l'auteur existe
        Author authorExist = findById(author.getId());
        if (authorExist == null)
        {
            LOGGER.log(Level.INFO, "Erreur lors de la MAJ de l'auteur, l'auteur n'existe pas(id=" + author.getId() + ")");
            return false;
        }

        // Construire la requête SQL dynamiquement
        StringBuilder sql = new StringBuilder("UPDATE authors SET ");
        List<Object> parameters = new ArrayList<>();
        boolean hasUpdates = false;

        // Vérifier chaque champ et l'ajouter à la requête s'il a été modifié
        if (author.getLastName() != null && !author.getLastName().equals(authorExist.getLastName()))
        {
            sql.append("last_name = ?");
            //met la 1ere lettre du nom
            author.setLastName(author.getLastName().substring(0, 1).toUpperCase() + author.getLastName().substring(1));

            parameters.add(author.getLastName());
            hasUpdates = true;
        }

        if (author.getFirstName() != null && !author.getFirstName().equals(authorExist.getFirstName()))
        {
            if (hasUpdates) sql.append(", ");
            sql.append("first_name = ?");
            //met la 1ere lettre du prenom
            author.setFirstName(author.getFirstName().substring(0, 1).toUpperCase() + author.getFirstName().substring(1));

            parameters.add(author.getFirstName());
            hasUpdates = true;
        }

        if (author.getBirthDate() != null && !author.getBirthDate().equals(authorExist.getBirthDate())) {
            if (hasUpdates) sql.append(", ");
            sql.append("birth_date = ?");
            parameters.add(author.getBirthDate());
            hasUpdates = true;
        }

        // Si aucun champ n'a été modifié, retourner true (pas besoin de mise à jour)
        if (!hasUpdates)
        {
            LOGGER.log(Level.INFO, "Aucune modification nécessaire pour l'auteur (id=" + author.getId() + ")");
            return true;
        }

        // Ajouter la clause WHERE
        sql.append(" WHERE id = ?");
        parameters.add(author.getId());

        try (PreparedStatement stmt = connection.prepareStatement(sql.toString()))
        {
            // Définir les paramètres
            for (int i = 0; i < parameters.size(); i++)
            {
                Object param = parameters.get(i);
                if (param instanceof String)
                {
                    stmt.setString(i + 1, (String) param);
                }
                else if (param instanceof Integer)
                {
                    stmt.setInt(i + 1, (Integer) param);
                }
            }

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;

        }
        catch (SQLException ex)
        {
            LOGGER.log(Level.SEVERE, "Erreur lors de la MAJ de l'auteur", ex);
            return false;
        }
    }

    public boolean delete(int id)
    {
        //verifier si l'auteur existe
        Author authorExist = findById(id);
        if (authorExist == null)
        {
            LOGGER.log(Level.INFO, "Erreur lors de la suppression de l'auteur, l'auteur n'existe pas");
            return false;
        }
        String sql = "DELETE FROM authors WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql))
        {
            stmt.setInt(1, id);

            int affectedRows = stmt.executeUpdate();

            return affectedRows > 0;
        }
        catch (SQLException ex)
        {
            LOGGER.log(Level.SEVERE, "Erreur lors de la suppression de l'auteur", ex);
            return false;
        }
    }

    private Author mapResultSetToAuthor(ResultSet rs) throws SQLException
    {
        Author author = new Author();
        author.setId(rs.getInt("id"));
        author.setLastName(rs.getString("lastName"));
        author.setFirstName(rs.getString("firstName"));
        author.setBirthDate(rs.getString("birthDate"));
        return author;
    }


}
