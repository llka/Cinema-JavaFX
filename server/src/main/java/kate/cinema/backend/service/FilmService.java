package kate.cinema.backend.service;

import kate.cinema.backend.dao.FilmDAO;
import kate.cinema.backend.dao.ScheduleDAO;
import kate.cinema.backend.exception.ApplicationException;
import kate.cinema.entity.Film;
import kate.cinema.entity.Schedule;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.List;

public class FilmService {
    private static final Logger logger = LogManager.getLogger(FilmService.class);

    private FilmDAO filmDAO;
    private ScheduleDAO scheduleDAO;

    public FilmService() {
        filmDAO = new FilmDAO();
        scheduleDAO = new ScheduleDAO();
    }


    public List<Film> getAll() throws ApplicationException {
        return filmDAO.getAll();
    }
}
