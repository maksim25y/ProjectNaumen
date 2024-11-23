package ru.mudan.domain.entity;

import jakarta.persistence.*;
import java.util.List;
import lombok.*;
import ru.mudan.domain.entity.users.Teacher;

/**
 * Сущность для работы с таблицей classes в БД
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"grades", "homeworks", "schedules"})
@Entity
@Table(name = "subjects")
public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name")
    private String name;
    @Column(name = "type")
    private String type;
    @Column(name = "code", unique = true)
    private String code;
    @Column(name = "description")
    private String description;
    @ManyToOne
    @JoinColumn(name = "class_id")
    private ClassEntity classEntity;
    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;
    @OneToMany(mappedBy = "subject")
    private List<Grade> grades;
    @OneToMany(mappedBy = "subject")
    private List<Homework> homeworks;
    @OneToMany(mappedBy = "subject")
    private List<Schedule> schedules;

    public Subject(String name, String type, String code, String description) {
        this.name = name;
        this.type = type;
        this.code = code;
        this.description = description;
    }
}
