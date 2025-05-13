package br.com.Blog.api.repositories;

import br.com.Blog.api.entities.Comment;
import br.com.Blog.api.entities.CommentMetrics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommentMetricsRepository extends JpaRepository<CommentMetrics, Comment> {
    Optional<CommentMetrics> findByComment(Comment comment);
}
