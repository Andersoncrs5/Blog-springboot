package br.com.Blog.api.services;

import br.com.Blog.api.entities.Category;
import br.com.Blog.api.entities.Post;
import br.com.Blog.api.entities.User;
import br.com.Blog.api.repositories.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository repository;
    private final UserService userService;
    private final CategoryService categoryService;

    @Async
    @Transactional
    public ResponseEntity<?> Create(Post post, Long userId, Long categoryId){
        Category category = this.categoryService.get(categoryId);
        User user = this.userService.Get(userId);

        post.setUser(user);
        post.setCategory(category);

        this.repository.save(post);

        return new ResponseEntity<>("Post created with success!", HttpStatus.CREATED);
    }

    @Async
    @Transactional
    public ResponseEntity<?> Update(Long id, Post post){
        Post postForUpdate = this.Get(id);

        postForUpdate.setTitle(post.getTitle());
        postForUpdate.setContent(post.getContent());
        post.setReadingTime(post.getReadingTime());
        post.setImageUrl(post.getImageUrl());

        this.repository.save(postForUpdate);

        return new ResponseEntity<>("Post updated with success", HttpStatus.OK);
    }

    @Async
    @Transactional
    public Post Get(Long id){
        if (id == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Id is required");

        Post post = this.repository.findById(id).orElse(null);

        if(post == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found");

        return post;
    }

    @Async
    @Transactional
    public ResponseEntity<?> Delete(Long id){
        Post post = this.Get(id);

        this.repository.delete(post);
        return new ResponseEntity<>("Post deleted", HttpStatus.OK);
    }

    @Async
    @Transactional
    public ResponseEntity<?> GetAll(Pageable pageable, Specification<Post> spec){
            return new ResponseEntity<>(this.repository.findAll(spec, pageable), HttpStatus.OK);
    }

    @Async
    @Transactional
    public ResponseEntity<?> GetAllByCategory(Long categoryId, Pageable pageable){
        Category category = this.categoryService.get(categoryId);

        Page<Post> posts = this.repository.findAllByCategory(category ,pageable);
        return new ResponseEntity<>(posts, HttpStatus.OK);
    }

    @Async
    @Transactional
    public ResponseEntity<?> filterByTitle(String title, Pageable pageable) {
        return new ResponseEntity<>(this.repository.findByTitleContaining(title, pageable)  ,HttpStatus.OK);
    }

}
