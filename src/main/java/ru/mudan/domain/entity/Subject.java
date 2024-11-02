package ru.mudan.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.mudan.domain.entity.users.Teacher;

@Getter
@Setter
@ToString
@NoArgsConstructor
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

    public Subject(String name, String type, String description) {
        this.name = name;
        this.type = type;
        this.description = description;
    }
}
