package kate.cinema.backend.exception;

import kate.cinema.entity.enums.ResponseStatus;

public class ApplicationException extends Exception {
    private ResponseStatus status;

    public ApplicationException(ResponseStatus status) {
        this.status = status;
    }

    public ApplicationException(String message, ResponseStatus status) {
        super(message);
        this.status = status;
    }

    public ApplicationException(String message, Throwable cause, ResponseStatus status) {
        super(message, cause);
        this.status = status;
    }

    public ApplicationException(Throwable cause, ResponseStatus status) {
        super(cause);
        this.status = status;
    }

    public ApplicationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, ResponseStatus status) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.status = status;
    }

    public ResponseStatus getStatus() {
        return status;
    }

    public void setStatus(ResponseStatus status) {
        this.status = status;
    }
}
