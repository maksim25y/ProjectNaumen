package ru.mudan.services.notification.email;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.io.File;
import java.util.List;
import lombok.Builder;
import ru.mudan.services.notification.NotificationDetails;

@Builder
public record EmailNotificationDetails(
    @NotBlank(message = "Отправитель не должен быть пустым")
    @Email(message = "Адрес электронной почты должен иметь корректный формат")
    String recipient,

    @NotBlank(message = "Тема письма должна быть указана")
    @Size(max = 255, message = "Тема письма должна быть менее 255 символов")
    String subject,

    @NotBlank(message = "Текст не должен быть пустым")
    String text,

    List<File> attachments
) implements NotificationDetails {
}
