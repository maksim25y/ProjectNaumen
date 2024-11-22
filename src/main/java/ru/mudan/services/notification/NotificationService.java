package ru.mudan.services.notification;

/**
 * Интерфейс сервиса для отправки писем на почту
 */
public interface NotificationService<T extends NotificationDetails> {
    void sendNotification(T notificationDetails);
}
