package kate.cinema.client.listener;

import kate.cinema.client.client.ContextHolder;
import kate.cinema.entity.technical.CommandResponse;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.ObjectInputStream;

public class ServerResponseListener extends Thread {
    private static Logger logger = Logger.getLogger(ServerResponseListener.class);
    private ObjectInputStream socketInput;
    private CommandResponse lastResponse;

    public ServerResponseListener(ObjectInputStream socketInput) {
        this.socketInput = socketInput;
    }

    public ObjectInputStream getSocketInput() {
        return socketInput;
    }

    public void setSocketInput(ObjectInputStream socketInput) {
        this.socketInput = socketInput;
    }

    @Override
    public void run() {
        while (true) {
            try {
                CommandResponse response = (CommandResponse) socketInput.readObject();
                lastResponse = response;
                ContextHolder.getResponseStack().push(response);
                logger.debug("Response received: " + response);

            } catch (IOException e) {
                logger.info("Server was stopped. You are disconnected now. " + e.getMessage());
                break;
            } catch (ClassNotFoundException e) {
                logger.error("Error while waiting for responses from server: " + e);
                break;
            }
        }
    }

    public CommandResponse getLastResponse() {
        return lastResponse;
    }

    public void setLastResponse(CommandResponse lastResponse) {
        this.lastResponse = lastResponse;
    }
}
