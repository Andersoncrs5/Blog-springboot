package br.com.Blog.api.repositories;

import br.com.Blog.api.entities.Media;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MediaRepository extends JpaRepository<Media, Long> {
}
