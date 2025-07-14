package br.com.Blog.api.repositories;

import br.com.Blog.api.entities.User;
import br.com.Blog.api.entities.UserPreference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserPreferenceRepository extends JpaRepository<UserPreference, Long> {
    Page<UserPreference> findAllByUser(User user, Pageable pageable);
    List<UserPreference> findAllByUser(User user);
}
