package br.com.Blog.api.repositories;

import br.com.Blog.api.entities.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllByUserId(Long id);
}
