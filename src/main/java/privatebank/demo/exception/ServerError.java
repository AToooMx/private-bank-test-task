package privatebank.demo.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;


@Getter
public enum ServerError {
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error"),
    API_CALL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Api call error"),
    EXCHANGE_RATE_NOT_FOUND(HttpStatus.NOT_FOUND, "Exchange rate not found"),
    VALIDATION_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Validation error"),
    PROCESSING_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Processing error"),
    INVALID_RATE_FORMAT(HttpStatus.INTERNAL_SERVER_ERROR, "Invalid rate format"),
    NO_SUFFICIENT_DATA(HttpStatus.BAD_REQUEST, "No sufficient data to calculate dynamic"),;

    private final HttpStatus status;
    private final String message;

    ServerError(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
