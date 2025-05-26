package br.com.Blog.api.controllers;

import br.com.Blog.api.DTOs.PostDTO;
import br.com.Blog.api.Specifications.PostSpecification;
import br.com.Blog.api.config.annotation.RateLimit;
import br.com.Blog.api.controllers.setUnitOfWork.UnitOfWork;
import br.com.Blog.api.entities.Post;
import br.com.Blog.api.entities.enums.SumOrReduce;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/v1/posts")
@RequiredArgsConstructor
public class PostController {

    private final UnitOfWork uow;

    @GetMapping("/get/{id}")
    @ResponseStatus(HttpStatus.OK)
    @RateLimit(capacity = 15, refillTokens = 2, refillSeconds = 8)
    public Post get(@PathVariable Long id){
        Post post = this.uow.postService.Get(id);
        this.uow.postMetricsService.viewed(post);
        return post;
    }

    @GetMapping("/getAll")
    @RateLimit(capacity = 20, refillTokens = 2, refillSeconds = 12)
    public ResponseEntity<?> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) LocalDateTime createdAtAfter,
            @RequestParam(required = false) LocalDateTime createdAtBefore,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Long categoryId

            ) {
        Pageable pageable = PageRequest.of(page, size);
        Specification<Post> spec = PostSpecification.filterBy(createdAtBefore, createdAtAfter, title, categoryId);
        return this.uow.postService.GetAll(pageable, spec);
    }

    @RateLimit(capacity = 10, refillTokens = 2, refillSeconds = 8)
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("{id}")
    public ResponseEntity<?> delete(@PathVariable Long id){
        Post post = this.uow.postService.Delete(id);
        this.uow.userMetricsService.sumOrRedPostsCount(post.getUser(), SumOrReduce.REDUCE);

        return new ResponseEntity<>("Post deleted with successfully", HttpStatus.OK);
    }

    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/{categoryId}")
    @RateLimit(capacity = 10, refillTokens = 2, refillSeconds = 8)
    public ResponseEntity<?> Create(
            @RequestBody @Valid PostDTO dto,
            HttpServletRequest request,
            @PathVariable Long categoryId
    ) {

        Long userId = this.uow.jwtService.extractId(request);
        Post post = this.uow.postService.Create(dto.MappearToPost(), userId, categoryId);
        this.uow.postMetricsService.create(post);
        this.uow.userMetricsService.sumOrRedPostsCount(post.getUser(), SumOrReduce.SUM);
        this.uow.notificationsService.notifyFollowersAboutPostCreated(post);

        return new ResponseEntity<>("Post created with successfully", HttpStatus.CREATED);
    }

    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("{postId}")
    @RateLimit(capacity = 10, refillTokens = 2, refillSeconds = 8)
    public ResponseEntity<?> Update(@PathVariable Long postId, @RequestBody @Valid PostDTO dto){
        Post post = this.uow.postService.Update(postId, dto.MappearToPost());
        this.uow.postMetricsService.editedTimes(post);

        return new ResponseEntity<>("Post updated with successfully", HttpStatus.OK);
    }

    @GetMapping("/GetAllByCategory/{categoryId}")
    @RateLimit(capacity = 12, refillTokens = 2, refillSeconds = 8)
    public ResponseEntity<?> GetAllByCategory(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return this.uow.postService.GetAllByCategory(categoryId, pageable);
    }

    @GetMapping("/filterByTitle/{title}")
    @RateLimit(capacity = 12, refillTokens = 2, refillSeconds = 8)
    public ResponseEntity<?> filterByTitle(
            @PathVariable String title,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return this.uow.postService.filterByTitle(title, pageable);
    }

}
