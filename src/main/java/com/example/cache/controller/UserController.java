package com.example.cache.controller;

import com.example.cache.entity.User;
import com.example.cache.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    @GetMapping("/{id}")
    public User selectById(@PathVariable("id") @NotNull Long id){
        User user = userService.selectById(id);
        if (user == null) {
            user = new User();
        }
        return user;
    }
    @GetMapping("/selectAll")
    public List<User> selectAll(){
        List<User> users = userService.selectAll();
        return users;
    }

    @GetMapping("/delete")
    public void delete(){
        userService.delete();
    }


}
