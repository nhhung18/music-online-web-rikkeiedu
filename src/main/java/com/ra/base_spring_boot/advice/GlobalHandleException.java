package com.ra.base_spring_boot.advice;

import com.ra.base_spring_boot.exception.*;
import com.ra.base_spring_boot.dto.ResponseWrapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalHandleException {
    private ResponseEntity<?> build(HttpStatus status, Object data) {
        return ResponseEntity.status(status).body(
                ResponseWrapper.builder()
                        .data(data)
                        .code(status.value())
                        .status(status)
                        .build()
        );
    }

    /**
     * @param ex UsernameNotFoundException
     * @apiNote handle username not found exception
     * */
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<?> handleUsernameNotFoundException(UsernameNotFoundException ex)
    {
        return build(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    /**
     * @param ex HttpBadRequest
     * @apiNote handle exception bad request (400)
     * */
    @ExceptionHandler(HttpBadRequest.class)
    public ResponseEntity<?> handleHttpBadRequest(HttpBadRequest ex)
    {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    /**
     * @param ex HttpUnAuthorized
     * @apiNote handle exception unauthorized (401)
     * */
    @ExceptionHandler(HttpUnAuthorized.class)
    public ResponseEntity<?> handleHttpUnAuthorized(HttpUnAuthorized ex)
    {
        return build(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    /**
     * @param ex HttpForbiden
     * @apiNote handle exception forbiden (403)
     * */
    @ExceptionHandler(HttpForbidden.class)
    public ResponseEntity<?> handleHttpForbidden(HttpForbidden ex)
    {
        return build(HttpStatus.FORBIDDEN, ex.getMessage());
    }

    /**
     * @param ex HttpNotFound
     * @apiNote handle exception not found (404)
     * */
    @ExceptionHandler(HttpNotFound.class)
    public ResponseEntity<?> handleHttpNotFound(HttpNotFound ex)
    {
        return build(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    /**
     * @param ex HttpConflict
     * @apiNote handle exception conflict (409)
     * */
    @ExceptionHandler(HttpConflict.class)
    public ResponseEntity<?> handleHttpConflict(HttpConflict ex)
    {
        return build(HttpStatus.CONFLICT, ex.getMessage());
    }


}
