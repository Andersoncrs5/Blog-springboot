package br.com.Blog.api.repositories;

import br.com.Blog.api.entities.Notification;
import br.com.Blog.api.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface NotificationRepository extends JpaRepository<Notification, Long>, JpaSpecificationExecutor<Notification> {
    Page<Notification> findAllByUser(User user, Pageable pageable, Specification<Notification> spec);
}
