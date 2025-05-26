package br.com.Blog.api.DTOs;

import br.com.Blog.api.entities.Notification;
import br.com.Blog.api.entities.enums.StatusNotification;

public record NotificationDto(
        String title,
        String message,
        StatusNotification status
) {
    public Notification mappearToNotification() {
        Notification not = new Notification();

        not.setTitle(title);
        not.setMessage(message);
        not.setStatus(status);

        return not;
    }
}
