package br.com.Blog.api.repositories;

import br.com.Blog.api.entities.User;
import br.com.Blog.api.entities.UserConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserConfigRepository extends JpaRepository<UserConfig, Long> {
    Optional<UserConfig> findByUser(User user);
}
