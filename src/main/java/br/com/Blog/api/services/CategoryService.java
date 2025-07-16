package br.com.Blog.api.services;

import br.com.Blog.api.entities.Category;
import br.com.Blog.api.entities.User;
import br.com.Blog.api.repositories.CategoryRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.util.List;

@Slf4j
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
        log.info("Getting all category");
        return this.repository.findAll();
    }

    public List<Category> getAllV2() {
        log.info("Getting all category in getAllV2");
        Object cached = redisTemplate.opsForValue().get("categories");

        if (cached != null) {
            log.info("Returning categories in redis");
            return objectMapper.convertValue(
                    cached, new TypeReference<List<Category>>() {}
            );
        }

        log.info("Categories not found in cache. searching in database");
        List<Category> all = repository.findAll();

        log.info("Saved categories in cache");
        redisTemplate.opsForValue().set("categories", all, Duration.ofMinutes(8));

        log.info("Returning categories");
        return all;
    }

    public Category get(Long id){
        log.info("Searching category by id");
        if (id <= 0) {
            log.info("Id come null");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Id is required");
        }

        Category category = this.repository.findById(id).orElse(null);

        if (category == null) {
            log.info("Category not found");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found");
        }

        log.info("Returning category");
        return category;
    }

    @Transactional
    public void delete(Category category){
        log.info("Deleting category");

        if (category.getId() <= 0) {
            log.info("Category is null");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Id is required");
        }

        this.repository.delete(category);
        log.info("Category deleted");
    }

    @Transactional
    public Category create(Category category, User user){
        log.info("Creating new category");
        boolean check = this.repository.existsByName(category.getName());

        if (check) {
            log.info("Error category name exists!");
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Name in used! Try another name");
        }
        category.setUser(user);

        log.info("Category saved!");
        return this.repository.save(category);
    }

    @Transactional
    public Category update(Category categoryToUpdate, Category category) {
        log.info("Starting update of category");
        categoryToUpdate.setName(category.getName());

        log.info("Category updated");
        return this.repository.save(categoryToUpdate);
    }

    @Transactional
    public void changeStatusActive(Category category) {
        category.setIsActive(!category.getIsActive());

        repository.save(category);
    }

}