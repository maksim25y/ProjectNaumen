package ru.mudan.services.users;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mudan.domain.entity.users.*;
import ru.mudan.domain.entity.users.enums.Role;
import ru.mudan.domain.repositories.*;
import ru.mudan.dto.auth.RegisterUserDTO;
import ru.mudan.exceptions.entity.already_exists.UserAlreadyExistsException;
import ru.mudan.services.notification.email.EmailNotificationDetails;
import ru.mudan.services.notification.email.EmailService;

/**
 * Класс с описанием бизнес-логики
 * для работы с регистрацией новых пользователей
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class RegistrationService {

    private final EmailService emailService;
    private final AdminRepository adminRepository;
    private final ParentRepository parentRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final ExecutorService executorService = Executors.newFixedThreadPool(10);

    /**
     * Метод для регистрации администратора
     *
     * @param registerUserDTO - входные данные для регистрации
     */
    public void registerAdmin(RegisterUserDTO registerUserDTO) {
        log.info("Started creating admin with email {}", registerUserDTO.email());
        checkUserExists(registerUserDTO.email());

        var admin = new Admin(registerUserDTO.firstname(),
                registerUserDTO.lastname(),
                registerUserDTO.patronymic(),
                registerUserDTO.email(),
                encodePassword(registerUserDTO.password()));

        var savedAdmin = adminRepository.save(admin);

        var appUser = getAppUserByRoleUserIdAndEmail(savedAdmin.getId(), Role.ROLE_ADMIN, savedAdmin.getEmail());

        appUserRepository.save(appUser);
        executorService.submit(() -> sendMessage(registerUserDTO));
        log.info("Finished creating admin with email {}", registerUserDTO.email());
    }

    /**
     * Метод для регистрации учителя
     *
     * @param registerUserDTO - входные данные для регистрации
     */
    public void registerTeacher(RegisterUserDTO registerUserDTO) {
        log.info("Started creating teacher with email {}", registerUserDTO.email());
        checkUserExists(registerUserDTO.email());

        var teacher = new Teacher(registerUserDTO.firstname(),
                registerUserDTO.lastname(),
                registerUserDTO.patronymic(),
                registerUserDTO.email(),
                encodePassword(registerUserDTO.password()));

        var savedTeacher = teacherRepository.save(teacher);

        var appUser = getAppUserByRoleUserIdAndEmail(savedTeacher.getId(), Role.ROLE_TEACHER, savedTeacher.getEmail());

        appUserRepository.save(appUser);
        executorService.submit(() -> sendMessage(registerUserDTO));
        log.info("Finished creating teacher with email {}", registerUserDTO.email());
    }

    /**
     * Метод для регистрации родителя
     *
     * @param registerUserDTO - входные данные для регистрации
     */
    public void registerParent(RegisterUserDTO registerUserDTO) {
        log.info("Started creating parent with email {}", registerUserDTO.email());
        checkUserExists(registerUserDTO.email());

        var parent = new Parent(
                registerUserDTO.firstname(),
                registerUserDTO.lastname(),
                registerUserDTO.patronymic(),
                registerUserDTO.email(),
                encodePassword(registerUserDTO.password()));

        var savedParent = parentRepository.save(parent);

        if (registerUserDTO.studentsIds() != null) {
            registerUserDTO.studentsIds().forEach(studentId -> {
                studentRepository.findById(studentId).ifPresent(student -> {
                    student.setParent(parent);
                });
            });
        }

        var appUser = getAppUserByRoleUserIdAndEmail(
                savedParent.getId(),
                Role.ROLE_PARENT,
                savedParent.getEmail()
        );
        appUserRepository.save(appUser);
        executorService.submit(() -> sendMessage(registerUserDTO));
        log.info("Finished creating parent with email {}", registerUserDTO.email());
    }

    /**
     * Метод для регистрации ученика
     *
     * @param registerUserDTO - входные данные для регистрации
     */
    public void registerStudent(RegisterUserDTO registerUserDTO) {
        log.info("Started creating student with email {}", registerUserDTO.email());

        checkUserExists(registerUserDTO.email());

        var student = new Student(
                registerUserDTO.firstname(),
                registerUserDTO.lastname(),
                registerUserDTO.patronymic(),
                registerUserDTO.email(),
                encodePassword(registerUserDTO.password()));

        var savedStudent = studentRepository.save(student);

        var appUser = getAppUserByRoleUserIdAndEmail(
                savedStudent.getId(),
                Role.ROLE_STUDENT,
                savedStudent.getEmail()
        );

        appUserRepository.save(appUser);
        executorService.submit(() -> sendMessage(registerUserDTO));
        log.info("Finished creating student with email {}", registerUserDTO.email());
    }

    /**
     * Метод для проверки существования пользователя
     *
     * @param email - адрес электронной почты пользователя
     */
    private void checkUserExists(String email) {
        var user = appUserRepository.findByEmail(email);
        if (user.isPresent()) {
            log.info("User with email {} already exists", email);
            throw new UserAlreadyExistsException(email);
        }
    }

    /**
     * Метод для создания пользователя по userId, role, email
     *
     * @param userId - id пользователя с ролью role в таблице
     * @param role   - роль пользователя
     * @param email  - адрес электронной почты пользователя
     */
    private AppUser getAppUserByRoleUserIdAndEmail(Long userId, Role role, String email) {
        return new AppUser(userId, role, email);
    }

    /**
     * Метод для хэширования пароля
     *
     * @param password - пароль пользователя
     */
    private String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    /**
     * Метод для отправки сообщения об успешной регистрации
     *
     * @param registerUserDTO - входные данные для создания сообщения
     */
    private void sendMessage(RegisterUserDTO registerUserDTO) {
        var emailDetails = EmailNotificationDetails.builder()
                .subject("Уведомление о регистрации")
                .recipient(registerUserDTO.email())
                .text("""
                        <!DOCTYPE html>
                        <html lang="ru">
                        <head>
                          <meta charset="UTF-8">
                          <meta name="viewport" content="width=device-width, initial-scale=1.0">
                          <title>Добро пожаловать</title>
                        </head>
                        <body style="font-family: Arial, sans-serif; color: #333;">
                          <div style="max-width: 600px; margin: 0 auto; padding: 20px;
                          background-color: #f9f9f9; border: 1px solid #ddd;">
                            <h2 style="color: #0078cf;">Здравствуйте, %s!</h2>
                            <p>Вы успешно зарегистрированы на сайте электронного дневника <strong>[сайт]</strong>.</p>
                            <p><strong>Ваш email:</strong> %s</p>
                            <p><strong>Пароль:</strong> %s</p>
                            <p>Для связи с администратором нажмите <a href="[ссылка]" style="color: #0078cf;">здесь</a>.</p>
                          </div>
                        </body>
                        </html>
                        """.formatted(registerUserDTO.email(), registerUserDTO.email(), registerUserDTO.password()))
                .attachments(List.of())
                .build();

        emailService.sendNotification(emailDetails);
    }
}