package ru.mudan.domain.entity;

import jakarta.persistence.*;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.mudan.domain.entity.users.Student;

/**
 * Сущность для работы с таблицей classes в БД
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity
@Table(name = "classes")
public class ClassEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "letter")
    private String letter;
    @Column(name = "number")
    private Integer number;
    @Column(name = "description")
    private String description;
    @OneToMany(mappedBy = "classEntity")
    private List<Subject> subjects;
    @OneToMany(mappedBy = "classEntity")
    private List<Student> students;
    @OneToMany(mappedBy = "classEntity")
    private List<Schedule> schedules;
    @OneToMany(mappedBy = "classEntity")
    private List<Homework> homeworks;

    public ClassEntity(String letter, Integer number, String description) {
        this.letter = letter;
        this.number = number;
        this.description = description;
    }
}