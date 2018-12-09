package kate.cinema.backend.command.user;

import kate.cinema.backend.command.ActionCommand;
import kate.cinema.entity.enums.ResponseStatus;
import kate.cinema.entity.enums.RoleEnum;
import kate.cinema.entity.technical.CommandRequest;
import kate.cinema.entity.technical.CommandResponse;
import kate.cinema.entity.technical.Session;
import kate.cinema.entity.technical.Visitor;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class LogOutCommand implements ActionCommand {
    private static final Logger logger = LogManager.getLogger(LogOutCommand.class);

    @Override
    public CommandResponse execute(CommandRequest request, CommandResponse response, Session session) {

        session.setVisitor(new Visitor(RoleEnum.GUEST));
        session.getVisitor().setContact(null);

        return new CommandResponse(ResponseStatus.OK);
    }
}
