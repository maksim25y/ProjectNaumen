package ru.mudan.domain.entity.users;

import jakarta.persistence.*;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ru.mudan.domain.entity.users.enums.Role;

/**
 * Сущность для работы с таблицей parents в БД
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity
@Table(name = "parents")
public class Parent implements UserDetails {

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
    @OneToMany(mappedBy = "parent")
    private List<Student> students;

    public Parent(String firstname, String lastname, String patronymic, String email, String hashedPassword) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.patronymic = patronymic;
        this.email = email;
        this.hashedPassword = hashedPassword;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList((GrantedAuthority) Role.ROLE_PARENT::toString);
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
