package kate.cinema.backend.command.user;

import kate.cinema.backend.command.ActionCommand;
import kate.cinema.backend.exception.ApplicationException;
import kate.cinema.backend.service.ContactService;
import kate.cinema.backend.util.JsonUtil;
import kate.cinema.entity.Contact;
import kate.cinema.entity.enums.ResponseStatus;
import kate.cinema.entity.technical.CommandRequest;
import kate.cinema.entity.technical.CommandResponse;
import kate.cinema.entity.technical.Session;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class GetContactCommand implements ActionCommand {
    private static final Logger logger = LogManager.getLogger(GetContactCommand.class);

    private static final String EMAIL_PARAM = "email";
    private static final String ID_PARAM = "id";

    @Override
    public CommandResponse execute(CommandRequest request, CommandResponse response, Session session) throws ApplicationException {
        ContactService contactLogic = new ContactService();
        String email = request.getParameter(EMAIL_PARAM);
        int id = -1;
        if (request.getParameter(ID_PARAM) != null) {
            id = Integer.parseInt(request.getParameter(ID_PARAM));
        }
        Contact contact = null;
        if (email != null && !email.isEmpty()) {
            try {
                contact = contactLogic.getByEmail(email);
            } catch (ApplicationException e) {
                if (id > 0) {
                    contact = contactLogic.getById(id);
                }
            }
        } else if (id > 0) {
            try {
                contact = contactLogic.getById(id);
            } catch (ApplicationException e) {
                contact = contactLogic.getByEmail(email);
            }
        }
        if (contact != null) {
            logger.debug(contact);
            return new CommandResponse(JsonUtil.serialize(contact), ResponseStatus.OK);
        } else {
            return new CommandResponse("Contact not found", ResponseStatus.NOT_FOUND);
        }
    }
}