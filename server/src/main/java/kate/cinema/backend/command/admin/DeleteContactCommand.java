package kate.cinema.backend.command.admin;

import kate.cinema.backend.command.ActionCommand;
import kate.cinema.backend.exception.ApplicationException;
import kate.cinema.backend.service.ContactService;
import kate.cinema.entity.enums.ResponseStatus;
import kate.cinema.entity.technical.CommandRequest;
import kate.cinema.entity.technical.CommandResponse;
import kate.cinema.entity.technical.Session;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;


public class DeleteContactCommand implements ActionCommand {
    private static final Logger logger = LogManager.getLogger(DeleteContactCommand.class);

    private static final String EMAIL_PARAM = "email";
    private static final String ID_PARAM = "id";

    @Override
    public CommandResponse execute(CommandRequest request, CommandResponse response, Session session) throws ApplicationException {
        ContactService contactService = new ContactService();
        contactService.delete(request.getParameter(ID_PARAM), request.getParameter(EMAIL_PARAM));

        return new CommandResponse(ResponseStatus.OK);
    }
}
