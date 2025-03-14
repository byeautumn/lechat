package com.snowyowl.lechat.websocket.announcement; // Adjust package name as needed


import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnnouncementRepository extends MongoRepository<Announcement, String> {
}