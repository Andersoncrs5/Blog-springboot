package br.com.Blog.api.repositories;

import br.com.Blog.api.entities.RecoverEmail;
import br.com.Blog.api.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RecoverEmailRepository extends JpaRepository<RecoverEmail, Long> {
    Optional<RecoverEmail> findByToken(String token);
    Optional<RecoverEmail> findByUser(User user);
}