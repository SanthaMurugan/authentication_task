package com.admin.user.admin.user.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class SignupDto {

    private String username;
    private String password;
    private String emailID;
    private String role;
}
