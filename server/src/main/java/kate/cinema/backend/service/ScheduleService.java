package kate.cinema.backend.service;

import kate.cinema.backend.dao.FilmDAO;
import kate.cinema.backend.dao.ScheduleDAO;
import kate.cinema.backend.exception.ApplicationException;
import kate.cinema.entity.Schedule;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Map;

public class ScheduleService {
    private static final Logger logger = LogManager.getLogger(ScheduleService.class);

    private FilmDAO filmDAO;
    private ScheduleDAO scheduleDAO;

    public ScheduleService() {
        filmDAO = new FilmDAO();
        scheduleDAO = new ScheduleDAO();
    }

    public List<Schedule> findFilms(Map<String, String> filterParams) throws ApplicationException {
        if (filterParams == null || filterParams.isEmpty()) {
            return scheduleDAO.getAll();
        } else {
            return scheduleDAO.find(filterParams);
        }
    }

    public List<Schedule> getAllFilms() throws ApplicationException {

        return scheduleDAO.getAll();

    }


}
