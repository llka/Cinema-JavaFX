package kate.cinema.backend.dao;

import kate.cinema.backend.database.ConnectionPool;
import kate.cinema.backend.exception.ApplicationException;
import kate.cinema.entity.Film;
import kate.cinema.entity.enums.ResponseStatus;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FilmDAO {
    private static final Logger logger = LogManager.getLogger(FilmDAO.class);

    private static final String SAVE = "INSERT INTO `film` (`title`, `duration`, `tickets_left_count`) " +
            " VALUES (?, ?, ?)";
    private static final String UPDATE = "UPDATE `film` SET `title` = ?, `duration` = ?, `tickets_left_count` = ? " +
            " WHERE `film_id` = ?";
    private static final String GET_BY_ID = "SELECT `film_id`, `title`, `duration`, `tickets_left_count` " +
            " FROM `film` WHERE `film_id` = ?";
    private static final String GET_ALL = "SELECT `film_id`, `title`, `duration`, `tickets_left_count` " +
            "FROM `film`";
    private static final String DELETE = "DELETE FROM `film` WHERE `film_id`= ?";

    private static final String COLUMN_FILM_ID = "film_id";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_DURATION = "duration";
    private static final String COLUMN_TICKETS_LEFT_COUNT = "tickets_left_count";

    public Film save(@Valid Film film) throws ApplicationException {
        try (Connection connection = ConnectionPool.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SAVE, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, film.getTitle());
            preparedStatement.setInt(2, film.getDurationInMin());
            preparedStatement.setInt(3, film.getTicketsLeft());

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows == 0) {
                throw new ApplicationException("Cannot save film. " + film, ResponseStatus.NOT_FOUND);
            }

            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    film.setId(generatedKeys.getInt(1));
                } else {
                    throw new ApplicationException("Cannot save film, no ID obtained. " + film, ResponseStatus.NOT_FOUND);
                }
            }
            return film;

        } catch (SQLException e) {
            throw new ApplicationException("Cannot save film. " + film + ". " + e, ResponseStatus.BAD_REQUEST);
        }
    }

    public void update(@Valid Film film) throws ApplicationException {
        try (Connection connection = ConnectionPool.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE)) {
            preparedStatement.setString(1, film.getTitle());
            preparedStatement.setInt(2, film.getDurationInMin());
            preparedStatement.setInt(3, film.getTicketsLeft());
            preparedStatement.setInt(4, film.getId());
            preparedStatement.execute();
        } catch (SQLException e) {
            throw new ApplicationException("Cannot update film. " + film + ". " + e, ResponseStatus.BAD_REQUEST);
        }
    }


    public Film getById(@Positive int id) throws ApplicationException {
        try (Connection connection = ConnectionPool.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_BY_ID)) {
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return buildFilm(resultSet);
            } else {
                throw new ApplicationException("Film with id = " + id + " not found.", ResponseStatus.NOT_FOUND);
            }
        } catch (SQLException e) {
            throw new ApplicationException("Cannot find film with id = " + id + " in database." + e, ResponseStatus.BAD_REQUEST);
        }
    }

    public List<Film> getAll() throws ApplicationException {
        try (Connection connection = ConnectionPool.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_ALL);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            List<Film> films = new ArrayList<>();
            while (resultSet.next()) {
                films.add(buildFilm(resultSet));
            }
            return films;
        } catch (SQLException e) {
            throw new ApplicationException("Cannot get all films. " + e, ResponseStatus.BAD_REQUEST);
        }
    }

    public void deleteById(@Positive int id) throws ApplicationException {
        try (Connection connection = ConnectionPool.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE)) {
            preparedStatement.setInt(1, id);
            preparedStatement.execute();
        } catch (SQLException e) {
            throw new ApplicationException("Cannot delete film with id = " + id + " " + e, ResponseStatus.BAD_REQUEST);
        }
    }

    private Film buildFilm(ResultSet resultSet) throws ApplicationException {
        try {
            Film film = new Film();
            film.setId(resultSet.getInt(COLUMN_FILM_ID));
            film.setDurationInMin(resultSet.getInt(COLUMN_DURATION));
            film.setTicketsLeft(resultSet.getInt(COLUMN_TICKETS_LEFT_COUNT));
            film.setTitle(resultSet.getString(COLUMN_TITLE));
            return film;
        } catch (SQLException e) {
            throw new ApplicationException("Error while building film! " + e, ResponseStatus.BAD_REQUEST);
        }
    }
}
