package com.example.cache.service.impl;

import com.example.cache.entity.User;
import com.example.cache.service.UserService;
import com.example.cache.util.NeedCacheAop;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    public final static Map<String, User> map = new HashMap<>();

    static {
        User user = null;
        for (int i = 0; i < 10; i++) {
            user = new User();
            user.setId((long) i + 100);
            user.setName("ewa" + i);
            map.put(String.valueOf(user.getId()), user);
        }

    }

    @Override
    @NeedCacheAop
    public User selectById(long id) {
        User user = map.get(String.valueOf(id));
        log.info("--------->> user:{}", id);
        return user;
    }

    @Override
    @NeedCacheAop
    public List<User> selectAll() {
        Collection<User> values = map.values();
        List<User> list = new ArrayList<>(values);
        log.info("--------->> selectAll ");
        return list;
    }

    @Override
    @NeedCacheAop
    public void delete() {
        log.info("--------->> delete ");
    }
}
