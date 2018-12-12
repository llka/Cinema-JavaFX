package kate.cinema.backend.service;

import kate.cinema.backend.dao.FilmDAO;
import kate.cinema.backend.dao.ScheduleDAO;
import kate.cinema.backend.exception.ApplicationException;
import kate.cinema.entity.Schedule;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class ScheduleService {
    private static final Logger logger = LogManager.getLogger(ScheduleService.class);

    private FilmDAO filmDAO;
    private ScheduleDAO scheduleDAO;

    private static final String UPDATE_PARAM_COST = "costToUpdate";
    private static final String UPDATE_PARAM_DURATION = "durationToUpdate";
    private static final String UPDATE_PARAM_TICKETS = "ticketsCountToUpdate";
    private static final String UPDATE_PARAM_DATE = "dateToUpdate";
    private static final String UPDATE_PARAM_TITLE = "titleToUpdate";

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

    public void deleteSchedule(Schedule schedule) throws ApplicationException {
        scheduleDAO.deleteById(schedule.getId());
    }

    public void update(Schedule oldSchedule, Map<String, String> updateParams){
        if(updateParams.get(UPDATE_PARAM_DATE) != null){
            oldSchedule.setDate(new Date(updateParams.get(UPDATE_PARAM_DATE)));
        }

    }



}
