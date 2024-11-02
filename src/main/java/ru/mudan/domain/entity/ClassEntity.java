package ru.mudan.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

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
    private Character letter;
    @Column(name = "number")
    private Integer number;
    @Column(name = "description")
    private String description;
    @OneToMany
    private List<Subject> subjects;

    public ClassEntity(Character letter, Integer number, String description) {
        this.letter = letter;
        this.number = number;
        this.description = description;
    }
}