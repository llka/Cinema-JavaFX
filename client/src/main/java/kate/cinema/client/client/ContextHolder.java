package kate.cinema.client.client;


import kate.cinema.entity.technical.CommandResponse;
import kate.cinema.entity.technical.Session;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.EmptyStackException;
import java.util.NoSuchElementException;

public class ContextHolder {
    private static Client client;
    private static Thread server;
    private static Session session;

    private static Deque<CommandResponse> responseStack;

    public ContextHolder() {
    }

    public static CommandResponse getLastResponse() {
        if (responseStack == null) {
            responseStack = new ArrayDeque<>();
        }

        while (true) {
            try {
                CommandResponse response = responseStack.pop();
                if (response != null) {
                    return response;
                }
            } catch (EmptyStackException | NoSuchElementException e) {

            }
        }
    }

    public static Client getClient() {
        return client;
    }

    public static void setClient(Client client) {
        ContextHolder.client = client;
    }

    public static Thread getServer() {
        return server;
    }

    public static void setServer(Thread server) {
        ContextHolder.server = server;
    }

    public static Deque<CommandResponse> getResponseStack() {
        if (responseStack == null) {
            responseStack = new ArrayDeque<>();
        }
        return responseStack;
    }

    public static void setResponseStack(Deque<CommandResponse> stack) {
        responseStack = stack;
    }

    public static Session getSession() {
        return session;
    }

    public static void setSession(Session session) {
        ContextHolder.session = session;
    }
}
