package br.com.Blog.api.repositories;

import br.com.Blog.api.entities.Post;
import br.com.Blog.api.entities.PostMetrics;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostMetricsRepository extends JpaRepository<PostMetrics, Post> {
    PostMetrics findByPost(Post post);

}
