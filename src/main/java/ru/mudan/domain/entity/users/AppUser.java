package ru.mudan.domain.entity.users;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.mudan.util.enums.Role;

/**
 * Сущность для работы с таблицей app_users в БД
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity
@Table(name = "app_users")
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "user_id")
    private Long userId;
    @Enumerated(EnumType.STRING)
    @Column(name = "role_name")
    private Role roleName;
    @Column(name = "email")
    private String email;

    public AppUser(Long userId, Role roleName, String email) {
        this.userId = userId;
        this.roleName = roleName;
        this.email = email;
    }
}
