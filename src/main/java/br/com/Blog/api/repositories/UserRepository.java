package br.com.Blog.api.repositories;

import br.com.Blog.api.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);

    Optional<User> findByName(String superadmin);
}
