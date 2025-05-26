package br.com.Blog.api.Specifications;

import br.com.Blog.api.entities.Notification;
import br.com.Blog.api.entities.enums.StatusNotification;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class NotificationSpecification {

    public static Specification<Notification> filterBy(
            LocalDateTime createdAtBefore,
            LocalDateTime createdAtAfter,
            Boolean isRead,
            StatusNotification status
    ) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (createdAtAfter != null && createdAtBefore != null) {
                predicates.add(cb.between(root.get("createdAt"), createdAtAfter, createdAtBefore));
            }

            if (createdAtAfter != null && createdAtBefore == null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), createdAtAfter));
            }

            if (createdAtBefore != null && createdAtAfter == null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), createdAtBefore));
            }

            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }

            if (isRead != null) {
                predicates.add(cb.equal(root.get("isRead"), isRead));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
