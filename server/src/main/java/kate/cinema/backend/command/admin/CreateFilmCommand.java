package kate.cinema.backend.command.admin;

import kate.cinema.backend.command.ActionCommand;
import kate.cinema.backend.exception.ApplicationException;
import kate.cinema.backend.service.FilmService;
import kate.cinema.backend.service.ScheduleService;
import kate.cinema.backend.util.JsonUtil;
import kate.cinema.dto.ScheduleListDTO;
import kate.cinema.entity.Film;
import kate.cinema.entity.enums.ResponseStatus;
import kate.cinema.entity.technical.CommandRequest;
import kate.cinema.entity.technical.CommandResponse;
import kate.cinema.entity.technical.Session;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class CreateFilmCommand implements ActionCommand {
    private static final Logger logger = LogManager.getLogger(UpdateScheduleCommand.class);

    @Override
    public CommandResponse execute(CommandRequest request, CommandResponse response, Session session) throws ApplicationException {
        FilmService filmService = new FilmService();
        ScheduleService scheduleService = new ScheduleService();

        filmService.create(JsonUtil.deserialize(request.getBody(), Film.class), request.getParameters());

        ScheduleListDTO dto = new ScheduleListDTO(scheduleService.getAllFilms());
        return new CommandResponse(JsonUtil.serialize(dto), ResponseStatus.OK);
    }
}
