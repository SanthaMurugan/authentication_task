package com.admin.user.admin.user.common;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Getter
@NoArgsConstructor
@Setter
@Component
public class ApiResponse {

    private Object data;
    private Object status;
    private String message;

    public ApiResponse(Object status, String message,Object data) {
        this.data = data;
        this.status = status;
        this.message = message;
    }

    public ApiResponse(Object status, String message) {
        this.status = status;
        this.message = message;
    }

}
