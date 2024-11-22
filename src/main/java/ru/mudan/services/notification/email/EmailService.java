package ru.mudan.services.notification.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.io.File;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import ru.mudan.exceptions.NotificationException;
import ru.mudan.services.notification.NotificationService;

/**
 * Класс с бизнес-логикой отправки писем на почту
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService implements NotificationService<EmailNotificationDetails> {

    private final JavaMailSender javaMailSender;

    /**
     * Метод для отправки письма на почту
     *
     * @param emailDetails - данные для отправки на почту
     */
    @Override
    public void sendNotification(EmailNotificationDetails emailDetails) {
        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            createMimeMessageHelper(message, emailDetails);
        } catch (MessagingException e) {
            throw new NotificationException();
        }

        try {
            javaMailSender.send(message);
            log.info("Email sent successfully to {}", emailDetails.recipient());
        } catch (MailException e) {
            log.error("Email sent error to {}", emailDetails.recipient(), e);
        }
    }

    /**
     * Метод для настройки объекта MimeMessageHelper для отправки письма
     *
     * @param message - сообщение для отправки
     * @param emailDetails - данные для отправки
     */
    private void createMimeMessageHelper(MimeMessage message, EmailNotificationDetails emailDetails)
            throws MessagingException {
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setTo(emailDetails.recipient());
        helper.setSubject(emailDetails.subject());
        helper.setText(emailDetails.text(), emailDetails.text());

        if (emailDetails.attachments() != null) {
            for (File attachment : emailDetails.attachments()) {
                helper.addAttachment(attachment.getName(), attachment);
            }
        }
    }
}
