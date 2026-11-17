package com.trivia.challenge.application.exception;

import com.trivia.challenge.domain.exception.ServiceException;
import jakarta.validation.ConstraintViolationException;
import java.net.URI;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ServiceException.class)
    public ProblemDetail handleServiceException(ServiceException exception) {
        return problem(HttpStatus.INTERNAL_SERVER_ERROR, "Server error", exception);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ProblemDetail handleConstraintViolation(ConstraintViolationException exception) {
        return problem(HttpStatus.BAD_REQUEST, "Invalid request", exception);
    }

    private static ProblemDetail problem(HttpStatus status, String title, Exception exception) {
        var problem = ProblemDetail.forStatusAndDetail(status, String.valueOf(exception.getMessage()));
        problem.setType(URI.create("about:blank"));
        problem.setTitle(title);
        return problem;
    }
}
