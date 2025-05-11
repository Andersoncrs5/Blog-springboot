package br.com.Blog.api.repositories;

import br.com.Blog.api.entities.Post;
import br.com.Blog.api.entities.PostLike;
import br.com.Blog.api.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    boolean existsByUserAndPost(User user, Post post);

    Page<PostLike> findAllByUser(User user, Pageable pageable);

    Integer countAllByPost(Post post);
}
