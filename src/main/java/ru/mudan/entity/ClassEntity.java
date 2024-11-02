package ru.mudan.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity
@Table(name = "classes")
public class ClassEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(name = "letter")
    private Character letter;
    @Column(name = "number")
    private Integer number;
    @Column(name = "description")
    private String description;

    public ClassEntity(Character letter, Integer number, String description) {
        this.letter = letter;
        this.number = number;
        this.description = description;
    }
}