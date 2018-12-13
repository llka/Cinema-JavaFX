package kate.cinema.backend.service;

import kate.cinema.backend.dao.FilmDAO;
import kate.cinema.backend.dao.ScheduleDAO;
import kate.cinema.backend.exception.ApplicationException;
import kate.cinema.entity.Film;
import kate.cinema.entity.Schedule;
import kate.cinema.entity.enums.ResponseStatus;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

public class ScheduleService {
    private static final Logger logger = LogManager.getLogger(ScheduleService.class);

    private static final String DATE_TIME_FORMAT_PATTERN = "yyyy-MM-dd HH:mm:ss";

    private static final String UPDATE_PARAM_COST = "costToUpdate";
    private static final String UPDATE_PARAM_DURATION = "durationToUpdate";
    private static final String UPDATE_PARAM_TICKETS = "ticketsCountToUpdate";
    private static final String UPDATE_PARAM_DATE = "dateToUpdate";
    private static final String UPDATE_PARAM_TITLE = "titleToUpdate";
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

    public void deleteSchedule(Schedule schedule) throws ApplicationException {
        scheduleDAO.deleteById(schedule.getId());
    }

    public void update(Schedule oldSchedule, Map<String, String> updateParams) throws ApplicationException {
        boolean update = false;

        if (updateParams.get(UPDATE_PARAM_DATE) != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_TIME_FORMAT_PATTERN);
            try {
                oldSchedule.setDate(dateFormat.parse(updateParams.get(UPDATE_PARAM_DATE)));
                update = true;
            } catch (ParseException e) {
                throw new ApplicationException("Invalid Date format!", ResponseStatus.BAD_REQUEST);
            }
        }
        if (updateParams.get(UPDATE_PARAM_COST) != null) {
            try {
                oldSchedule.setTicketCost(new BigDecimal(updateParams.get(UPDATE_PARAM_COST)));
                update = true;
            } catch (NumberFormatException e) {
                throw new ApplicationException("Invalid Ticket Cost!", ResponseStatus.BAD_REQUEST);
            }
        }

        if (update) {
            scheduleDAO.update(oldSchedule);
        } else {
            throw new ApplicationException("Params for update are not set!", ResponseStatus.BAD_REQUEST);
        }
    }

    public void create(Film film, Map<String, String> scheduleParams) throws ApplicationException {

        if (film != null && scheduleParams.get(UPDATE_PARAM_DATE) != null && scheduleParams.get(UPDATE_PARAM_COST) != null) {
            Schedule schedule = new Schedule();
            schedule.setFilm(film);
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_TIME_FORMAT_PATTERN);
                schedule.setDate(dateFormat.parse(scheduleParams.get(UPDATE_PARAM_DATE)));
            } catch (ParseException e) {
                throw new ApplicationException("Invalid Date format!", ResponseStatus.BAD_REQUEST);
            }

            try {
                schedule.setTicketCost(new BigDecimal(scheduleParams.get(UPDATE_PARAM_COST)));
            } catch (NumberFormatException e) {
                throw new ApplicationException("Invalid Ticket Cost!", ResponseStatus.BAD_REQUEST);
            }
            scheduleDAO.save(schedule);
        }
    }


}
