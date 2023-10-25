package com.admin.user.admin.user.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@NoArgsConstructor

public class ApplicationException extends RuntimeException{

    private HttpStatus httpStatus;

    private String error;

    private String message;

    public ApplicationException(HttpStatus httpStatus, String s, String message) {
    }
}
