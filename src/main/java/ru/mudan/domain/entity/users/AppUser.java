package ru.mudan.domain.entity.users;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.mudan.domain.entity.users.enums.Role;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity
@Table(name = "app_users")
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(name = "user_id")
    private Long userId;
    @Column(name = "lastname")
    private String lastname;
    @Column(name = "role_name", columnDefinition = "VARCHAR")
    private Role roleName;
    @Column(name = "email")
    private String email;

    public AppUser(Long userId, String lastname, Role roleName, String email) {
        this.userId = userId;
        this.lastname = lastname;
        this.roleName = roleName;
        this.email = email;
    }
}
