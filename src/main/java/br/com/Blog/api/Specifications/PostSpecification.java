package br.com.Blog.api.Specifications;

import br.com.Blog.api.entities.Post;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PostSpecification {

    public static Specification<Post> filterBy(LocalDateTime createdAt, String title) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (createdAt != null) {
                predicates.add(cb.equal(root.get("createdAt"), createdAt));
            }

            if (title != null && !title.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%"));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
