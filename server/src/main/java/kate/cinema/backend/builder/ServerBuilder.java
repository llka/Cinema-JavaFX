package kate.cinema.backend.builder;


import kate.cinema.backend.exception.ApplicationException;
import kate.cinema.backend.server.Server;
import kate.cinema.entity.enums.ResponseStatus;

public class ServerBuilder {
    private static final int ARG_PORT_NUMBER_INDEX = 0;

    public Server build(String[] arguments) throws ApplicationException {
        Server server;
        switch (arguments.length) {
            case 1:
                try {
                    server = new Server(Integer.parseInt(arguments[ARG_PORT_NUMBER_INDEX]));
                } catch (NumberFormatException e) {
                    throw new ApplicationException("Invalid port number.", ResponseStatus.INTERNAL_SERVER_ERROR);
                }
                break;
            case 0:
                server = new Server();
                break;
            default:
                throw new ApplicationException("Invalid number of arguments!", ResponseStatus.INTERNAL_SERVER_ERROR);
        }
        return server;
    }
}
