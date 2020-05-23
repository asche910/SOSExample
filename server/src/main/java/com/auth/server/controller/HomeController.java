package com.auth.server.controller;

import com.auth.server.util.HttpUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Controller
public class HomeController {

    @RequestMapping(value = {"/", "/home"})
    public String home(HttpServletRequest request, Model model) throws IOException {
        Object user = request.getSession().getAttribute("user");
        System.out.println(user);

        if (user == null) {
            Cookie[] cookies = request.getCookies();
            for (Cookie cookie : cookies) {
                if ("user".equals(cookie.getName())) {
                    user = cookie.getValue();

                    String url = String.format("http://localhost:9090/loginCheck?username=%s", user.toString());
                    String responseContent = HttpUtils.getResponseContent(url);
                    if("1".equals(responseContent)){
                        request.getSession().setAttribute("user", user);
                    }else user = null;
                    break;
                }
            }
        }

        if (user == null) {
            model.addAttribute("user", "游客");
        } else {
            model.addAttribute("user", user);
            model.addAttribute("login", new Object());
        }
        return "home";
    }
}
