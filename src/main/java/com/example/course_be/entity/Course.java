package com.example.course_be.entity;

import com.example.course_be.enums.CourseType;
import com.google.api.client.json.JsonPolymorphicTypeMap;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "course")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Course implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_id")
    private Long id;

    @Column(name = "course_title")
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "course_price")
    private BigDecimal coursePrice;

    @Column(name = "course_cover")
    private String cover;

    @Column(name = "course_type")
    private CourseType courseType;

    @Column(name = "create_by")
    private String createBy;

    @Column(name = "update_by")
    private String updateBy;

    @Column(name = "course_vid_url")
    private String videoUrl;

    @Column(name = "created_at" , updatable = false , nullable = false)
    @CreationTimestamp
    private Date createdAt;

    @Column(name = "update_at")
    @UpdateTimestamp
    private Date updateAt;

    @Column(name = "deleted", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean deleted = (Boolean) false;


    @ManyToMany(fetch = FetchType.LAZY , cascade = {
            CascadeType.DETACH,
            CascadeType.MERGE,
            CascadeType.PERSIST,
            CascadeType.REFRESH
    })
    @JoinTable(name = "enrolments_course",
            joinColumns = @JoinColumn(name = "course_id"),
            inverseJoinColumns = @JoinColumn(name = "enrolments_id"))
    private List<Enrolments> listEnrolments;


    @OneToMany(mappedBy = "course" ,fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<PurchaseHistory> listPurchaseHistory;

    @OneToMany(mappedBy = "course")
    private List<UserCourse> courseUsers;


    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL)
    private List<Chapter> chapters;

}
