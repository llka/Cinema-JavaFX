package kate.cinema.backend.starter;

import kate.cinema.backend.builder.ServerBuilder;
import kate.cinema.backend.exception.ApplicationException;
import kate.cinema.backend.server.Server;
import org.apache.log4j.Logger;


public class Starter {
    private static Logger logger = Logger.getLogger(Starter.class);

    public static void main(String[] args) {
        try {
            Server server = new ServerBuilder().build(args);
            startServer(server);
        } catch (ApplicationException e) {
            logger.error("Can not initialize server. " + e);
        }
    }

    private static void startServer(Server server) {
        try {
            server.start();
        } catch (ApplicationException e) {
            logger.error("Can not start server. " + e);
        }
    }

}
