package privatebank.demo.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import privatebank.demo.exception.CustomException;
import privatebank.demo.exception.ErrorApi;

import java.time.Instant;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({Exception.class})
    public ResponseEntity<ErrorApi> handleError(HttpServletRequest request, Exception ex) {
        log.info("Request failed. {} {} Cause: {}", request.getMethod(), request.getRequestURI(), ex.getMessage(), ex);
        var error = ErrorApi.builder()
            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .timestamp(Instant.now())
            .message(ex.getMessage())
            .build();
        return ResponseEntity.status(error.getStatus()).body(error);
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorApi> handleServerError(CustomException ex, HttpServletRequest request) {
        log.error("Request failed. {} {} Cause: {}", request.getMethod(), request.getRequestURI(), ex.getMessage(), ex);
        var error = ErrorApi.builder()
            .timestamp(Instant.now())
            .status(ex.getError().getStatus().value())
            .message(ex.getMessage())
            .build();
        return ResponseEntity.status(error.getStatus()).body(error);
    }
}
