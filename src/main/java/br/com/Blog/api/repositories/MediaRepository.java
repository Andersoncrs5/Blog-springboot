package br.com.Blog.api.repositories;

import br.com.Blog.api.entities.Media;
import br.com.Blog.api.entities.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MediaRepository extends JpaRepository<Media, Long> {
    List<Media> findAllByPost(Post post);
}
