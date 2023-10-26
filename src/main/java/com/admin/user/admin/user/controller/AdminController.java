package com.admin.user.admin.user.controller;

import com.admin.user.admin.user.common.ApiResponse;
import com.admin.user.admin.user.dto.AddInfoDto;
import com.admin.user.admin.user.dto.UpdateDto;
import com.admin.user.admin.user.entity.User;
import com.admin.user.admin.user.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    //addInfo
    @PostMapping("/add")
    public ResponseEntity<ApiResponse> addInfo(@RequestBody AddInfoDto addInfoDto){
        return adminService.addInfo(addInfoDto);
    }

    //get
    @GetMapping("/get")
    public ResponseEntity<ApiResponse> getInfo(){
        return adminService.getInfo();
    }

    //update
    @PutMapping("/update")
    public ResponseEntity<ApiResponse> updateInfo(@RequestBody UpdateDto updateDto){
        return adminService.updateInfo(updateDto);
    }

    //delete
    @DeleteMapping("/delete")
    public ResponseEntity<ApiResponse> delete(){
        return adminService.delete();
    }

    //getAll
    @GetMapping("/get/all")
    public List<User> getAll(){
        return adminService.getAll();
    }
}
