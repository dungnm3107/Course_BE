package com.example.course_be.entity;

import com.example.course_be.enums.Gender;
import jakarta.persistence.*;
import lombok.*;
import net.minidev.json.annotate.JsonIgnore;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "user")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private Gender gender;

    @Column(name = "avatar")
    private String avatar;

    @Column(name="phone")
    private String phone;

    @Column(name="facebook_id")
    private String facebookId;

    @Column(name="google_id")
    private String googleId;

    @Column(name = "status", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean isDeleted = (Boolean) false;

    @Column(name = "created_date" , updatable = false , nullable = false)
    @CreationTimestamp
    private Date createdDate;

    @Column(name = "updated_date")
    @UpdateTimestamp
    private Date updatedDate;

    @ManyToMany(fetch = FetchType.EAGER , cascade = {
            CascadeType.DETACH,
            CascadeType.MERGE,
            CascadeType.PERSIST,
            CascadeType.REFRESH
    })

    @JoinTable(name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
//    @JsonIgnore
    private List<Role> listRoles;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<FavoriteLesson> favoriteLessons;


    @OneToMany(mappedBy = "user")
    private List<UserCourse> userCourses;

}
