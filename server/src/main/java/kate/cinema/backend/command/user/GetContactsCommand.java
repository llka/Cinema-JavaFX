package kate.cinema.backend.command.user;

import kate.cinema.ContactListDTO;
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

import java.util.List;

public class GetContactsCommand implements ActionCommand {
    private static final Logger logger = LogManager.getLogger(GetContactsCommand.class);

    @Override
    public CommandResponse execute(CommandRequest request, CommandResponse response, Session session) throws ApplicationException {
        ContactService contactLogic = new ContactService();

        List<Contact> contacts = contactLogic.getAll();
        ContactListDTO contactListDTO = new ContactListDTO(contacts);
        return new CommandResponse(CommandType.GET_CONTACTS.toString(), JsonUtil.serialize(contactListDTO), ResponseStatus.OK);
    }
}