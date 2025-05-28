package br.com.Blog.api.repositories;

import br.com.Blog.api.entities.Post;
import br.com.Blog.api.entities.PostMetrics;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostMetricsRepository extends JpaRepository<PostMetrics, Long> {
    Optional<PostMetrics> findByPost(Post post);
}
