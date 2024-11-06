package ru.mudan.domain.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.mudan.domain.entity.users.Student;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity
@Table(name = "grades")
public class Grade {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(name = "mark")
    private Integer mark;
    @Column(name = "date_of_mark", columnDefinition = "DATE")
    private LocalDate dateOfMark;
    @Column(name = "comment")
    private String comment;
    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;
    @ManyToOne
    @JoinColumn(name = "subject_id")
    private Subject subject;

    public Grade(Integer mark, LocalDate dateOfMark, String comment) {
        this.mark = mark;
        this.dateOfMark = dateOfMark;
        this.comment = comment;
    }
}
