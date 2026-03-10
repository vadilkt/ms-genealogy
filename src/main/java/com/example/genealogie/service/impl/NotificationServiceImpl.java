package com.example.genealogie.service.impl;

import com.example.genealogie.dto.NotificationDto;
import com.example.genealogie.model.Notification;
import com.example.genealogie.model.User;
import com.example.genealogie.repository.NotificationRepository;
import com.example.genealogie.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    @Override
    @Transactional
    public void notify(User user, String message) {
        notificationRepository.save(new Notification(user, message));
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDto> getNotifications(User user) {
        return notificationRepository.findByUser_IdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(n -> new NotificationDto(n.getId(), n.getMessage(), n.isRead(), n.getCreatedAt()))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public long countUnread(User user) {
        return notificationRepository.countByUser_IdAndReadFalse(user.getId());
    }

    @Override
    @Transactional
    public void markAllRead(User user) {
        notificationRepository.markAllReadByUserId(user.getId());
    }
}
