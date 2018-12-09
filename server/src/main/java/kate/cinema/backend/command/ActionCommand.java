package kate.cinema.backend.command;


import kate.cinema.backend.exception.ApplicationException;
import kate.cinema.entity.technical.CommandRequest;
import kate.cinema.entity.technical.CommandResponse;
import kate.cinema.entity.technical.Session;

public interface ActionCommand {
    CommandResponse execute(CommandRequest request, CommandResponse response, Session session) throws ApplicationException;
}