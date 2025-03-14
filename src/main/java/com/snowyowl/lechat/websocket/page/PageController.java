package com.snowyowl.lechat.websocket.page;

import com.snowyowl.lechat.websocket.user.User;
import com.snowyowl.lechat.websocket.user.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class PageController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/announcements")
    public String announcementsPage(HttpSession session, Model model) {
        if (session.getAttribute("authenticated") != null && (boolean) session.getAttribute("authenticated")) {
            String username = (String) session.getAttribute("username");
            User user = userRepository.findByUsername(username).orElse(null);
            if (user != null) {
                System.out.println("Adding role to model for user " + username + ": " + user.getRole()); //Added logging.
                model.addAttribute("userRole", user.getRole());
                return "forward:/announcements.html";
            }
            return "redirect:/login?error=Please login to access the announcements.";
        } else {
            return "redirect:/login?error=Please login to access the announcements.";
        }
    }

    @GetMapping("/api/user/role")
    @ResponseBody
    public String getUserRole(HttpSession session) {
        if (session.getAttribute("authenticated") != null && (boolean) session.getAttribute("authenticated")) {
            String username = (String) session.getAttribute("username");
            User user = userRepository.findByUsername(username).orElse(null);
            if (user != null) {
                System.out.println("Retrieved role for user " + username + ": " + user.getRole()); // Added logging
                return user.getRole();
            }
        }
        return "";
    }
    @GetMapping("/api/user/username")
    @ResponseBody
    public String getUsername(HttpSession session) {
        if (session.getAttribute("authenticated") != null && (boolean) session.getAttribute("authenticated")) {
            return (String) session.getAttribute("username");
        }
        return "";
    }

    @GetMapping("/")
    public String mainPage() {
        return "forward:/main.html";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "forward:/login.html";
    }

    @GetMapping("/chat")
    public String chatPage(HttpSession session) {
        if (session.getAttribute("authenticated") != null && (boolean) session.getAttribute("authenticated")) {
            return "forward:/chat.html";
        } else {
            return "redirect:/login?error=Please login to access the chat.";
        }
    }

    @GetMapping("/api/teachers")
    @ResponseBody
    public List<User> getTeachers() {
        return userRepository.findAll().stream()
                .filter(user -> user.getRole() != null && user.getRole().equals("TEACHER")) // Check for null role
                .sorted((u1, u2) -> u1.getUsername().compareToIgnoreCase(u2.getUsername()))
                .collect(Collectors.toList());
    }

    @GetMapping("/api/users")
    @ResponseBody
    public List<User> getUsers() {
        return userRepository.findAll().stream()
                .sorted((u1, u2) -> u1.getUsername().compareToIgnoreCase(u2.getUsername()))
                .collect(Collectors.toList());
    }

    @GetMapping("/teacherchat")
    public String teacherChat() {
        return "forward:/teacherchat.html"; // Or just "teacherchat" if using Thymeleaf
    }
}