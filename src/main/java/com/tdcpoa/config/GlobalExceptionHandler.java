package com.tdcpoa.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<String> handleNoHandlerFoundException(
            NoHandlerFoundException ex, HttpServletRequest request) {
        logger.error("=== 404 Not Found ===");
        logger.error("Requested URL: {}", request.getRequestURL());
        logger.error("Request Method: {}", request.getMethod());
        logger.error("Request URI: {}", request.getRequestURI());
        logger.error("Query String: {}", request.getQueryString());
        logger.error("Headers: {}", getHeaders(request));
        logger.error("===================");
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("404 - Not Found: " + request.getRequestURI());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(
            Exception ex, HttpServletRequest request) {
        logger.error("=== 500 Internal Server Error ===");
        logger.error("Requested URL: {}", request.getRequestURL());
        logger.error("Request Method: {}", request.getMethod());
        logger.error("Request URI: {}", request.getRequestURI());
        logger.error("Exception: ", ex);
        logger.error("===================");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("500 - Internal Server Error: " + ex.getMessage());
    }

    private String getHeaders(HttpServletRequest request) {
        StringBuilder headers = new StringBuilder();
        request.getHeaderNames().asIterator().forEachRemaining(headerName -> {
            headers.append(headerName).append("=").append(request.getHeader(headerName)).append(", ");
        });
        return headers.toString();
    }
}

