package br.com.Blog.api.services;

import br.com.Blog.api.entities.Category;
import br.com.Blog.api.entities.Post;
import br.com.Blog.api.entities.User;
import br.com.Blog.api.repositories.PostMetricsRepository;
import br.com.Blog.api.repositories.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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
public class PostService {

    @Autowired
    private PostRepository repository;

    @Async
    @Transactional
    public Post Create(Post post, User user, Category category){
        post.setUser(user);
        post.setCategory(category);
        post.setId(null);

        boolean checkSlug = this.repository.existsBySlug(post.getSlug());

        if(checkSlug) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Slug in used!! try another");
        }

        return this.repository.save(post);
    }

    @Async
    @Transactional
    public Post Get(Long id){
        if (id <= 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Id is required");

        Post post = this.repository.findById(id).orElse(null);

        if(post == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found");

        return post;
    }

    @Async
    @Transactional
    public Post Delete(Post post){
        this.repository.delete(post);
        return post;
    }

    @Async
    @Transactional
    public Page<Post> GetAll(Pageable pageable, Specification<Post> spec){
        return this.repository.findAll(spec, pageable);
    }

    @Async
    @Transactional
    public Page<Post> GetAllByCategory(Category category, Pageable pageable){
        return this.repository.findAllByCategory(category ,pageable);
    }

    @Async
    @Transactional
    public Page<Post> filterByTitle(String title, Pageable pageable) {
        return this.repository.findByTitleContainingIgnoreCase(title, pageable);
    }

    @Async
    @Transactional
    public Post Update(Post postExist, Post post){

        post.setUser(postExist.getUser());
        post.setId(postExist.getId());
        post.setVersion(postExist.getVersion());
        post.setCategory(postExist.getCategory());

        return this.repository.save(post);
    }

}
