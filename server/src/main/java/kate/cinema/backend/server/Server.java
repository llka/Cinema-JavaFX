package kate.cinema.backend.server;

import kate.cinema.backend.command.ActionCommand;
import kate.cinema.backend.exception.ApplicationException;
import kate.cinema.backend.factory.ActionFactory;
import kate.cinema.entity.enums.ResponseStatus;
import kate.cinema.entity.enums.RoleEnum;
import kate.cinema.entity.technical.CommandRequest;
import kate.cinema.entity.technical.CommandResponse;
import kate.cinema.entity.technical.Session;
import kate.cinema.entity.technical.Visitor;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class Server {
    private static final Logger logger = LogManager.getLogger(Server.class);
    private static final int DEFAULT_PORT_NUMBER = 8844;
    private static final int SAME_TIME_REQUESTS_COUNT = 1;

    private static AtomicInteger connectionsCount = new AtomicInteger(0);
    private HashMap<Integer, ClientThread> clientThreads;
    private int portNumber;
    private AtomicBoolean isServerWorking;
    private Semaphore semaphore;

    public Server() {
        this.portNumber = DEFAULT_PORT_NUMBER;
        this.clientThreads = new HashMap<>();
        this.isServerWorking = new AtomicBoolean(false);
        this.semaphore = new Semaphore(SAME_TIME_REQUESTS_COUNT);
    }

    public Server(int port) {
        this.portNumber = port;
        this.clientThreads = new HashMap<>();
        this.isServerWorking = new AtomicBoolean(false);
        this.semaphore = new Semaphore(SAME_TIME_REQUESTS_COUNT);
    }

    public void start() throws ApplicationException {
        isServerWorking.set(true);
        logger.info("Server started");
        try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
            while (isServerWorking.get()) {
                logger.info("Server is waiting for Clients on port number: " + portNumber);

                Socket socket = null;
                try {
                    /*This method waits until a client connects to the server on the given port.*/
                    socket = serverSocket.accept();
                /*When the ServerSocket invokes accept(), the method does not return until a client connects.
                 After a client does connect, the ServerSocket creates a new Socket on an unspecified port and
                 returns a reference to this new Socket. A TCP connection now exists between the client and
                 the server, and communication can begin.*/
                } catch (IOException e) {
                    throw new ApplicationException("Can not accept Server Socket.", e, ResponseStatus.INTERNAL_SERVER_ERROR);
                }
                if (!isServerWorking.get()) {
                    break;
                }

                try {
                    ClientThread client = new ClientThread(socket);
                    clientThreads.put(client.getClientId(), client);
                    client.start();
                } catch (ApplicationException e) {
                    logger.error("Can not log in client: " + e);
                }
            }
        } catch (IOException e) {
            throw new ApplicationException("Can not init ServerSocket on port number: " + portNumber + " " + e, ResponseStatus.INTERNAL_SERVER_ERROR);
        }

        isServerWorking.set(false);
        logger.info("Server stopped!");

        clientThreads.forEach((id, client) -> {
            try {
                client.disconnect();
            } catch (ApplicationException e) {
                logger.error("Error while closing clients socket's threads. ", e);
            }
        });

    }

    private void removeClient(int clientId) {
        clientThreads.remove(clientId);
    }


    class ClientThread extends Thread {
        private Socket socket;
        private ObjectInputStream socketInput;
        private ObjectOutputStream socketOutput;

        private Integer clientId;
        private Session session;

        public ClientThread(Socket socket) throws ApplicationException {
            this.clientId = connectionsCount.incrementAndGet();
            this.socket = socket;

            try {
                socketOutput = new ObjectOutputStream(socket.getOutputStream());
                socketInput = new ObjectInputStream(socket.getInputStream());

                session = new Session();
                Visitor visitor = new Visitor();
                visitor.setRole(RoleEnum.GUEST);
                session.setVisitor(visitor);

            } catch (IOException e) {
                throw new ApplicationException("Error while creating new Input / output Streams: " + e, ResponseStatus.INTERNAL_SERVER_ERROR);
            }
        }

        @Override
        public void run() {
            logger.debug("New client connected! Client id = " + clientId);
            boolean keepGoing = true;
            CommandRequest request;
            while (keepGoing) {
                try {
                    request = receiveRequest(socketInput);
                } catch (ApplicationException e) {
                    logger.error(e);
                    logger.info("Client with id = " + clientId + " disconnected");
                    try {
                        disconnect();
                    } catch (ApplicationException e1) {
                        logger.error(e);
                    }
                    break;
                }

                CommandResponse response = null;
                ActionFactory actionFactory = new ActionFactory();

                ActionCommand command = null;
                try {
                    command = actionFactory.defineCommand(request, session.getVisitor());
                    response = command.execute(request, response, session);

                    if (response != null) {
                        answer(response, clientId);
                    } else {
                        answer(new CommandResponse(ResponseStatus.BAD_REQUEST), clientId);
                    }

                    logger.debug("server session " + session);

                } catch (ApplicationException e) {
                    logger.info("Exception response: " + e);
                    answer(new CommandResponse(e.getMessage(), e.getStatus()), clientId);
                }

            }

            try {
                semaphore.acquire();
                removeClient(clientId);
            } catch (InterruptedException e) {
                logger.error("Can not remove client before disconnection. " + e);
            }
            semaphore.release();

            try {
                disconnect();
            } catch (ApplicationException e) {
                logger.error("Can not disconnect. " + e);
            }
        }

        private void answer(CommandResponse response, int clientId) {

            try {
                semaphore.acquire();

                ClientThread client = clientThreads.get(clientId);
                try {
                    client.sendResponse(response);
                } catch (ApplicationException e) {
                    logger.error("Cannot answer to client with id = " + clientId);
                    clientThreads.remove(clientId);
                }

                semaphore.release();
            } catch (InterruptedException e) {
                logger.error("Can not send response " + e);
            }
        }


        private void disconnect() throws ApplicationException {
            try {
                if (socketInput != null) {
                    socketInput.close();
                }
            } catch (IOException e) {
                throw new ApplicationException("Error while closing socketInput" + e, ResponseStatus.INTERNAL_SERVER_ERROR);
            }
            try {
                if (socketOutput != null) {
                    socketOutput.close();
                }
            } catch (IOException e) {
                throw new ApplicationException("Error while closing socketOutput" + e, ResponseStatus.INTERNAL_SERVER_ERROR);
            }
            try {
                if (socket != null) {
                    socket.close();
                }
            } catch (IOException e) {
                throw new ApplicationException("Error while closing socket" + e, ResponseStatus.INTERNAL_SERVER_ERROR);
            }
            //connectionsCount.decrementAndGet();
        }

        private void sendResponse(CommandResponse response) throws ApplicationException {
            if (!socket.isConnected()) {
                disconnect();
                throw new ApplicationException("Socket is closed!", ResponseStatus.INTERNAL_SERVER_ERROR);
            }

            try {
                socketOutput.writeObject(response);
            } catch (IOException e) {
                throw new ApplicationException("Error while sending response for " + session.getVisitor(), ResponseStatus.INTERNAL_SERVER_ERROR);
            }
        }

        private CommandRequest receiveRequest(ObjectInputStream inputStream) throws ApplicationException {
            try {
                return (CommandRequest) inputStream.readObject();
            } catch (IOException | ClassNotFoundException e) {
                throw new ApplicationException("Can not receive request!", ResponseStatus.BAD_REQUEST);
            }
        }

        public Socket getSocket() {
            return socket;
        }

        public void setSocket(Socket socket) {
            this.socket = socket;
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

        public int getClientId() {
            return clientId;
        }

        public void setClientId(int clientId) {
            this.clientId = clientId;
        }

        public Session getSession() {
            return session;
        }

        public void setSession(Session session) {
            this.session = session;
        }
    }
}
