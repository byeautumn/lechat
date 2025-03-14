package com.snowyowl.lechat.websocket.user;

import com.snowyowl.lechat.websocket.view.RegisterView;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @GetMapping("/register")
    public String registerPage() {
        return "forward:/register.html";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute RegisterView registerView, HttpSession session) {
        try {
            User user = new User(registerView.getUsername(), registerView.getPassword(), registerView.getRole());
            userService.registerUser(user);
            // Automatically log in the user after successful registration
            session.setAttribute("authenticated", true);
            session.setAttribute("username", user.getUsername());
            return "redirect:/announcements"; // Redirect to announcements after registration
        } catch (IllegalArgumentException e) {
            return "redirect:/register?error=" + e.getMessage();
        }
    }

    // ... RegisterView class ...
}