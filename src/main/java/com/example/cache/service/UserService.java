package com.example.cache.service;

import com.example.cache.entity.User;

import java.util.List;

public interface UserService {

    User selectById(long id);
    List<User> selectAll();
    void delete();

}
