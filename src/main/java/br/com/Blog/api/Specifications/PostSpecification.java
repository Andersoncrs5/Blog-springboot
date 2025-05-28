package br.com.Blog.api.Specifications;

import br.com.Blog.api.entities.Post;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PostSpecification {

    public static Specification<Post> filterBy(
            LocalDateTime createdAtBefore,
            LocalDateTime createdAtAfter,
            String title,
            Long categoryId,
            Long likesBefore,
            Long likesAfter,
            Long dislikesBefore,
            Long dislikesAfter,
            Long comments,
            Long favorites,
            Long viewed
    ) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (createdAtAfter != null && createdAtBefore != null) {
                predicates.add(cb.between(root.get("createdAt"), createdAtAfter, createdAtBefore));
            }

            if (createdAtBefore != null && createdAtAfter == null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), createdAtBefore));
            }

            if (createdAtAfter != null && createdAtBefore == null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), createdAtAfter));
            }

            if (categoryId != null) {
                predicates.add(cb.equal(root.get("category").get("id"), categoryId));
            }

            if (title != null && !title.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%"));
            }

            if (likesBefore != null && likesAfter != null) {
                predicates.add(cb.between(root.get("metrics").get("likes"), likesAfter, likesBefore));
            }

            if (likesBefore != null && likesAfter == null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("metrics").get("likes"), likesBefore));
            }

            if (likesAfter != null && likesBefore == null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("metrics").get("likes"), likesAfter));
            }

            if (dislikesBefore != null && dislikesAfter != null) {
                predicates.add(cb.between(root.get("metrics").get("dislikes"), dislikesAfter, dislikesBefore));
            }

            if (dislikesBefore != null && dislikesAfter == null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("metrics").get("dislikes"), dislikesBefore));
            }

            if (dislikesAfter != null && dislikesBefore == null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("metrics").get("dislikes"), dislikesAfter));
            }

            if (comments != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("metrics").get("comments"), comments));
            }

            if (favorites != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("metrics").get("favorites"), favorites));
            }

            if (viewed != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("metrics").get("viewed"), viewed));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
