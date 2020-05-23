package com.auth.authserver.controller;

import com.auth.authserver.entity.Login;
import com.auth.authserver.mapper.LoginMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashSet;
import java.util.Set;

@Controller
public class LoginController {

    private static Set<String> userSet = new HashSet<>();

    @Autowired
    LoginMapper loginMapper;

    @GetMapping("/login")
    @ResponseBody
    public String login(String username, String password){
        Login login = loginMapper.selectByPrimaryKey(username);
        userSet.add(username);
        return login != null && login.getPassword().equals(password) ? "1" : "0";
    }

    @GetMapping("/loginCheck")
    @ResponseBody
    public String checkLogin(String username){
        return userSet.contains(username) ? "1" : "0";
    }

    @GetMapping("/logout")
    @ResponseBody
    public String logout(String username){
        return userSet.remove(username) ? "1" : "0";
    }
}
