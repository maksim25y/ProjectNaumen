package ru.mudan.domain.entity;

import jakarta.persistence.*;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "schedules")
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "day_of_week")
    private Integer dayOfWeek;
    @Column(name = "start_time", columnDefinition = "TIMESTAMP")
    private LocalTime startTime;

    @Column(name = "number_of_classroom")
    private Integer numberOfClassroom;
    @ManyToOne
    @JoinColumn(name = "class_id")
    private ClassEntity classEntity;
    @ManyToOne
    @JoinColumn(name = "subject_id")
    private Subject subject;

    public Schedule(Integer dayOfWeek,
                    LocalTime startTime,
                    Integer numberOfClassroom,
                    ClassEntity classEntity,
                    Subject subject) {
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.numberOfClassroom = numberOfClassroom;
        this.classEntity = classEntity;
        this.subject = subject;
    }
}
