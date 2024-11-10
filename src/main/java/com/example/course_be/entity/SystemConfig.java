package com.example.course_be.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "system_config")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SystemConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "setting_name")
    private String settingName;

    @Column(name = "setting_value")
    private String settingValue;


    @Column(name = "deleted")
    private String deleted;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

}
