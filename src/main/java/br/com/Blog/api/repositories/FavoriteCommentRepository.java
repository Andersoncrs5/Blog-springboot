package br.com.Blog.api.repositories;

import br.com.Blog.api.entities.Comment;
import br.com.Blog.api.entities.FavoriteComment;
import br.com.Blog.api.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FavoriteCommentRepository extends JpaRepository<FavoriteComment, Long> {
    Page<FavoriteComment> findAllByUser(User user, Pageable pageable);

    Boolean existsByUserAndComment(User user, Comment comment);
}
