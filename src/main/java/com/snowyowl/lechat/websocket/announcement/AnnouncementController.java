package com.snowyowl.lechat.websocket.announcement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;


@RestController
@RequestMapping("/api/announcements")
public class AnnouncementController {

    @Autowired
    private AnnouncementRepository announcementRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @GetMapping
    public List<Announcement> getAnnouncements() {
        return announcementRepository.findAll();
    }

    @PostMapping
    public void postAnnouncement(@RequestBody Announcement announcement) {
        announcement.setTimestamp(LocalDateTime.now().toString());
        announcementRepository.save(announcement);
        messagingTemplate.convertAndSend("/topic/announcements", announcementRepository.findAll());
    }

    @DeleteMapping("/{id}")
    public void deleteAnnouncement(@PathVariable String id) {
        announcementRepository.deleteById(id);
        messagingTemplate.convertAndSend("/topic/announcements", announcementRepository.findAll());
    }
}