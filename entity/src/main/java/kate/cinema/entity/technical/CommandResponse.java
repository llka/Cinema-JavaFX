package kate.cinema.entity.technical;

import kate.cinema.entity.enums.ResponseStatus;

import java.util.Objects;

public class CommandResponse extends SocketEntity {
    private static final long serialVersionUID = 205242440943911308L;

    private String fromCommand;
    private String body;
    private ResponseStatus status;

    public CommandResponse() {
    }

    public CommandResponse(ResponseStatus status) {
        this.status = status;
    }

    public CommandResponse(String body, ResponseStatus status) {
        this.body = body;
        this.status = status;
    }

    public CommandResponse(String fromCommand, String body, ResponseStatus status) {
        this.fromCommand = fromCommand;
        this.body = body;
        this.status = status;
    }

    public CommandResponse(String fromCommand) {
        this.fromCommand = fromCommand;
    }

    public CommandResponse(String fromCommand, String body) {
        this.fromCommand = fromCommand;
        this.body = body;
    }

    public ResponseStatus getStatus() {
        return status;
    }

    public void setStatus(ResponseStatus status) {
        this.status = status;
    }

    public String getFromCommand() {
        return fromCommand;
    }

    public void setFromCommand(String fromCommand) {
        this.fromCommand = fromCommand;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CommandResponse that = (CommandResponse) o;
        return fromCommand.equals(that.fromCommand) &&
                Objects.equals(body, that.body) &&
                status == that.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(fromCommand, body, status);
    }

    @Override
    public String toString() {
        return "CommandResponse{" +
                "fromCommand='" + fromCommand + '\'' +
                ", body='" + body + '\'' +
                ", status=" + status +
                '}';
    }
}
