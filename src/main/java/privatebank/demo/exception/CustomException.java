package privatebank.demo.exception;

import lombok.Data;

@Data
public class CustomException extends RuntimeException {
    private ServerError error;

    public CustomException(ServerError error) {
        super(error.getMessage());
        this.error = error;
    }
}
