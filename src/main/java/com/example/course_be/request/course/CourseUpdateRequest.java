package com.example.course_be.request.course;

import com.example.course_be.enums.CourseType;
import com.example.course_be.infrastructure.constant.EntityProperties;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CourseUpdateRequest {

    private Long idCourse;

    @NotNull(message = "Id user create is not empty")
    private Long idUserUpdate;

    @Size(min = EntityProperties.MAX_LENGTH_10, max = EntityProperties.MAX_LENGTH_50, message = "Title must be between 5 and 50 characters")
    @NotBlank(message = "Title is not empty")
    private String title;

    @Size(min = EntityProperties.MAX_LENGTH_10, max = EntityProperties.MAX_LENGTH_2000, message = "Description must be between 10 and 255 characters")
    @NotBlank(message = "Description is not empty")
    private String description;

    @PositiveOrZero(message = "Price must be non-negative")
    private BigDecimal coursePrice;

    private String cover;

    private CourseType courseType;

    private String videoUrl;

    private Boolean deleted;

    @AssertTrue(message = "For free courses, the price must be 0.")
    private boolean isCoursePriceValid() {
        if (courseType == CourseType.FREE) {
            return coursePrice != null && coursePrice.compareTo(BigDecimal.ZERO) == 0;
        }
        return true;
    }

}
