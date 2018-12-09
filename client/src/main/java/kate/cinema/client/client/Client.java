package kate.cinema.client.client;

import kate.cinema.client.exception.ClientException;
import kate.cinema.entity.technical.CommandRequest;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Client {
    private static final Logger logger = LogManager.getLogger(Client.class);

    private static final int DEFAULT_PORT_NUMBER = 8844;
    private static final String DEFAULT_SERVER_NAME = "localhost";

    private ObjectInputStream socketInput;
    private ObjectOutputStream socketOutput;
    private Socket socket;
    private String serverName;
    private int portNumber;

    public Client() {
        this.serverName = DEFAULT_SERVER_NAME;
        this.portNumber = DEFAULT_PORT_NUMBER;
    }

    public Client(String serverAddress) {
        this.serverName = serverAddress;
        this.portNumber = DEFAULT_PORT_NUMBER;
    }

    public Client(String serverAddress, int portNumber) {
        this.serverName = serverAddress;
        this.portNumber = portNumber;
    }

    public Client(int portNumber) {
        this.serverName = DEFAULT_SERVER_NAME;
        this.portNumber = portNumber;
    }


    public void connect() throws ClientException {
        try {
            socket = new Socket(serverName, portNumber);
        } catch (Exception e) {
            throw new ClientException("Error while connecting to server.", e);
        }

        logger.info("Connection accepted " + socket.getInetAddress() + ":" + socket.getPort());

        try {
            socketInput = new ObjectInputStream(socket.getInputStream());
            socketOutput = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            throw new ClientException("Exception creating new Input/output Streams.", e);
        }
    }

    public void sendRequest(CommandRequest request) throws ClientException {
        try {
            socketOutput.writeObject(request);
            logger.debug("sendRequest " + request);
        } catch (IOException e) {
            throw new ClientException("Exception sending request to server.", e);
        }
    }

    public void disconnect() throws ClientException {
        try {
            if (socketInput != null) {
                socketInput.close();
            }
        } catch (IOException e) {
            throw new ClientException("Error while closing socketInput.", e);
        }
        try {
            if (socketOutput != null) {
                socketOutput.close();
            }
        } catch (IOException e) {
            throw new ClientException("Error while closing socketOutput.", e);
        }
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            throw new ClientException("Error while closing socket.", e);
        }
    }

    public ObjectInputStream getSocketInput() {
        return socketInput;
    }

    public void setSocketInput(ObjectInputStream socketInput) {
        this.socketInput = socketInput;
    }

    public ObjectOutputStream getSocketOutput() {
        return socketOutput;
    }

    public void setSocketOutput(ObjectOutputStream socketOutput) {
        this.socketOutput = socketOutput;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public int getPortNumber() {
        return portNumber;
    }

    public void setPortNumber(int portNumber) {
        this.portNumber = portNumber;
    }
}
