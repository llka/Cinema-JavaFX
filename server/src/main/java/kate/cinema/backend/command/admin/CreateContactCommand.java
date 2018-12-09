package kate.cinema.backend.command.admin;

import kate.cinema.backend.command.ActionCommand;
import kate.cinema.backend.command.CommandType;
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


public class CreateContactCommand implements ActionCommand {
    private static final Logger logger = LogManager.getLogger(CreateContactCommand.class);

    @Override
    public CommandResponse execute(CommandRequest request, CommandResponse response, Session session) throws ApplicationException {
        ContactService contactService = new ContactService();

        Contact contact = JsonUtil.deserialize(request.getBody(), Contact.class);
        try {
            contactService.getByEmail(contact.getEmail());
            return new CommandResponse(CommandType.CREATE_CONTACT.toString(), "Contact with the same email " + contact.getEmail() + " already exists!", ResponseStatus.PARTIAL_CONTENT);
        } catch (ApplicationException e) {
            contact = contactService.register(contact);
            return new CommandResponse(CommandType.CREATE_CONTACT.toString(), JsonUtil.serialize(contact), ResponseStatus.OK);
        }
    }
}
