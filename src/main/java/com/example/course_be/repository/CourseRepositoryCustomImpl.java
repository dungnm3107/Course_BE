package com.example.course_be.repository;

import com.example.course_be.entity.Course;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import java.util.List;

// sử dụng JPA Criteria API để xây dựng truy vấn động
@Repository
public class CourseRepositoryCustomImpl implements CourseRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Course> searchCoursesByKeywords(String keywords) {
        if (keywords == null || keywords.trim().isEmpty()) {
            return List.of(); // Trả về danh sách rỗng nếu không có từ khóa
        }

        String[] keywordArray = keywords.trim().split("\\s+"); // Tách theo khoảng trắng

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Course> cq = cb.createQuery(Course.class);
        Root<Course> course = cq.from(Course.class);

        List<Predicate> predicates = new ArrayList<>();

        // Duyệt qua từng từ và thêm điều kiện LIKE
        for (String keyword : keywordArray) {
            if (!keyword.isEmpty()) {
                predicates.add(cb.like(cb.lower(course.get("title")), "%" + keyword.toLowerCase() + "%"));
            }
        }

        // kiểm tra khóa học không bị xóa
        Predicate deletedPredicate = cb.equal(course.get("deleted"), false);

        // Sử dụng OR cho tất cả các predicates(điều kiện) của từ khóa
        cq.where(cb.and(deletedPredicate, cb.or(predicates.toArray(new Predicate[0]))));


        return entityManager.createQuery(cq).getResultList();
    }

}
