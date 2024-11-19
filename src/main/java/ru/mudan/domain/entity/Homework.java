package ru.mudan.domain.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Сущность для работы с таблицей homeworks в БД
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity
@Table(name = "homeworks")
public class Homework {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "title")
    private String title;
    @Column(name = "description")
    private String description;
    @Column(name = "deadline", columnDefinition = "DATETIME")
    private LocalDate deadline;
    @ManyToOne
    @JoinColumn(name = "class_id")
    private ClassEntity classEntity;
    @ManyToOne
    @JoinColumn(name = "subject_id")
    private Subject subject;

    public Homework(String title, String description, LocalDate deadline) {
        this.title = title;
        this.description = description;
        this.deadline = deadline;
    }
}
