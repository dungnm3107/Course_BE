package com.example.course_be.security;

public class Endpoints {


    public static final String[] PUBLIC_GET = {
            "/api/v1/chapter",
            "/api/v1/course/free",
            "/api/v1/course/paid",
            "/api/v1/lesson/**",
            "/api/v1/course/get/**",
            "/api/v1/chapter/get/**",
            "/api/v1/lesson/get/**",
            "/api/v1/orders/**",
            "/api/v1/course/search/**",
            "/api/v1/payment/vn-pay",
            "/api/v1/payment/vn-pay/callback",
            "/api/v1/orders/latest",
            "/api/v1/lesson-watch-history/get",
            "/api/v1/lesson-watch-history/watched",
            "/api/v1/favorite-lessons",
            "/api/v1/progress/completed",
            "api/v1/video/stream",
            "api/v1/video/get-presigned-url",
            "/api/v1/video/gcs/get-url",
            "/api/v1/user/profile",
            "/api/v1/lesson-watch-history/top8-watched",
            "/api/v1/orders/user"
    };

    public static final String[] PUBLIC_PUT = {
            "/api/v1/password-reset/change-password",
    };

    public static final String[] PUBLIC_POST = {
            "/api/v1/user/register",
            "/api/v1/user/login",
            "/api/v1/SSO/loginGoogle",
            "/api/v1/orders",
            "/api/v1/payment/create-order",
            "/api/v1/lesson-watch-history/save",
            "/api/v1/favorite-lessons/toggle",
            "/api/v1/progress",
            "/api/v1/password-reset",
            "api/v1/password-reset/validate-token",
    };

    public static final String[] ADMIN_POST = {
            "/api/v1/course/save",
            "/api/v1/course/addUserToCourse",
            "/api/v1/lesson/save",
            "/api/v1/chapter/save",
            "/api/v1/course/upload-cover",
            "/api/v1/video/upload",
            "/api/v1/video/gcs/upload",
    };
    public static final String[] PUBLIC_DELETE = {
            "/api/v1/favorite-lessons",
    };

    public static final String[] ADMIN_GET = {
            "/api/v1/course/all",
            "/api/v1/user/search/**",
            "/api/v1/user/**",
            "/api/v1/video/presigned-url/**",
            "/api/v1/user/count",
            "/api/v1/course/statistics",
            "/api/v1/user/registration-statistics",
            "api/v1/orders/revenue",
            "/api/v1/favorite-lessons/top4",
            "api/v1/orders/most-purchased-courses",
            "api/v1/orders/total-revenue",
            "/api/v1/google-analytics/user-access",
    };

    public static final String[] ADMIN_PUT = {
            "/api/v1/course/update/**",
            "/api/v1/course/changeStatus/**",
            "/api/v1/chapter/update",
            "/api/v1/lesson/update",

    };
    public static final String[] ADMIN_DELETE = {
            "/api/v1/course/**",
            "/api/v1/chapter/**",
            "/api/v1/lesson/**",
            "/api/v1/user/**",
    };


    public static final String[] CUSTOMER_GET = {

            "/api/v1/course/get/**",

    };

    public static final String[] CUSTOMER_POST = {
            "/api/v1/user/addUserCourse",
            "/api/v1/blog/save",
    };

    public static final String[] CUSTOMER_PUT = {
            "/api/v1/user/updateUser",

    };

}
