package com.example.course_be.service.impl;

import com.example.course_be.entity.Chapter;
import com.example.course_be.entity.Course;
import com.example.course_be.entity.User;
import com.example.course_be.enums.CourseType;
import com.example.course_be.exceptions.AppException;
import com.example.course_be.exceptions.ErrorCode;
import com.example.course_be.repository.ChapterRepository;
import com.example.course_be.repository.CourseRepository;
import com.example.course_be.repository.LessonRepository;
import com.example.course_be.repository.UserRepository;
import com.example.course_be.request.course.CourseRequest;
import com.example.course_be.request.course.CourseUpdateRequest;
import com.example.course_be.response.course.CourseResponse;
import com.example.course_be.response.course.CourseStatisticResponse;
import com.example.course_be.service.CourseService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final ChapterRepository chapterRepository;
    private final ObjectMapper objectMapper;
    private final LessonRepository lessonRepository;

    @Override
    @Transactional
    public String saveCourse(CourseRequest courseRequest) {
        try {
            if (courseRequest == null) {
                return "Course not found";
            }

            boolean existsByTitle = courseRepository.existsByTitle(courseRequest.getTitle());
            if (existsByTitle) {
                return "Course already exists";
            }

            User user = userRepository.findById(courseRequest.getIdUserCreate()).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
            Course course = getCourseFromRequest(courseRequest, user);
            courseRepository.save(course);
            return "Course saved successfully";
        } catch (Exception e) {
            throw new AppException(ErrorCode.COURSE_SAVE_ERROR);
        }
    }

    @Override
    public CourseResponse getCourseById(Long courseId) {
        if (courseId == null) {
            throw new AppException(ErrorCode.COURSE_NOT_FOUND);
        }
        Course course = courseRepository.findById(courseId).orElseThrow(()
                -> new AppException(ErrorCode.COURSE_NOT_FOUND));
        return convertCourseToResponse(course);
    }

    @Override
    public List<CourseResponse> getAllCourses() {
        List<Course> listCourses = courseRepository.findByDeletedFalse();
        if (listCourses.isEmpty()) {
            throw new AppException(ErrorCode.COURSE_NOT_FOUND);
        }
        List<CourseResponse> listCoresResponse = new ArrayList<>();
        for (Course course : listCourses) {
            listCoresResponse.add(convertCourseToResponseGetAll(course));
        }
        return listCoresResponse;
    }


    public List<CourseResponse> getAllFreeCourses() {
        List<Course> freeCourses = courseRepository.findByCourseTypeAndDeletedFalse(CourseType.FREE);
        if (freeCourses.isEmpty()) {
            throw new AppException(ErrorCode.COURSE_NOT_FOUND);
        }
        List<CourseResponse> freeCourseResponses = new ArrayList<>();
        for (Course course : freeCourses) {
            freeCourseResponses.add(convertCourseToResponseGetAll(course));
        }
        return freeCourseResponses;
    }


    public List<CourseResponse> getAllPaidCourses() {
        List<Course> paidCourses = courseRepository.findByCourseTypeAndDeletedFalse(CourseType.PAID);
        if (paidCourses.isEmpty()) {
            throw new AppException(ErrorCode.COURSE_NOT_FOUND);
        }
        List<CourseResponse> paidCourseResponses = new ArrayList<>();
        for (Course course : paidCourses) {
            paidCourseResponses.add(convertCourseToResponseGetAll(course));
        }
        return paidCourseResponses;
    }


    @Override
    public String deleteCourseById(Long courseId) {
    Course course = courseRepository.findById(courseId)
                    .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));
            course.setDeleted(true);
            courseRepository.save(course);
            return "Course deleted successfully";
    }


    @Override
    @Transactional
    public String updateCourse(CourseUpdateRequest courseUpdateRequest) {
        Optional<Course> existingCourse = Optional.ofNullable(courseRepository.findById(courseUpdateRequest.getIdCourse()).orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND)));
        boolean existsByTitle = courseRepository.existsByTitle(courseUpdateRequest.getTitle());

        if (existingCourse.isEmpty()) {
            throw new AppException(ErrorCode.COURSE_NOT_FOUND);
        }

        if (Objects.equals(existingCourse.get().getTitle(), courseUpdateRequest.getTitle())) {
            existsByTitle = false;
        }
        if (existsByTitle) {
            return "Course already exists";
        }
        Course courseToUpdate = getCourse(courseUpdateRequest, existingCourse);
        courseRepository.save(courseToUpdate);
        return "Course updated successfully";
    }


    @Override
    @Transactional
    public String changeStatusCourse(Long courseId) {
        Optional<Course> course = Optional.ofNullable(courseRepository.findById(courseId).orElseThrow(()
                -> new AppException(ErrorCode.COURSE_NOT_FOUND)));
        if (course.isPresent()) {
            course.get().setDeleted(!course.get().getDeleted());
            courseRepository.save(course.get());
            return "Course status changed successfully";
        }
        return "Course not found to change status";
    }

    @Override
    @Transactional
    public List<Course> searchCourses(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return List.of(); // Trả về danh sách rỗng nếu không có từ khóa
        }
        return courseRepository.searchCoursesByKeywords(keyword); // Gọi phương thức mới
    }
    @Override
    public CourseStatisticResponse getCourseStatistic() {
        long totalCourses = courseRepository.countCourses();
        long totalChapters = courseRepository.countChapters();
        long totalLesson =  courseRepository.countLessons();
        return new CourseStatisticResponse(totalCourses, totalChapters, totalLesson);

    }

    private Course getCourseFromRequest(CourseRequest courseRequest, User user) {
        Course course = new Course();
        course.setTitle(courseRequest.getTitle());
        course.setDescription(courseRequest.getDescription());
        course.setCoursePrice(courseRequest.getCoursePrice());
        course.setCreateBy(user.getFullName());
        course.setCover(courseRequest.getCover());
        course.setCourseType(courseRequest.getCourseType());
        course.setVideoUrl(courseRequest.getVideoUrl());
        return course;
    }

    private Course getCourse(CourseUpdateRequest courseUpdateRequest, Optional<Course> existingCourse) {

        if (existingCourse.isEmpty()) {
            throw new AppException(ErrorCode.COURSE_NOT_FOUND);
        }

        Course courseToUpdate = existingCourse.get();

        User user = userRepository.findById(courseUpdateRequest.getIdUserUpdate()).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        courseToUpdate.setUpdateBy(user.getFullName());
        if (courseUpdateRequest.getTitle() != null) {
            courseToUpdate.setTitle(courseUpdateRequest.getTitle());
        } else {
            courseToUpdate.setTitle(existingCourse.get().getTitle());
        }
        if (courseUpdateRequest.getDescription() != null) {
            courseToUpdate.setDescription(courseUpdateRequest.getDescription());
        }
        if (courseUpdateRequest.getCoursePrice() != null) {
            courseToUpdate.setCoursePrice(courseUpdateRequest.getCoursePrice());
        }
        if (courseUpdateRequest.getCover() != null) {
            courseToUpdate.setCover(courseUpdateRequest.getCover());
        }
        if(courseUpdateRequest.getVideoUrl() != null) {
            courseToUpdate.setVideoUrl(courseUpdateRequest.getVideoUrl());
        }

        return courseToUpdate;
    }



    private CourseResponse convertCourseToResponse(Course course) {
        CourseResponse courseResponse = new CourseResponse();
        List<Chapter> listChapter = chapterRepository.findAllByCourseIdAndDeletedFalse(course.getId());
        courseResponse.setId(course.getId());
        courseResponse.setTitle(course.getTitle());
        courseResponse.setDescription(course.getDescription());
        courseResponse.setCoursePrice(course.getCoursePrice());
        courseResponse.setCreateBy(course.getCreateBy());
        courseResponse.setCover(course.getCover());
        courseResponse.setDeleted(course.getDeleted());
        courseResponse.setUpdateBy(course.getUpdateBy());
        courseResponse.setCreatedAt(course.getCreatedAt());
        courseResponse.setUpdateAt(course.getUpdateAt());
        courseResponse.setCourseType(course.getCourseType());
        courseResponse.setVideoUrl(course.getVideoUrl());
        courseResponse.setListChapter(listChapter);
        return courseResponse;
    }

    private CourseResponse convertCourseToResponseGetAll(Course course) {
        CourseResponse courseResponse = new CourseResponse();
        courseResponse.setId(course.getId());
        courseResponse.setTitle(course.getTitle());
        courseResponse.setDescription(course.getDescription());
        courseResponse.setCoursePrice(course.getCoursePrice());
        courseResponse.setCreateBy(course.getCreateBy());
        courseResponse.setCover(course.getCover());
        courseResponse.setCourseType(course.getCourseType());
        courseResponse.setVideoUrl(course.getVideoUrl());
        courseResponse.setDeleted(course.getDeleted());
        courseResponse.setUpdateBy(course.getUpdateBy());
        courseResponse.setCreatedAt(course.getCreatedAt());
        courseResponse.setUpdateAt(course.getUpdateAt());
        return courseResponse;
    }

}