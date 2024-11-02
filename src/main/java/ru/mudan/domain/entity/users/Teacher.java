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
import ru.mudan.domain.entity.Subject;
import ru.mudan.domain.entity.users.enums.Role;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity
@Table(name = "teachers")
public class Teacher implements UserDetails {

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
    @OneToMany(mappedBy = "teacher")
    private List<Subject> subjects;

    public Teacher(String firstname, String lastname, String patronymic, String email, String hashedPassword) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.patronymic = patronymic;
        this.email = email;
        this.hashedPassword = hashedPassword;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList((GrantedAuthority) Role.ROLE_TEACHER::toString);
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
