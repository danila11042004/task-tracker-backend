package danila.backendservice.web;

import danila.backendservice.dto.ErrorResponseDto;
import danila.backendservice.exception.OutboxEventSerializationException;
import danila.backendservice.exception.TaskNotFoundException;
import danila.backendservice.exception.UserAlreadyExistException;
import danila.backendservice.exception.UserNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final String UNKNOWN_ERROR_MESSAGE = "Unknown error";
    private static final String UNKNOWN_ERROR_MESSAGE_LOG = "Unknown error in {} {}";
    private static final String REQUEST_BODY_ERROR_MESSAGE = "Invalid request body";


    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponseDto handleFileAlreadyExistException(AuthenticationException exception) {
        return new ErrorResponseDto(exception.getMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDto handleHttpMessageNotReadableException(
            HttpMessageNotReadableException exception) {
        return new ErrorResponseDto(REQUEST_BODY_ERROR_MESSAGE);
    }

    @ExceptionHandler(UserAlreadyExistException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponseDto handleUserAlreadyExistException(UserAlreadyExistException exception) {
        return new ErrorResponseDto(exception.getMessage());
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponseDto handleUserNotFoundException(UserNotFoundException exception) {
        return new ErrorResponseDto(exception.getMessage());
    }

    @ExceptionHandler(TaskNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponseDto handleTaskNotFoundException(TaskNotFoundException exception) {
        return new ErrorResponseDto(exception.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDto handleValidationException(MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult().getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining("\n"));
        return new ErrorResponseDto(message);
    }

    @ExceptionHandler(OutboxEventSerializationException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponseDto handleOutboxEventSerializationException(OutboxEventSerializationException exception, HttpServletRequest req) {
        log.error(exception.getMessage(), req.getMethod(), req.getRequestURI(), exception);
        return new ErrorResponseDto(UNKNOWN_ERROR_MESSAGE);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponseDto handleUnknownException(Exception exception, HttpServletRequest req) {
        log.error(UNKNOWN_ERROR_MESSAGE_LOG, req.getMethod(), req.getRequestURI(), exception);
        return new ErrorResponseDto(UNKNOWN_ERROR_MESSAGE);
    }

}
