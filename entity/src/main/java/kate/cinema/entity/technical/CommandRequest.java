package kate.cinema.entity.technical;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CommandRequest extends SocketEntity {
    private static final long serialVersionUID = 105242440943911307L;

    private String command;
    private String body;
    private Map<String, String> parameters;

    public CommandRequest() {
        parameters = new HashMap<>();
    }

    public CommandRequest(String command) {
        this.command = command;
        parameters = new HashMap<>();
    }

    public CommandRequest(String command, String body) {
        this.command = command;
        this.body = body;
        parameters = new HashMap<>();
    }

    public CommandRequest(String command, Map<String, String> parameters) {
        this.command = command;
        this.parameters = parameters;
    }

    public CommandRequest(String command, String body, Map<String, String> parameters) {
        this.command = command;
        this.body = body;
        this.parameters = parameters;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    public void addParameter(String key, String value) {
        parameters.put(key, value);
    }

    public void removeParameter(String key) {
        parameters.remove(key);
    }

    public String getParameter(String key) {
        return parameters.get(key);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CommandRequest that = (CommandRequest) o;
        return command.equals(that.command) &&
                Objects.equals(body, that.body) &&
                Objects.equals(parameters, that.parameters);
    }

    @Override
    public int hashCode() {
        return Objects.hash(command, body, parameters);
    }

    @Override
    public String toString() {
        return "CommandRequest{" +
                "command='" + command + '\'' +
                ", body='" + body + '\'' +
                ", parameters=" + parameters +
                '}';
    }
}
