package com.admin.user.admin.user.exception;


import com.admin.user.admin.user.common.ApiResponse;
import com.admin.user.admin.user.common.StringConstants;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.ArrayList;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;



@RestControllerAdvice
public class GlobalException {

    @ExceptionHandler(UserNameAlreadyExistsException.class)
    public ResponseEntity<ApiResponse> handleUserNameAlreadyExistsException(UserNameAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse(HttpStatus.CONFLICT, StringConstants.USER_NAME_ALREADY_EXISTS, new ArrayList<>()));
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ApiResponse> handleUsernameNotFoundException(UsernameNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(HttpStatus.NOT_FOUND, StringConstants.USERNAME_NOT_FOUND, new ArrayList<>()));
    }

    @ResponseStatus(INTERNAL_SERVER_ERROR)
    @ExceptionHandler({ApplicationException.class, IllegalArgumentException.class})
    public ResponseEntity<ApiResponse> applicationExceptions(ApplicationException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse(HttpStatus.BAD_REQUEST, StringConstants.INVALID_TOKEN, new ArrayList<>()));
    }

    @ExceptionHandler(TokenVerificationException.class)
    public ResponseEntity<ApiResponse> handleTokenVerificationException(TokenVerificationException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ApiResponse(HttpStatus.FORBIDDEN, StringConstants.ACCESS_DENIED, new ArrayList<>()));
    }

}
