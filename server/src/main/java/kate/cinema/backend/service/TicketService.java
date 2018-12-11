package kate.cinema.backend.service;

import kate.cinema.backend.dao.FilmDAO;
import kate.cinema.backend.dao.ScheduleDAO;
import kate.cinema.backend.dao.TicketDAO;
import kate.cinema.backend.exception.ApplicationException;
import kate.cinema.entity.Contact;
import kate.cinema.entity.Film;
import kate.cinema.entity.Schedule;
import kate.cinema.entity.Ticket;
import kate.cinema.entity.enums.ResponseStatus;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.List;

public class TicketService {
    private static final Logger logger = LogManager.getLogger(TicketService.class);

    private FilmDAO filmDAO;
    private ScheduleDAO scheduleDAO;
    private TicketDAO ticketDAO;

    public TicketService() {
        filmDAO = new FilmDAO();
        scheduleDAO = new ScheduleDAO();
        ticketDAO = new TicketDAO();
    }

    public void buyTicket(Schedule schedule, int place, Contact contact) throws ApplicationException {
        Film film = schedule.getFilm();
        int available = film.getTicketsLeft();
        if (available <= 0) {
            throw new ApplicationException("There are no available tickets for the film! ", ResponseStatus.BAD_REQUEST);
        }

        List<Ticket> allTickets = ticketDAO.getAll();
        for (Ticket ticket : allTickets) {
            if (ticket.getSchedule().getId() == schedule.getId() && ticket.getPlaceNumber() == place) {
                throw new ApplicationException("Ticket is already bought! ", ResponseStatus.BAD_REQUEST);
            }
        }

        Ticket ticket = new Ticket();
        ticket.setSchedule(schedule);
        ticket.setPlaceNumber(place);
        ticket.setCost(schedule.getFilm().getTicketCost());
        ticket = ticketDAO.save(ticket);
        ticketDAO.saveToContactsTickets(ticket, contact);
        film.setTicketsLeft(--available);
        filmDAO.update(film);
    }

}
