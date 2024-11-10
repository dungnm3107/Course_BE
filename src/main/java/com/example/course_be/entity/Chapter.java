package com.example.course_be.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Where;

import java.util.List;

@Entity
@Table(name = "chapter")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Chapter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chapter_id")
    private Long id;

    @Column(name = "chapter_title")
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "chapter_sequence")
    private Integer chapterSequence;

    @Column(name = "deleted", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean deleted = (Boolean) false;

    @JsonManagedReference
    @OneToMany(mappedBy = "chapter", cascade = CascadeType.ALL)
    @Where(clause = "deleted = false")
    private List<Lesson> lessons;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;
}
