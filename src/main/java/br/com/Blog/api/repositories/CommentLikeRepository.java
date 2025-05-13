package br.com.Blog.api.repositories;

import br.com.Blog.api.entities.Comment;
import br.com.Blog.api.entities.CommentLike;
import br.com.Blog.api.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {
    boolean existsByUserAndComment(User user, Comment comment);

    Page<CommentLike> findAllByUser(User user, Pageable pageable);

    Integer countAllByComment(Comment comment);

    boolean existsByUserIdAndCommentId(Long userId, Long commentId);
}
