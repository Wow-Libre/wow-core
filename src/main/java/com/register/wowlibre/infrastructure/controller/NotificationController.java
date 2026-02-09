package com.register.wowlibre.infrastructure.controller;

import com.register.wowlibre.domain.dto.notifications.NotificationAdminDto;
import com.register.wowlibre.domain.dto.notifications.NotificationDto;
import com.register.wowlibre.domain.dto.notifications.NotificationRequestDto;
import com.register.wowlibre.domain.port.in.notifications.NotificationPort;
import com.register.wowlibre.domain.shared.GenericResponse;
import com.register.wowlibre.domain.shared.GenericResponseBuilder;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.register.wowlibre.domain.constant.Constants.HEADER_TRANSACTION_ID;
import static com.register.wowlibre.domain.constant.Constants.HEADER_USER_ID;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationPort notificationPort;

    public NotificationController(NotificationPort notificationPort) {
        this.notificationPort = notificationPort;
    }

    /** List notifications for the authenticated user. Default: unread only (so they "disappear" after read). */
    @GetMapping
    public ResponseEntity<GenericResponse<List<NotificationDto>>> listForUser(
            @RequestHeader(name = HEADER_TRANSACTION_ID, required = false) String transactionId,
            @RequestHeader(name = HEADER_USER_ID) Long userId,
            @RequestParam(name = "unreadOnly", defaultValue = "true") boolean unreadOnly) {
        List<NotificationDto> list = notificationPort.findByUserId(userId, unreadOnly, transactionId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new GenericResponseBuilder<>(list, transactionId).ok().build());
    }

    /** Mark one notification as read (so it disappears from unread list). */
    @PatchMapping("/{id}/read")
    public ResponseEntity<GenericResponse<Void>> markAsRead(
            @RequestHeader(name = HEADER_TRANSACTION_ID, required = false) String transactionId,
            @RequestHeader(name = HEADER_USER_ID) Long userId,
            @PathVariable Long id) {
        notificationPort.markAsRead(id, userId, transactionId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new GenericResponseBuilder<Void>(transactionId).ok().build());
    }

    /** Mark all notifications of the user as read. */
    @PostMapping("/mark-all-read")
    public ResponseEntity<GenericResponse<Void>> markAllAsRead(
            @RequestHeader(name = HEADER_TRANSACTION_ID, required = false) String transactionId,
            @RequestHeader(name = HEADER_USER_ID) Long userId) {
        notificationPort.markAllAsRead(userId, transactionId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new GenericResponseBuilder<Void>(transactionId).ok().build());
    }

    // ---------- Admin ----------

    @GetMapping("/admin/list")
    public ResponseEntity<GenericResponse<List<NotificationAdminDto>>> adminList(
            @RequestHeader(name = HEADER_TRANSACTION_ID, required = false) String transactionId) {
        List<NotificationAdminDto> list = notificationPort.findAllForAdmin(transactionId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new GenericResponseBuilder<>(list, transactionId).ok().build());
    }

    @PostMapping("/admin")
    public ResponseEntity<GenericResponse<NotificationAdminDto>> adminCreate(
            @RequestHeader(name = HEADER_TRANSACTION_ID, required = false) String transactionId,
            @RequestBody @Valid NotificationRequestDto request) {
        NotificationAdminDto created = notificationPort.create(request, transactionId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new GenericResponseBuilder<>(created, transactionId).created().build());
    }

    @PutMapping("/admin")
    public ResponseEntity<GenericResponse<NotificationAdminDto>> adminUpdate(
            @RequestHeader(name = HEADER_TRANSACTION_ID, required = false) String transactionId,
            @RequestBody @Valid NotificationRequestDto request) {
        NotificationAdminDto updated = notificationPort.update(request, transactionId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new GenericResponseBuilder<>(updated, transactionId).ok().build());
    }

    @DeleteMapping("/admin/{id}")
    public ResponseEntity<GenericResponse<Void>> adminDelete(
            @RequestHeader(name = HEADER_TRANSACTION_ID, required = false) String transactionId,
            @PathVariable Long id) {
        notificationPort.deleteById(id, transactionId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new GenericResponseBuilder<Void>(transactionId).ok().build());
    }
}
