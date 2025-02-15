package br.com.Blog.api.repositories;

import br.com.Blog.api.entities.FavoritePost;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FavoritePostRepository extends JpaRepository<FavoritePost, Long> {
    List<FavoritePost> findAllByUserId(Long id);

    FavoritePost findByUserIdAndPostId(Long idUser, Long idPost);

    boolean existsByUserIdAndPostId(Long idUser, Long idPost);



}
