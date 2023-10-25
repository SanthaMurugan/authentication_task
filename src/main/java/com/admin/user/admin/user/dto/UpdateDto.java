package com.admin.user.admin.user.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class UpdateDto {

    private String emailID;
    private Integer age;
    private String gender;
    private String address;
}
