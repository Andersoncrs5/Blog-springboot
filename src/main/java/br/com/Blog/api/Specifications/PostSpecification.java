package br.com.Blog.api.Specifications;

import br.com.Blog.api.entities.Post;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PostSpecification {

    public static Specification<Post> filterBy(LocalDateTime createdAtBefore, LocalDateTime createdAtAfter, String title, Long categoryId) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (createdAtAfter != null && createdAtBefore != null ) {
                predicates.add(cb.between(root.get("createdAt"), createdAtAfter, createdAtBefore));
            }

            if (createdAtBefore != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), createdAtBefore));
            }

            if (createdAtAfter != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), createdAtAfter));
            }

            if (categoryId != null) {
                predicates.add(cb.equal(root.get("category").get("id"), categoryId));
            }

            if (title != null && !title.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%"));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
