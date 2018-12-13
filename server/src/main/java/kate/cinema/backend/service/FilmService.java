package kate.cinema.backend.service;

import kate.cinema.backend.dao.FilmDAO;
import kate.cinema.backend.exception.ApplicationException;
import kate.cinema.entity.Film;
import kate.cinema.entity.enums.ResponseStatus;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Map;

public class FilmService {
    private static final Logger logger = LogManager.getLogger(FilmService.class);


    private static final String UPDATE_PARAM_DURATION = "durationToUpdate";
    private static final String UPDATE_PARAM_TICKETS = "ticketsCountToUpdate";
    private static final String UPDATE_PARAM_TITLE = "titleToUpdate";

    private static final String UPDATE_PARAM_COST = "costToUpdate";
    private static final String UPDATE_PARAM_DATE = "dateToUpdate";


    private FilmDAO filmDAO;
    private ScheduleService scheduleService;

    public FilmService() {
        filmDAO = new FilmDAO();
        scheduleService = new ScheduleService();
    }

    public List<Film> getAll() throws ApplicationException {
        return filmDAO.getAll();
    }

    public Film findFilm(int filmId) throws ApplicationException {
        return filmDAO.getById(filmId);
    }

    public void update(Film oldFilm, Map<String, String> updateParams) throws ApplicationException {
        boolean update = false;

        if (updateParams.get(UPDATE_PARAM_DURATION) != null) {
            try {
                oldFilm.setDurationInMin(Integer.parseInt(updateParams.get(UPDATE_PARAM_DURATION)));
                update = true;
            } catch (NumberFormatException e) {
                throw new ApplicationException("Invalid Duration!", ResponseStatus.BAD_REQUEST);
            }
        }
        if (updateParams.get(UPDATE_PARAM_TICKETS) != null) {
            try {
                oldFilm.setTicketsLeft(Integer.parseInt(updateParams.get(UPDATE_PARAM_TICKETS)));
                update = true;
            } catch (NumberFormatException e) {
                throw new ApplicationException("Invalid Tickets left param!", ResponseStatus.BAD_REQUEST);
            }
        }
        if (updateParams.get(UPDATE_PARAM_TITLE) != null && !updateParams.get(UPDATE_PARAM_TITLE).isEmpty()) {
            oldFilm.setTitle(updateParams.get(UPDATE_PARAM_TITLE));
            update = true;
        }

        if (update) {
            filmDAO.update(oldFilm);
        } else {
            throw new ApplicationException("Params for update are not set!", ResponseStatus.BAD_REQUEST);
        }
    }

    public void create(Film film, Map<String, String> scheduleParams) throws ApplicationException {
        if (film != null) {
            film = filmDAO.save(film);
            logger.info("film created!");
            if (film != null && scheduleParams.get(UPDATE_PARAM_COST) != null && scheduleParams.get(UPDATE_PARAM_DATE) != null) {
                scheduleService.create(film, scheduleParams);
                logger.info("schedule created!");
            }
        } else {
            throw new ApplicationException("Film cannot be created!", ResponseStatus.BAD_REQUEST);
        }
    }

}
