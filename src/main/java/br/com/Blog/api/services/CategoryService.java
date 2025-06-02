package br.com.Blog.api.services;

import br.com.Blog.api.entities.Category;
import br.com.Blog.api.entities.User;
import br.com.Blog.api.repositories.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository repository;

    @Async
    @Transactional(readOnly = true)
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

        if (check)
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Name in used! Try another name");

        category.setUser(user);
        return this.repository.save(category);
    }

    @Async
    @Transactional
    public Category update(Category categoryToUpdate, Category category){
        categoryToUpdate.setName(category.getName());

        return this.repository.save(categoryToUpdate);
    }

}
