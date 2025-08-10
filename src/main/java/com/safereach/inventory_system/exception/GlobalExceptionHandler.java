package com.safereach.inventory_system.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ProductAlreadyExistsException.class)
    public ProblemDetail handleProductAlreadyExists(ProductAlreadyExistsException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        pd.setTitle("Product already exists");
        pd.setType(URI.create("https://exmple.com/problems/product-exists"));
        pd.setDetail(ex.getMessage());
        return pd;
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ProblemDetail handleNotFound(ProductNotFoundException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        pd.setTitle("Resource not found");
        pd.setType(URI.create("https://exmple.com/problems/not-found"));
        pd.setDetail(ex.getMessage());
        return pd;
    }
}
