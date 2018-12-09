package kate.cinema.backend.command.user;

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

public class UpdateContactCommand implements ActionCommand {
    @Override
    public CommandResponse execute(CommandRequest request, CommandResponse response, Session session) throws ApplicationException {
        ContactService contactService = new ContactService();

        Contact contact = JsonUtil.deserialize(request.getBody(), Contact.class);
        contactService.update(contact);

        session.getVisitor().setContact(contact);
        session.getVisitor().setRole(contact.getRole());
        return new CommandResponse(CommandType.UPDATE_CONTACT.toString(), JsonUtil.serialize(contact), ResponseStatus.OK);

    }
}
