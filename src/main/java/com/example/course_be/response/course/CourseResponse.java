package com.example.course_be.response.course;

import com.example.course_be.entity.Chapter;
import com.example.course_be.enums.CourseType;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CourseResponse {

    private Long id;

    private String title;

    private String description;

    private BigDecimal coursePrice;

    private String cover;

    private CourseType courseType;

    private String videoUrl;

    private Date createdAt;

    private String createBy;

    private Date updateAt;

    private String updateBy;

    private List<Chapter> listChapter;

    private Boolean deleted;
}
