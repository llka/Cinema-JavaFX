package kate.cinema.backend.dao;

import kate.cinema.backend.database.ConnectionPool;
import kate.cinema.backend.exception.ApplicationException;
import kate.cinema.entity.Schedule;
import kate.cinema.entity.enums.ResponseStatus;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ScheduleDAO {
    private static final Logger logger = LogManager.getLogger(ScheduleDAO.class);
    private static final String DATE_TIME_FORMAT_PATTERN = "yyyy-MM-dd HH:mm:ss";

    private static final String SAVE = "INSERT INTO `shedule` (`film_id`, `date`) " +
            " VALUES (?, ?, ?, ?)";
    private static final String UPDATE = "UPDATE `shedule` SET `film_id` = ?, `date` = ? " +
            " WHERE `shedule_id` = ?";
    private static final String GET_BY_ID = "SELECT `shedule_id`, `film_id`, `date` " +
            " FROM `shedule` WHERE `shedule_id` = ?";
    private static final String GET_ALL = "SELECT `shedule_id`, `film_id`, `date` " +
            " FROM `shedule`";
    private static final String FIND = "SELECT `shedule`.`shedule_id`, `shedule`.`film_id`, `shedule`.`date` " +
            " FROM `shedule` JOIN `film` ON `film`.`film_id` = `shedule`.`film_id` WHERE ";


    private static final String DELETE = "DELETE FROM `shedule` WHERE `shedule_id`= ?";

    private static final String COLUMN_FILM_ID = "film_id";
    private static final String COLUMN_SCHEDULE_ID = "shedule_id";
    private static final String COLUMN_DATE = "date";

    private FilmDAO filmDAO;

    public ScheduleDAO() {
        filmDAO = new FilmDAO();
    }

    public void save(@Valid Schedule schedule) throws ApplicationException {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_TIME_FORMAT_PATTERN);
        try (Connection connection = ConnectionPool.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SAVE)) {
            preparedStatement.setInt(1, schedule.getFilm().getId());

            if (schedule.getDate() != null) {
                preparedStatement.setString(2, dateFormat.format(schedule.getDate()));
            } else {
                preparedStatement.setString(2, null);
            }

            preparedStatement.execute();
        } catch (SQLException e) {
            throw new ApplicationException("Cannot save schedule. " + schedule + ". " + e, ResponseStatus.BAD_REQUEST);
        }
    }

    public void update(@Valid Schedule schedule) throws ApplicationException {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_TIME_FORMAT_PATTERN);
        try (Connection connection = ConnectionPool.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE)) {
            preparedStatement.setInt(1, schedule.getFilm().getId());

            if (schedule.getDate() != null) {
                preparedStatement.setString(2, dateFormat.format(schedule.getDate()));
            } else {
                preparedStatement.setString(2, null);
            }
            preparedStatement.setInt(3, schedule.getId());
            preparedStatement.execute();
        } catch (SQLException e) {
            throw new ApplicationException("Cannot update schedule. " + schedule + ". " + e, ResponseStatus.BAD_REQUEST);
        }
    }


    public Schedule getById(@Positive int id) throws ApplicationException {
        try (Connection connection = ConnectionPool.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_BY_ID)) {
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return buildSchedule(resultSet);
            } else {
                throw new ApplicationException("Schedule with id = " + id + " not found.", ResponseStatus.NOT_FOUND);
            }
        } catch (SQLException e) {
            throw new ApplicationException("Cannot find schedule with id = " + id + " in database." + e, ResponseStatus.BAD_REQUEST);
        }
    }

    public List<Schedule> getAll() throws ApplicationException {
        try (Connection connection = ConnectionPool.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_ALL);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            List<Schedule> scheduleList = new ArrayList<>();
            while (resultSet.next()) {
                scheduleList.add(buildSchedule(resultSet));
            }
            return scheduleList;
        } catch (SQLException e) {
            throw new ApplicationException("Cannot get all schedules. " + e, ResponseStatus.BAD_REQUEST);
        }
    }

    public List<Schedule> find(Map<String, String> filterParams) throws ApplicationException {
        StringBuilder query = new StringBuilder(FIND);
        final String ID_PARAM = "id";
        final String TITLE_PARAM = "title";
        boolean and = false;
        boolean findable = false;

        if (filterParams.get(ID_PARAM) != null) {
            query.append(" `shedule`.`film_id` = ? ");
            and = true;
            findable = true;
        }
        if (filterParams.get(TITLE_PARAM) != null) {
            if (and) {
                query.append(" AND ");
            }
            query.append(" `film`.`title` = ? ");
            and = true;
            findable = true;
        }

        if (!findable) {
            query = new StringBuilder(GET_ALL);
        }

        try (Connection connection = ConnectionPool.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query.toString())) {

            int index = 1;
            if (filterParams.get(ID_PARAM) != null) {
                try {
                    preparedStatement.setInt(index++, Integer.parseInt(filterParams.get(ID_PARAM)));
                } catch (NumberFormatException e) {
                    throw new ApplicationException("Invalid id!", ResponseStatus.BAD_REQUEST);
                }
            }
            if (filterParams.get(TITLE_PARAM) != null) {
                preparedStatement.setString(index++, filterParams.get(TITLE_PARAM));
            }

            ResultSet resultSet = preparedStatement.executeQuery();
            List<Schedule> scheduleList = new ArrayList<>();
            while (resultSet.next()) {
                scheduleList.add(buildSchedule(resultSet));
            }
            return scheduleList;
        } catch (SQLException e) {
            throw new ApplicationException("Cannot find films." + e, ResponseStatus.BAD_REQUEST);
        }
    }

    public void deleteById(@Positive int id) throws ApplicationException {
        try (Connection connection = ConnectionPool.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE)) {
            preparedStatement.setInt(1, id);
            preparedStatement.execute();
        } catch (SQLException e) {
            throw new ApplicationException("Cannot delete schedule with id = " + id + " " + e, ResponseStatus.BAD_REQUEST);
        }
    }

    private Schedule buildSchedule(ResultSet resultSet) throws ApplicationException {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_TIME_FORMAT_PATTERN);
        try {
            Schedule schedule = new Schedule();
            schedule.setId(resultSet.getInt(COLUMN_SCHEDULE_ID));
            schedule.setFilm(filmDAO.getById(resultSet.getInt(COLUMN_FILM_ID)));
            try {
                if (resultSet.getDate(COLUMN_DATE) != null) {
                    schedule.setDate(dateFormat.parse(resultSet.getString(COLUMN_DATE)));
                }
            } catch (ParseException exception) {
                logger.error(exception);
            }
            return schedule;
        } catch (SQLException e) {
            throw new ApplicationException("Error while building schedule! " + e, ResponseStatus.BAD_REQUEST);
        }
    }
}
