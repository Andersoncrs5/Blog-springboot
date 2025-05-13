package br.com.Blog.api.repositories;

import br.com.Blog.api.entities.User;
import br.com.Blog.api.entities.UserMetrics;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserMetricsRepository extends JpaRepository<UserMetrics, User> {
    Optional<UserMetrics> findByUser(User user);
}
