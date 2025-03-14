package com.snowyowl.lechat.websocket.user;

import lombok.RequiredArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import java.util.Optional;
import java.util.Enumeration;

@Controller
@RequiredArgsConstructor
public class LoginController {

    private final UserRepository userRepository;

    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password, HttpSession session) {
        Optional<User> userOptional = userRepository.findByUsername(username);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (BCrypt.checkpw(password, user.getPassword())) {
                // Set session attributes
                session.setAttribute("authenticated", true);
                session.setAttribute("username", username);
                session.setAttribute("userRole", user.getRole());

                // Add logging to verify session attributes
                System.out.println("Session ID after login: " + session.getId());
                Enumeration<String> attributeNames = session.getAttributeNames();
                while (attributeNames.hasMoreElements()) {
                    String attributeName = attributeNames.nextElement();
                    System.out.println("Session Attribute: " + attributeName + " = " + session.getAttribute(attributeName));
                }

                return "redirect:/announcements";
            } else {
                return "redirect:/login?error=Invalid password";
            }
        } else {
            return "redirect:/login?error=Invalid username";
        }
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}