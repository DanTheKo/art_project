package com.prototype.specifications;

import com.prototype.entities.Authority;
import com.prototype.entities.Post;
import com.prototype.entities.User;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;


public class PostSpecifications {
    public static Specification<Post> hasText(String text) {
        return ((root, query, builder) -> {
            if (text == null || text.isBlank()) {
                return builder.conjunction();
            }
            return builder.like(
                    builder.lower(root.get("post_text")), "%" + text.toLowerCase() + "%");
        });
    }

    public static Specification<Post> afterDate(LocalDate date) {
        return ((root, query, builder) -> {
            if (date == null) {
                return builder.conjunction();
            }
            return builder.greaterThanOrEqualTo(root.get("created_at"), date);
        });
    }
    public static Specification<Post> beforeDate(LocalDate date) {
        return ((root, query, builder) -> {
            if (date == null) {
                return builder.conjunction();
            }
            return builder.lessThanOrEqualTo(root.get("created_at"), date);
        });
    }

    public static Specification<Post> hasUser(User user) {
        return ((root, query, builder) -> {
            if (user == null) {
                return builder.conjunction();
            }
            return builder.equal(root.get("user"), user);
        });
    }
//    public static Specification<Post> isAdmin(Authority authority) {
//        return ((root, query, builder) -> {
//            if (authority == null) {
//                return builder.conjunction();
//            }
//            return builder.equal(root.get("authority"), authority);
//        });
//    }
}