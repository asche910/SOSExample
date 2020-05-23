package com.auth.server.controller;


import com.auth.server.util.HttpUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String login(){
        return "login";
    }

    @PostMapping("/login")
    public String login(String username, String password,
                        HttpServletRequest request, Model model) throws IOException {
        String url = String.format("http://localhost:9090/login?username=%s&password=%s", username, password);
        String responseContent = HttpUtils.getResponseContent(url);
        System.out.println("login:" + responseContent);
        if("1".equals(responseContent)){
            request.getSession().setAttribute("user", username);
            return "redirect:home";
        }
        model.addAttribute("msg", "login failed!");
        return "login";
    }

    @RequestMapping("/addCookie")
    @ResponseBody
    public String addCookie(String cookie, HttpServletResponse response){
        response.addCookie(new Cookie("user", cookie));
        return "1";
    }


    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        Object user = session.getAttribute("user");
        System.out.println(user);
        if (user != null){
            String url = String.format("http://localhost:9090/logout?username=%s", user.toString());
            HttpUtils.getResponseContent(url);
            session.setAttribute("user", null);
        }
        return "redirect:home";
    }
}
