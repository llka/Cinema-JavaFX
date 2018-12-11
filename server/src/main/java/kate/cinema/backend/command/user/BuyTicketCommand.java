package kate.cinema.backend.command.user;

import kate.cinema.backend.command.ActionCommand;
import kate.cinema.backend.exception.ApplicationException;
import kate.cinema.backend.service.ScheduleService;
import kate.cinema.backend.service.TicketService;
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

public class BuyTicketCommand implements ActionCommand {
    private static final Logger logger = LogManager.getLogger(BuyTicketCommand.class);

    private static final String PLACE_PARAM = "place";

    @Override
    public CommandResponse execute(CommandRequest request, CommandResponse response, Session session) throws ApplicationException {
        ScheduleService scheduleService = new ScheduleService();
        TicketService ticketService = new TicketService();

        int place = 0;
        if (request.getParameter(PLACE_PARAM) != null) {
            try {
                place = Integer.parseInt(request.getParameter(PLACE_PARAM));
            } catch (NumberFormatException e) {
                throw new ApplicationException("Invalid place!", ResponseStatus.BAD_REQUEST);
            }
        }

        List<Schedule> scheduleList = scheduleService.findFilms(request.getParameters());

        if (scheduleList != null && scheduleList.size() == 1) {
            ticketService.buyTicket(scheduleList.get(0), place, session.getVisitor().getContact());
        } else {
            throw new ApplicationException("Film not found!", ResponseStatus.BAD_REQUEST);
        }

        scheduleList = scheduleService.getAllFilms();
        ScheduleListDTO dto = new ScheduleListDTO(scheduleList);
        return new CommandResponse(JsonUtil.serialize(dto), ResponseStatus.OK);
    }
}

