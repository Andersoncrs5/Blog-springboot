package br.com.Blog.api.services;

import br.com.Blog.api.entities.Category;
import br.com.Blog.api.entities.User;
import br.com.Blog.api.repositories.CategoryRepository;
import br.com.Blog.api.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository repository;
    private final UserRepository userRepository;

    @Async
    public ResponseEntity<?> getAll(){
        try {
            return new ResponseEntity<>(this.repository.findAll(), HttpStatus.FOUND);
        } catch (Exception e){
            return new ResponseEntity<>(e, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Async
    public ResponseEntity<?> get(Long id){
        try {
            if (id == null)
                return new ResponseEntity<>("Id is required", HttpStatus.BAD_REQUEST);

            Category category = this.repository.findById(id).orElse(null);

            if (category == null)
                return new ResponseEntity<>("Category not found", HttpStatus.NOT_FOUND);

            return new ResponseEntity<>(category, HttpStatus.FOUND);
        } catch (Exception e){
            return new ResponseEntity<>(e, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Async
    @Transactional
    public ResponseEntity<?> delete(Long id){
        try {
            if (id == null)
                return new ResponseEntity<>("Id is required", HttpStatus.BAD_REQUEST);

            Category category = this.repository.findById(id).orElse(null);

            if (category == null)
                return new ResponseEntity<>("Category not found", HttpStatus.NOT_FOUND);

            this.repository.delete(category);

            return new ResponseEntity<>("Category deleted", HttpStatus.FOUND);
        } catch (Exception e){
            return new ResponseEntity<>(e, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Async
    @Transactional
    public ResponseEntity<?> create(Category category, Long idUser){
        try {
            if(idUser == null)
                return new ResponseEntity<>("Id is required", HttpStatus.BAD_REQUEST);

            User user = this.userRepository.findById(idUser).orElse(null);

            if (user == null)
                return new ResponseEntity<>("User is required", HttpStatus.NOT_FOUND);

            category.setUser(user);
            return new ResponseEntity<>(this.repository.save(category), HttpStatus.CREATED);
        } catch (Exception e){
            return new ResponseEntity<>(e, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Async
    @Transactional
    public ResponseEntity<?> update(Category category){
        try {
            Category categoryToUpdate = this.repository.findById(category.getId()).orElse(null);

            if (categoryToUpdate == null)
                return new ResponseEntity<>("Category not found", HttpStatus.NOT_FOUND);

            categoryToUpdate.setName(category.getName());

            Category categoryUpdated = this.repository.save(categoryToUpdate);
            return new ResponseEntity<>(categoryUpdated, HttpStatus.CREATED);
        } catch (Exception e){
            return new ResponseEntity<>(e, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
