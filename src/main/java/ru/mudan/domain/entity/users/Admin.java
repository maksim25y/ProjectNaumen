package ru.mudan.domain.entity.users;

import jakarta.persistence.*;
import java.util.Collection;
import java.util.Collections;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ru.mudan.util.enums.Role;

/**
 * Сущность для работы с таблицей admins в БД
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity
@Table(name = "admins")
public class Admin implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "firstname")
    private String firstname;
    @Column(name = "lastname")
    private String lastname;
    @Column(name = "patronymic")
    private String patronymic;
    @Column(name = "email")
    private String email;
    @Column(name = "hashed_password")
    private String hashedPassword;

    public Admin(String firstname, String lastname, String patronymic, String email, String hashedPassword) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.patronymic = patronymic;
        this.email = email;
        this.hashedPassword = hashedPassword;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList((GrantedAuthority) Role.ROLE_ADMIN::toString);
    }

    @Override
    public String getPassword() {
        return hashedPassword;
    }

    @Override
    public String getUsername() {
        return email;
    }
}
