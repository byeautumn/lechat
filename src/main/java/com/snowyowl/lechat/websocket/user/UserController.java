package com.snowyowl.lechat.websocket.user;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RestController; // Import RestController
import jakarta.servlet.http.HttpSession;

@RestController // Use RestController instead of Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    @MessageMapping("/user.registerUser")
    @SendTo("/topic/public")
    public void registerUser(@Payload User user) {
        service.registerUser(user);
    }

    @MessageMapping("/user.disconnectUser")
    @SendTo("/topic/public")
    public void disconnectUser(@Payload User user) {
        service.updateUserStatus(user.getUsername(), Status.OFFLINE);
    }

    // REMOVE THIS METHOD:
    // @GetMapping("/api/user/role")
    // public String getUserRole(HttpSession session) {
    //     String username = (String) session.getAttribute("username");
    //     if (username == null) {
    //         return "GUEST";
    //     }
    //     User user = service.findByUsername(username).orElse(null); // Use UserService
    //     if (user != null) {
    //         return user.getRole();
    //     }
    //     return "GUEST";
    // }
}