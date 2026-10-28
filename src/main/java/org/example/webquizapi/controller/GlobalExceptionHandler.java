package org.example.webquizapi.controller;

import org.example.webquizapi.contracts.ErrorDetails;
import org.example.webquizapi.exception.QuizNotFoundException;
import org.example.webquizapi.exception.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;

@ControllerAdvice
public class GlobalExceptionHandler {
    // return
    private ResponseEntity<ErrorDetails> returnExceptionDetails(Exception ex, WebRequest req, HttpStatus status) {
        ErrorDetails errorDetails = new ErrorDetails(new Date(), ex.getMessage(), req.getDescription(false));
        return new ResponseEntity<>(errorDetails, status);
    }

    // specific exceptions
    @ExceptionHandler(QuizNotFoundException.class)
    public ResponseEntity<ErrorDetails> handleQuizNotFoundException(QuizNotFoundException ex, WebRequest req) {
        return returnExceptionDetails(ex, req, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorDetails> handleUserNotFoundException(UserNotFoundException ex, WebRequest req) {
        return returnExceptionDetails(ex, req, HttpStatus.NOT_FOUND);
    }

    // global exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetails> handleGlobalException(Exception ex, WebRequest req){
        return returnExceptionDetails(ex, req, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
