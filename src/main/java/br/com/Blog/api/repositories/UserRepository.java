package br.com.Blog.api.repositories;

import br.com.Blog.api.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);

    Optional<User> findByName(String superadmin);

    Page<User> findAllByRoles_Name(String adminRoleName, Pageable pageable);

    Page<User> findAll(Specification<User> specs, Pageable pageable);

}
