package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String root() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login-register";
    }

    @PostMapping("/register")
    public String registerUser(
            @RequestParam("firstName") String firstName,
            @RequestParam("lastName") String lastName,
            @RequestParam("email") String email,
            @RequestParam("phone") String phone,
            @RequestParam("password") String password,
            HttpSession session,
            Model model) {

        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPhone(phone);
        user.setPassword(password);

        String result = userService.registerUser(user);
        if (result.equals("EMAIL_EXISTS")) {
            model.addAttribute("registerError", "இந்த Email already registered!");
            model.addAttribute("activeTab", "register");
            return "login-register";
        }

        User savedUser = userService.findByEmail(email);
        session.setAttribute("user", savedUser);
        return "redirect:/main";
    }

    @PostMapping("/login")
    public String loginUser(
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            HttpSession session,
            Model model) {

        String result = userService.loginUser(email, password);
        if (result.equals("SUCCESS")) {
            User user = userService.findByEmail(email);
            session.setAttribute("user", user);
            return "redirect:/main";
        }

        model.addAttribute("loginError", "Invalid email or password!");
        model.addAttribute("activeTab", "login");
        return "login-register";
    }

    @GetMapping("/main")
    public String dashboard(HttpSession session, Model model) {

        User user = (User) session.getAttribute("user");

        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute("user", user);
        return "main";
    }
    @GetMapping("/like")
    public String likePage(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";
        model.addAttribute("user", user);
        return "like";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
   
}
