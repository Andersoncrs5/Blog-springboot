package br.com.Blog.api.repositories;

import br.com.Blog.api.entities.UserConfig;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserConfigRepository extends JpaRepository<UserConfig, Long> {
}
