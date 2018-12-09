package kate.cinema.backend.dao;

import kate.cinema.backend.database.ConnectionPool;
import kate.cinema.backend.exception.ApplicationException;
import kate.cinema.entity.Contact;
import kate.cinema.entity.enums.ResponseStatus;
import kate.cinema.entity.enums.RoleEnum;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ContactDAO {
    private static final Logger logger = LogManager.getLogger(ContactDAO.class);

    private static final String LOGIN = "SELECT `contact_id`, `first_name`, `last_name`, `email`, `password`, `role`" +
            " FROM `contact` WHERE `email` = ? AND `password` = ?";
    private static final String SAVE = "INSERT INTO `contact`(`first_name`, `last_name`, `email`, `password`, `role`)" +
            " VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE = "UPDATE `contact` SET `first_name` = ?, `last_name` = ?, `email` = ?, " +
            " `password` = ?, `role` = ? WHERE `contact_id` = ?";
    private static final String GET_BY_EMAIL = "SELECT `contact_id`, `first_name`, `last_name`, `email`, `password`, `role`" +
            " FROM `contact` WHERE `email` = ?";
    private static final String GET_BY_ID = "SELECT `contact_id`, `first_name`, `last_name`, `email`, `password`, `role`" +
            " FROM `contact` WHERE `contact_id` = ?";
    private static final String GET_ALL = "SELECT `contact_id`, `first_name`, `last_name`, `email`, `password`, `role` " +
            "FROM `contact`";
    private static final String DELETE = "DELETE FROM `contact` WHERE `contact_id`= ?";

    private static final String COLUMN_CONTACT_ID = "contact_id";
    private static final String COLUMN_FIRST_NAME = "first_name";
    private static final String COLUMN_LAST_NAME = "last_name";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_ROLE = "role";

    public boolean login(@NotBlank String email, @NotBlank String password) throws ApplicationException {
        try (Connection connection = ConnectionPool.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(LOGIN)) {
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, password);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            throw new ApplicationException("Error while logging in in database." + e, ResponseStatus.BAD_REQUEST);
        }
    }

    public void save(@Valid Contact contact) throws ApplicationException {
        try (Connection connection = ConnectionPool.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SAVE)) {
            preparedStatement.setString(1, contact.getFirstName());
            preparedStatement.setString(2, contact.getLastName());
            preparedStatement.setString(3, contact.getEmail());
            preparedStatement.setString(4, contact.getPassword());
            preparedStatement.setString(5, contact.getRole().toString());
            preparedStatement.execute();
        } catch (SQLException e) {
            throw new ApplicationException("Error while saving new account in database." + e, ResponseStatus.BAD_REQUEST);
        }
    }

    public void update(@Valid Contact contact) throws ApplicationException {
        try (Connection connection = ConnectionPool.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE)) {
            preparedStatement.setString(1, contact.getFirstName());
            preparedStatement.setString(2, contact.getLastName());
            preparedStatement.setString(3, contact.getEmail());
            preparedStatement.setString(4, contact.getPassword());
            preparedStatement.setString(5, contact.getRole().toString());
            preparedStatement.setInt(6, contact.getId());
            preparedStatement.execute();
        } catch (SQLException e) {
            throw new ApplicationException("Error while updating contact with id = " + contact.getId() + " in database." + e, ResponseStatus.BAD_REQUEST);
        }
    }

    public Contact getByEmail(@NotBlank String email) throws ApplicationException {
        try (Connection connection = ConnectionPool.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_BY_EMAIL)) {
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return buildContact(resultSet);
            } else {
                throw new ApplicationException("No account with such email " + email + " found in database.", ResponseStatus.NOT_FOUND);
            }
        } catch (SQLException e) {
            throw new ApplicationException("Error while finding contact " + email + " in database." + e, ResponseStatus.BAD_REQUEST);
        }
    }

    public Contact getById(@Positive int id) throws ApplicationException {
        try (Connection connection = ConnectionPool.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_BY_ID)) {
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return buildContact(resultSet);
            } else {
                throw new ApplicationException("No account with such id " + id + " found in database.", ResponseStatus.NOT_FOUND);
            }
        } catch (SQLException e) {
            throw new ApplicationException("Error while finding contact " + id + " in database." + e, ResponseStatus.BAD_REQUEST);
        }
    }

    public List<Contact> getAll() throws ApplicationException {
        try (Connection connection = ConnectionPool.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_ALL);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            List<Contact> contacts = new ArrayList<>();
            while (resultSet.next()) {
                contacts.add(buildContact(resultSet));
            }
            return contacts;
        } catch (SQLException e) {
            throw new ApplicationException("Error while loading all accounts from database." + e, ResponseStatus.BAD_REQUEST);
        }
    }

    public void deleteById(@Positive int contactId) throws ApplicationException {
        try (Connection connection = ConnectionPool.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE)) {
            preparedStatement.setInt(1, contactId);
            preparedStatement.execute();
        } catch (SQLException e) {
            throw new ApplicationException("Error while deleting contact  " + contactId + " " + e, ResponseStatus.BAD_REQUEST);
        }
    }


    private Contact buildContact(ResultSet resultSet) throws ApplicationException {
        try {
            Contact contact = new Contact();
            contact.setId(resultSet.getInt(COLUMN_CONTACT_ID));
            contact.setFirstName(resultSet.getString(COLUMN_FIRST_NAME));
            contact.setLastName(resultSet.getString(COLUMN_LAST_NAME));
            contact.setEmail(resultSet.getString(COLUMN_EMAIL));
            contact.setPassword(resultSet.getString(COLUMN_PASSWORD));
            contact.setRole(RoleEnum.valueOf(resultSet.getString(COLUMN_ROLE)));
            return contact;
        } catch (SQLException e) {
            throw new ApplicationException("Error while building contact!" + e, ResponseStatus.BAD_REQUEST);
        }
    }


}
