package com.example.course_be.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "lessons")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Lesson implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lessons_id")
    private Long id;

    @Column(name = "lessons_title")
    private String title;

    @Column(name = "lessons_content")
    private String content;

    @Column(name = "created_at" , updatable = false , nullable = false)
    @CreationTimestamp
    private Date createdAt;

    @Column(name = "update_at")
    @UpdateTimestamp
    private Date updateAt;

    @Column(name = "create_by")
    private String createBy;

    @Column(name = "update_by")
    private String updateBy;

    @Column(name = "lessons_cover")
    private String cover;

    @Column(name = "lessons_vid_url")
    private String videoUrl;

    @Column(name = "lessons_sequence")
    private Integer lessonSequence;

    @Column(name = "deleted", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean deleted = (Boolean) false;


    @OneToMany(mappedBy = "lesson", cascade = CascadeType.ALL)
    private List<FavoriteLesson> favoriteLessons;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "chapter_id", nullable = false)
    private Chapter chapter;

}
