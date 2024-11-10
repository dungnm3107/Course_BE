package com.example.course_be.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "lesson_watch_history")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LessonWatchHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;


    @Column(name = "user_id", nullable = false)
    private Long userId;

    @ManyToOne
    @JoinColumn(name = "lesson_id", nullable = false)
    private Lesson lesson;

    // Thời gian người dùng đã xem tới (tính bằng giây)
    @Column(name = "last_watched_time")
    private Long lastWatchedTime;

    @Column(name = "last_watched_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastWatchedAt;
}