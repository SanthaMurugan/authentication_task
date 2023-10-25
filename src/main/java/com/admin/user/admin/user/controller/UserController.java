package com.admin.user.admin.user.controller;


import com.admin.user.admin.user.common.ApiResponse;
import com.admin.user.admin.user.dto.AddInfoDto;
import com.admin.user.admin.user.dto.LoginDto;
import com.admin.user.admin.user.dto.SignupDto;
import com.admin.user.admin.user.dto.UpdateDto;
import com.admin.user.admin.user.entity.User;
import com.admin.user.admin.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    //signup
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse> signup(@RequestBody SignupDto signupDto) throws Exception {
        return userService.signup(signupDto);
    }

    //login
    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@RequestBody LoginDto loginDto) throws Exception {
        return userService.login(loginDto);
    }

    //addInfo
    @PostMapping("/add")
    public ResponseEntity<ApiResponse> addInfo(@RequestBody AddInfoDto addInfoDto){
        return userService.addInfo(addInfoDto);
    }

    //get
    @GetMapping("/get")
    private ResponseEntity<ApiResponse> getInfo(){
        return userService.getInfo();
    }

    //update
    @PutMapping("/update")
    public ResponseEntity<ApiResponse> updateInfo(@RequestBody UpdateDto updateDto){
        return userService.updateInfo(updateDto);
    }

    //delete
    @DeleteMapping("/delete")
    public ResponseEntity<ApiResponse> delete(){
        return userService.delete();
    }

    //getAll
    @GetMapping("/get/all")
    public List<User> getALL(){
        return userService.getALL();
    }


}
