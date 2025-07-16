package br.com.Blog.api.utils.Specifications;

import br.com.Blog.api.entities.User;
import br.com.Blog.api.utils.filtersDtos.UserFilterDTO;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;

import java.util.ArrayList;
import java.util.List;

public class UserSpecification {
    public static Specification<User> filterBy(UserFilterDTO dto) {
        return (root, query, cb) -> {
          List<Predicate> predicates = new ArrayList<>();

            if (dto.getCreatedAtAfter() != null && dto.getCreatedAtBefore() != null) {
                predicates.add(cb.between(root.get("createdAt"), dto.getCreatedAtAfter(), dto.getCreatedAtBefore()));
            }

            if (dto.getCreatedAtBefore() != null && dto.getCreatedAtAfter() == null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), dto.getCreatedAtBefore()));
            }

            if (dto.getCreatedAtAfter() != null && dto.getCreatedAtBefore() == null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), dto.getCreatedAtAfter()));
            }

            if (dto.getName() != null && !dto.getName().isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("name")), "%"+ dto.getName().toLowerCase() +"%"));
            }

            if (dto.getEmail() != null && !dto.getEmail().isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("email")), "%"+ dto.getEmail().toLowerCase() +"%"));
            }

            if (dto.getLoginBlockAt() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("loginBlockAt"), dto.getLoginBlockAt()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
