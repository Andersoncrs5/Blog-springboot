package br.com.Blog.api.repositories;

import br.com.Blog.api.entities.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByUserId(Long id);
    List<Comment> findAllByPostId(Long id);
}
