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

    @OneToMany(mappedBy = "lessons", cascade = {
            CascadeType.DETACH,
            CascadeType.MERGE,
            CascadeType.PERSIST,
            CascadeType.REFRESH}
    )
    private List<Comments> listComments;

    @OneToMany(mappedBy = "lesson", cascade = CascadeType.ALL)
    private List<FavoriteLesson> favoriteLessons;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "chapter_id", nullable = false)
    private Chapter chapter;


//    usser  thêm trang thiasi hoàn nthanhf bài hịc
    // admin cắt lại giao diện
    //  yêu thích thay bài viết:
    //  trang admin , tổng số lượng khóa học hiện tại , lieetj kee 10 video yeeu thichs nhaast, filter thoe time,   khóa học được nhiều người mua nhất
    // Doanh thu
    // thoongs kee tir leej ddawng kis theo tuwngf thanhs
    // thoongs kee xem heej thoongs , bao nhieeu nguwowif truy capaj heej thoongs cungf lucs
    // hoi chat ....
    // tinhf hinhf hocj taapj trong trang cua em

    // mucj tieeu cuar admin -> lieen quan ddeesn doardboad + bieeru ddoof, loc
    // với khóa học free cho phép ngta xem
    // đến vài nào thì cho login
    //    {/* % bài học và % khoa học tới đâu */}
    //    {/* tạm thời boe bài viết */}

    // video giới thiệu khóa học, cho người ta xem: done
    // khi học xog, cho view người ta xem bài học đã hoàn thành: done
}
