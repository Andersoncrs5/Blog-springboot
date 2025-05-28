package br.com.Blog.api.Specifications;

import br.com.Blog.api.entities.Comment;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.criteria.Predicate;

public class CommentSpecification {

    public static Specification<Comment> filterBy(
            LocalDateTime createdAtBefore,
            LocalDateTime createdAtAfter,
            String content,
            Long viewsCountBefore,
            Long viewsCountAfter,
            Long favorites,
            Long likesBefore,
            Long likesAfter,
            Long dislikesBefore,
            Long dislikesAfter
    ) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (favorites != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("metrics").get("favorites"), favorites));
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

            if (viewsCountBefore != null && viewsCountAfter != null) {
                predicates.add(cb.between(root.get("metrics").get("viewsCount"), viewsCountAfter, viewsCountBefore));
            }
            if (viewsCountBefore != null && viewsCountAfter == null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("metrics").get("viewsCount"), viewsCountBefore));
            }
            if (viewsCountAfter != null && viewsCountBefore == null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("metrics").get("viewsCount"), viewsCountAfter));
            }

            if (createdAtAfter != null && createdAtBefore != null) {
                predicates.add(cb.between(root.get("createdAt"), createdAtAfter, createdAtBefore));
            }
            if (createdAtAfter != null && createdAtBefore == null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), createdAtAfter));
            }
            if (createdAtBefore != null && createdAtAfter == null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), createdAtBefore));
            }

            if (content != null && !content.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("content")), "%" + content.toLowerCase() + "%"));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}