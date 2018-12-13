package kate.cinema.backend.command.guest;

import kate.cinema.backend.command.ActionCommand;
import kate.cinema.backend.command.CommandType;
import kate.cinema.backend.exception.ApplicationException;
import kate.cinema.backend.service.ContactService;
import kate.cinema.backend.util.JsonUtil;
import kate.cinema.entity.Contact;
import kate.cinema.entity.enums.ResponseStatus;
import kate.cinema.entity.enums.RoleEnum;
import kate.cinema.entity.technical.CommandRequest;
import kate.cinema.entity.technical.CommandResponse;
import kate.cinema.entity.technical.Session;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;


public class RegisterCommand implements ActionCommand {
    private static final Logger logger = LogManager.getLogger(RegisterCommand.class);

    @Override
    public CommandResponse execute(CommandRequest request, CommandResponse response, Session session) throws ApplicationException {
        ContactService contactService = new ContactService();

        Contact contact = JsonUtil.deserialize(request.getBody(), Contact.class);
        if (contact.getFirstName() == null || contact.getFirstName().isEmpty()) {
            throw new ApplicationException("Invalid First name!", ResponseStatus.BAD_REQUEST);
        }
        if (contact.getLastName() == null || contact.getLastName().isEmpty()) {
            throw new ApplicationException("Invalid Last name!", ResponseStatus.BAD_REQUEST);
        }
        if (contact.getEmail() == null || contact.getEmail().isEmpty()) {
            throw new ApplicationException("Invalid Email!", ResponseStatus.BAD_REQUEST);
        }
        if (contact.getPassword() == null || contact.getPassword().isEmpty()) {
            throw new ApplicationException("Invalid Password!", ResponseStatus.BAD_REQUEST);
        }

        contact.setRole(RoleEnum.USER);
        contact = contactService.register(contact);

        session.getVisitor().setContact(contact);
        session.getVisitor().setRole(contact.getRole());
        return new CommandResponse(CommandType.REGISTER.toString(), JsonUtil.serialize(contact), ResponseStatus.OK);

    }
}