package com.mentorconnect.backend.domains.communication;

import com.mentorconnect.backend.domains.user.User;
import com.mentorconnect.backend.domains.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommunicationService {

    private final MessageRepository messageRepository;
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @Transactional
    public Message sendMessage(User sender, SendMessageRequest request) {
        User receiver = userRepository.findById(request.receiverId())
                .orElseThrow(() -> new RuntimeException("Receiver user not found"));

        Message message = Message.builder()
                .sender(sender)
                .receiver(receiver)
                .content(request.content())
                .isRead(false)
                .build();

        return messageRepository.save(message);
    }

    @Transactional(readOnly = true)
    public List<Message> getConversation(UUID user1, UUID user2) {
        return messageRepository.findConversationBetween(user1, user2);
    }

    @Transactional
    public Notification createNotification(CreateNotificationRequest request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Notification notification = Notification.builder()
                .user(user)
                .type(request.type())
                .title(request.title())
                .message(request.message())
                .isRead(false)
                .build();

        return notificationRepository.save(notification);
    }

    @Transactional(readOnly = true)
    public List<Notification> getUserNotifications(UUID userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @Transactional
    public void markNotificationAsRead(UUID notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        notification.setIsRead(true);
        notificationRepository.save(notification);
    }
}