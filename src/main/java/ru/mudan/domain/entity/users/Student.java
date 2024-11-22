package ru.mudan.domain.entity.users;

import jakarta.persistence.*;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ru.mudan.domain.entity.ClassEntity;
import ru.mudan.domain.entity.Grade;
import ru.mudan.util.enums.Role;

/**
 * Сущность для работы с таблицей students в БД
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@EqualsAndHashCode(exclude = "grades")
@Entity
@Table(name = "students")
public class Student implements UserDetails {

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
    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Parent parent;
    @ManyToOne
    @JoinColumn(name = "class_id")
    private ClassEntity classEntity;
    @OneToMany(mappedBy = "student")
    private List<Grade> grades;

    public Student(String firstname, String lastname, String patronymic, String email, String hashedPassword) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.patronymic = patronymic;
        this.email = email;
        this.hashedPassword = hashedPassword;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList((GrantedAuthority) Role.ROLE_STUDENT::toString);
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