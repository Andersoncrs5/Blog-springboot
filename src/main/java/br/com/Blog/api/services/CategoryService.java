package br.com.Blog.api.services;

import br.com.Blog.api.entities.Category;
import br.com.Blog.api.entities.User;
import br.com.Blog.api.repositories.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository repository;
    private final UserService userService;

    @Async
    @Transactional
    public List<Category> getAll(){
        return this.repository.findAll();
    }

    @Async
    public Category get(Long id){
        if (id <= 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Id is required");

        Category category = this.repository.findById(id).orElse(null);

        if (category == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found");

        return category;
    }

    @Async
    @Transactional
    public void delete(Category category){
        this.repository.delete(category);
    }

    @Async
    @Transactional
    public Category create(Category category, User user){
        boolean check = this.repository.existsByName(category.getName());

        if (check) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Name in used! Try another name");
        }

        category.setUser(user);
        return this.repository.save(category);
    }

    @Async
    @Transactional
    public Category update(Long id, Category category){
        Category categoryToUpdate = this.get(id);

        categoryToUpdate.setName(category.getName());

        return this.repository.save(categoryToUpdate);
    }

}
