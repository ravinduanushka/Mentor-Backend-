package com.mentorconnect.backend.domains.communication;

import com.mentorconnect.backend.domains.user.User;
import com.mentorconnect.backend.domains.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CommunicationController {

    private final CommunicationService communicationService;
    private final UserService userService;

    @PostMapping("/messages")
    public ResponseEntity<Message> sendMessage(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody SendMessageRequest request
    ) {
        User sender = userService.getUserByExternalAuthId(jwt.getClaimAsString("sub"));
        return ResponseEntity.ok(communicationService.sendMessage(sender, request));
    }

    @GetMapping("/messages/conversation/{otherUserId}")
    public ResponseEntity<List<Message>> getConversation(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID otherUserId
    ) {
        User currentUser = userService.getUserByExternalAuthId(jwt.getClaimAsString("sub"));
        return ResponseEntity.ok(communicationService.getConversation(currentUser.getId(), otherUserId));
    }

    @GetMapping("/notifications")
    public ResponseEntity<List<Notification>> getNotifications(@AuthenticationPrincipal Jwt jwt) {
        User user = userService.getUserByExternalAuthId(jwt.getClaimAsString("sub"));
        return ResponseEntity.ok(communicationService.getUserNotifications(user.getId()));
    }

    @PatchMapping("/notifications/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable UUID id) {
        communicationService.markNotificationAsRead(id);
        return ResponseEntity.ok().build();
    }
}