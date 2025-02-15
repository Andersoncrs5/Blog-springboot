package br.com.Blog.api.repositories;

import br.com.Blog.api.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
