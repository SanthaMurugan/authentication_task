package com.admin.user.admin.user.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.*;

@Entity
@AllArgsConstructor
@Setter
@NoArgsConstructor
@Getter
@Table(name = "auth_task")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "username")
    private String username;
    @Column(name = "emailID")
    private String emailID;
    @Column(name = "password")
    private String password;
    @Column(name = "role")
    private String role;
    @Column(name = "age")
    private Integer age;
    @Column(name = "gender")
    private String gender;
    @Column(name = "address")
    private String address;

}
