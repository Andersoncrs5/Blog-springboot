package br.com.Blog.api.services;

import br.com.Blog.api.entities.Category;
import br.com.Blog.api.entities.User;
import br.com.Blog.api.repositories.CategoryRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository repository;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private ObjectMapper objectMapper;

    @Transactional(readOnly = true)
    public List<Category> getAll(){
        return this.repository.findAll();
    }

    public List<Category> getAllV2() {
        Object cached = redisTemplate.opsForValue().get("categories");

        if (cached != null) {
            return objectMapper.convertValue(
                    cached, new TypeReference<List<Category>>() {}
            );
        }

        List<Category> all = repository.findAll();
        redisTemplate.opsForValue().set("categories", all, Duration.ofMinutes(8));

        return all;
    }


    public Category get(Long id){
        if (id <= 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Id is required");

        Category category = this.repository.findById(id).orElse(null);

        if (category == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found");

        return category;
    }

    @Transactional
    public void delete(Category category){
        this.repository.delete(category);
    }

    @Transactional
    public Category create(Category category, User user){
        boolean check = this.repository.existsByName(category.getName());

        if (check)
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Name in used! Try another name");

        category.setUser(user);
        return this.repository.save(category);
    }

    @Transactional
    public Category update(Category categoryToUpdate, Category category){
        categoryToUpdate.setName(category.getName());

        return this.repository.save(categoryToUpdate);
    }

}