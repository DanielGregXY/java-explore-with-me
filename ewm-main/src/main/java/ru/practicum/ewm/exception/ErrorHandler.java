package ru.practicum.ewm.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handlerNotFoundException(final ObjectNotFoundException ex) {
        log.warn("404 {}", ex.getMessage(), ex);
        return new ErrorResponse("Object not found 404", ex.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handlerBadRequest(final BadRequestException ex) {
        log.warn("404 {}", ex.getMessage(), ex);
        return new ErrorResponse("Object are not available 400 ", ex.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleIntegrityException(final ConflictException ex) {
        log.warn("409 {}", ex.getMessage(), ex);
        return new ErrorResponse("No valid data", ex.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleIntegrityException(final DataAccessException ex) {
        log.warn("409 {}", ex.getMessage(), ex);
        return new ErrorResponse("No valid data", ex.getMessage());
    }
}

