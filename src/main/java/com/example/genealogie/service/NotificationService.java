package com.example.genealogie.service;

import com.example.genealogie.dto.NotificationDto;
import com.example.genealogie.model.User;

import java.util.List;

public interface NotificationService {
    void notify(User user, String message);
    List<NotificationDto> getNotifications(User user);
    long countUnread(User user);
    void markAllRead(User user);
}
