package kate.cinema.backend.dao;

import kate.cinema.backend.database.ConnectionPool;
import kate.cinema.backend.exception.ApplicationException;
import kate.cinema.entity.Ticket;
import kate.cinema.entity.enums.ResponseStatus;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TicketDAO {
    private static final Logger logger = LogManager.getLogger(TicketDAO.class);


    private static final String SAVE = "INSERT INTO `ticket`(`place_number`, `cost`, `shedule_id`)" +
            " VALUES (?, ?, ?)";
    private static final String UPDATE = "UPDATE `ticket` SET `place_number` = ?, `cost` = ?, `shedule_id` = ? " +
            " WHERE `ticket_id` = ?";
    private static final String GET_BY_ID = "SELECT `ticket_id`, `place_number`, `cost`, `shedule_id` " +
            " FROM `ticket` WHERE `ticket_id` = ?";
    private static final String GET_ALL = "SELECT `ticket_id`, `place_number`, `cost`, `shedule_id` " +
            "FROM `ticket`";
    private static final String GET_TICKETS_FOR_CONTACT = "SELECT `ticket_id`, `place_number`, `cost`, `shedule_id` " +
            " FROM `ticket` " +
            " JOIN `contact_has_ticket` ON `ticket_ticket_id` = `ticket_id` " +
            " WHERE contact_contact_id = ?";

    private static final String DELETE = "DELETE FROM `ticket` WHERE `ticket_id`= ?";

    private static final String COLUMN_TICKET_ID = "ticket_id";
    private static final String COLUMN_PLACE_NUMBER = "place_number";
    private static final String COLUMN_COST = "cost";
    private static final String COLUMN_SCHEDULE_ID = "shedule_id";

    private ScheduleDAO scheduleDAO;

    public TicketDAO() {
        scheduleDAO = new ScheduleDAO();
    }

    public void save(@Valid Ticket ticket) throws ApplicationException {
        try (Connection connection = ConnectionPool.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SAVE)) {
            preparedStatement.setInt(1, ticket.getPlaceNumber());
            preparedStatement.setBigDecimal(2, ticket.getCost());
            preparedStatement.setInt(3, ticket.getSchedule().getId());
            preparedStatement.execute();
        } catch (SQLException e) {
            throw new ApplicationException("Cannot save ticket. " + ticket + " " + e, ResponseStatus.BAD_REQUEST);
        }
    }

    public void update(@Valid Ticket ticket) throws ApplicationException {
        try (Connection connection = ConnectionPool.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE)) {
            preparedStatement.setInt(1, ticket.getPlaceNumber());
            preparedStatement.setBigDecimal(2, ticket.getCost());
            preparedStatement.setInt(3, ticket.getSchedule().getId());
            preparedStatement.setInt(4, ticket.getId());
            preparedStatement.execute();
        } catch (SQLException e) {
            throw new ApplicationException("Cannot update ticket. " + ticket + " " + e, ResponseStatus.BAD_REQUEST);
        }
    }


    public Ticket getById(@Positive int id) throws ApplicationException {
        try (Connection connection = ConnectionPool.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_BY_ID)) {
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return buildTicket(resultSet);
            } else {
                throw new ApplicationException("Ticket with id =  " + id + "not found.", ResponseStatus.NOT_FOUND);
            }
        } catch (SQLException e) {
            throw new ApplicationException("Cannot find ticket with id = " + id + ". " + e, ResponseStatus.BAD_REQUEST);
        }
    }

    public List<Ticket> getAll() throws ApplicationException {
        try (Connection connection = ConnectionPool.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_ALL);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            List<Ticket> tickets = new ArrayList<>();
            while (resultSet.next()) {
                tickets.add(buildTicket(resultSet));
            }
            return tickets;
        } catch (SQLException e) {
            throw new ApplicationException("Error while loading all tickets! " + e, ResponseStatus.BAD_REQUEST);
        }
    }

    public List<Ticket> getTicketsForContact(int contactId) throws ApplicationException {
        try (Connection connection = ConnectionPool.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_TICKETS_FOR_CONTACT)) {
            preparedStatement.setInt(1, contactId);
            ResultSet resultSet = preparedStatement.executeQuery();
            List<Ticket> tickets = new ArrayList<>();
            while (resultSet.next()) {
                tickets.add(buildTicket(resultSet));
            }
            return tickets;
        } catch (SQLException e) {
            throw new ApplicationException("Error while loading tickets for contact with id = " + contactId + ". " + e, ResponseStatus.BAD_REQUEST);
        }
    }

    public void deleteById(@Positive int id) throws ApplicationException {
        try (Connection connection = ConnectionPool.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE)) {
            preparedStatement.setInt(1, id);
            preparedStatement.execute();
        } catch (SQLException e) {
            throw new ApplicationException("Cannot delete ticket  " + id + " " + e, ResponseStatus.BAD_REQUEST);
        }
    }

    private Ticket buildTicket(ResultSet resultSet) throws ApplicationException {
        try {
            Ticket ticket = new Ticket();
            ticket.setId(resultSet.getInt(COLUMN_TICKET_ID));
            ticket.setCost(resultSet.getBigDecimal(COLUMN_COST));
            ticket.setPlaceNumber(resultSet.getInt(COLUMN_PLACE_NUMBER));
            ticket.setSchedule(scheduleDAO.getById(resultSet.getInt(COLUMN_SCHEDULE_ID)));
            return ticket;
        } catch (SQLException e) {
            throw new ApplicationException("Error while building Ticket! " + e, ResponseStatus.BAD_REQUEST);
        }
    }
}
