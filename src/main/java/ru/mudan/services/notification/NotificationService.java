package ru.mudan.services.notification;

public interface NotificationService<T extends NotificationDetails> {
    void sendNotification(T notificationDetails);
}
