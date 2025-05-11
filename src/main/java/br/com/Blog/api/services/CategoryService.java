package br.com.Blog.api.services;

import br.com.Blog.api.entities.Category;
import br.com.Blog.api.entities.User;
import br.com.Blog.api.repositories.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository repository;
    private final UserService userService;

    @Async
    @Transactional
    public ResponseEntity<?> getAll(){
        return new ResponseEntity<>(this.repository.findAll(), HttpStatus.OK);
    }

    @Async
    public Category get(Long id){
        if (id == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Id is required");

        Category category = this.repository.findById(id).orElse(null);

        if (category == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found");

        return category;
    }

    @Async
    @Transactional
    public ResponseEntity<?> delete(Long id){
        Category category = this.get(id);
        this.repository.delete(category);

        return new ResponseEntity<>("Category deleted", HttpStatus.FOUND);
    }

    @Async
    @Transactional
    public ResponseEntity<?> create(Category category, Long userId){
        User user = this.userService.Get(userId);

        category.setUser(user);
        return new ResponseEntity<>(this.repository.save(category), HttpStatus.CREATED);
    }

    @Async
    @Transactional
    public ResponseEntity<?> update(Long id, Category category){
        Category categoryToUpdate = this.get(id);

        categoryToUpdate.setName(category.getName());

        Category categoryUpdated = this.repository.save(categoryToUpdate);
        return new ResponseEntity<>(categoryUpdated, HttpStatus.OK);
    }

}
