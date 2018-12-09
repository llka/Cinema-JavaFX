package kate.cinema.backend.command;


import kate.cinema.entity.enums.ResponseStatus;
import kate.cinema.entity.technical.CommandRequest;
import kate.cinema.entity.technical.CommandResponse;
import kate.cinema.entity.technical.Session;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class EmptyCommand implements ActionCommand {
    private static final Logger logger = LogManager.getLogger(EmptyCommand.class);

    @Override
    public CommandResponse execute(CommandRequest request, CommandResponse response, Session session) {
        logger.debug("Welcome to empty command");

        return new CommandResponse(CommandType.EMPTY.toString(), ResponseStatus.NOT_IMPLEMENTED);
    }
}
