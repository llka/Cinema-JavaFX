package kate.cinema.backend.command.user;

import kate.cinema.backend.command.ActionCommand;
import kate.cinema.backend.exception.ApplicationException;
import kate.cinema.backend.service.ScheduleService;
import kate.cinema.backend.util.JsonUtil;
import kate.cinema.dto.ScheduleListDTO;
import kate.cinema.entity.Schedule;
import kate.cinema.entity.enums.ResponseStatus;
import kate.cinema.entity.technical.CommandRequest;
import kate.cinema.entity.technical.CommandResponse;
import kate.cinema.entity.technical.Session;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.List;

public class GetFilmsCommand implements ActionCommand {
    private static final Logger logger = LogManager.getLogger(GetFilmsCommand.class);

    @Override
    public CommandResponse execute(CommandRequest request, CommandResponse response, Session session) throws ApplicationException {
        ScheduleService scheduleService = new ScheduleService();
        List<Schedule> scheduleList = scheduleService.findFilms(request.getParameters());

        ScheduleListDTO dto = new ScheduleListDTO(scheduleList);
        return new CommandResponse(JsonUtil.serialize(dto), ResponseStatus.OK);
    }
}
