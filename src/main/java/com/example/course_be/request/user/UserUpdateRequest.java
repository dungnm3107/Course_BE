package com.example.course_be.request.user;

import com.example.course_be.enums.Gender;
import com.example.course_be.infrastructure.constant.EntityProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateRequest {


    private Long idUser;

    @Size(min = EntityProperties.MAX_LENGTH_5, max = EntityProperties.MAX_LENGTH_50, message = "Username must be between 5 and 50 characters")
    @NotBlank(message = "Username is required")
    private String userName;

    @Size(min = EntityProperties.MAX_LENGTH_5, max = EntityProperties.MAX_LENGTH_50, message = "Full name must be between 5 and 50 characters")
    private String fullName;

    @NotBlank(message = "Password is required")
    @Size(min = EntityProperties.MAX_LENGTH_5, max = EntityProperties.MAX_LENGTH_50, message = "Password must be between 5 and 50 characters")
    private String password;

    @NotBlank(message = "Email is required")
    @Pattern(regexp = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$", message = "Invalid email format")
    private String email;

    private String avatar;

    private Gender gender;

    @Pattern(regexp = "^0\\d{9}$", message = "Invalid p hone number format")
    private String phone;

}
