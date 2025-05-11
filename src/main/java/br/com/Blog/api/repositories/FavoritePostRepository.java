package br.com.Blog.api.repositories;

import br.com.Blog.api.entities.FavoritePost;
import br.com.Blog.api.entities.Post;
import br.com.Blog.api.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoritePostRepository extends JpaRepository<FavoritePost, Long> {
    Page<FavoritePost> findAllByUser(User user, Pageable pageable);

    boolean existsByUserIdAndPostId(Long idUser, Long idPost);

    boolean existsByUserAndPost(User user, Post post);
}
