package br.com.Blog.api.controllers;

import br.com.Blog.api.DTOs.PostDTO;
import br.com.Blog.api.utils.Specifications.PostSpecification;
import br.com.Blog.api.config.annotation.RateLimit;
import br.com.Blog.api.controllers.setUnitOfWork.UnitOfWork;
import br.com.Blog.api.entities.*;
import br.com.Blog.api.entities.enums.SumOrReduce;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/v1/posts")
public class PostController {

    @Autowired
    private UnitOfWork uow;

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @RateLimit(capacity = 15, refillTokens = 2, refillSeconds = 8)
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> get(@PathVariable Long id, HttpServletRequest request){
        Post post = this.uow.postService.Get(id);
        PostMetrics metrics = this.uow.postMetricsService.get(post);
        this.uow.postMetricsService.viewed(metrics);

        var response = this.uow.responseDefault.response(
                "Post found with successfully",
                200,
                request.getRequestURL().toString(),
                post,
                true
        );

        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/getMetric/{postId}")
    @RateLimit(capacity = 15, refillTokens = 2, refillSeconds = 8)
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> getMetric(
            @PathVariable Long postId,
            HttpServletRequest request
    ) {
        Post post = this.uow.postService.Get(postId);
        PostMetrics metric = this.uow.postMetricsService.get(post);

        var response = this.uow.responseDefault.response(
                "Post metric found with successfully",
                200,
                request.getRequestURL().toString(),
                metric,
                true
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/getAll")
    @RateLimit(capacity = 26, refillTokens = 2, refillSeconds = 8)
    @SecurityRequirement(name = "bearerAuth")
    public Page<Post> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection,
            @RequestParam(required = false) LocalDateTime createdAtBefore,
            @RequestParam(required = false) LocalDateTime createdAtAfter,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long likesBefore,
            @RequestParam(required = false) Long likesAfter,
            @RequestParam(required = false) Long dislikesBefore,
            @RequestParam(required = false) Long dislikesAfter,
            @RequestParam(required = false) Long commentsCount,
            @RequestParam(required = false) Long favorites,
            @RequestParam(required = false) Long viewed

            ) {

        List<String> validSortFields = List.of("createdAt", "title", "likes", "dislikes", "commentsCount", "favorites", "viewed");
        if (!validSortFields.contains(sortBy)) {
            sortBy = "createdAt";
        }

        Sort.Direction direction = sortDirection.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Specification<Post> spec = PostSpecification.filterBy(
                createdAtBefore,
                createdAtAfter,
                title,
                categoryId,
                likesBefore,
                likesAfter,
                dislikesBefore,
                dislikesAfter,
                commentsCount,
                favorites,
                viewed
        );
        return this.uow.postService.GetAll(pageable, spec);
    }

    @RateLimit(capacity = 10, refillTokens = 2, refillSeconds = 8)
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("{id}")
    public ResponseEntity<?> delete(@PathVariable Long id, HttpServletRequest request){
        Post post = this.uow.postService.Get(id);
        Post postDeleted = this.uow.postService.Delete(post);
        UserMetrics userMetrics = this.uow.userMetricsService.get(postDeleted.getUser());
        this.uow.userMetricsService.sumOrRedPostsCount(userMetrics, SumOrReduce.REDUCE);

        var response = this.uow.responseDefault.response(
                "Post deleted with successfully",
                200,
                request.getRequestURL().toString(),
                null,
                true
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
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

        User user = this.uow.userService.getV2(userId);
        Category category = this.uow.categoryService.get(categoryId);

        Post post = this.uow.postService.Create(dto.MappearToPost(), user, category);
        this.uow.postMetricsService.create(post);
        UserMetrics userMetrics = this.uow.userMetricsService.get(post.getUser());
        this.uow.userMetricsService.sumOrRedPostsCount(userMetrics, SumOrReduce.SUM);
        this.uow.notificationsService.notifyFollowersAboutPostCreated(post);

        var response = this.uow.responseDefault.response(
                "Post created with successfully",
                200,
                request.getRequestURL().toString(),
                post,
                true
        );
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/{postId}")
    @RateLimit(capacity = 10, refillTokens = 2, refillSeconds = 8)
    public ResponseEntity<?> Update(
            @PathVariable Long postId,
            @RequestBody @Valid PostDTO dto,
            HttpServletRequest request
    ){
        Post postExist = this.uow.postService.Get(postId);
        Post post = this.uow.postService.Update(postExist, dto.MappearToPost());

        var response = this.uow.responseDefault.response(
                "Post updated with successfully",
                200,
                request.getRequestURL().toString(),
                post,
                true
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/GetAllByCategory/{categoryId}")
    @SecurityRequirement(name = "bearerAuth")
    @RateLimit(capacity = 12, refillTokens = 2, refillSeconds = 8)
    public Page<Post> GetAllByCategory(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Category category = this.uow.categoryService.get(categoryId);
        return this.uow.postService.GetAllByCategory(category, pageable);
    }

    @GetMapping("/filterByTitle/{title}")
    @SecurityRequirement(name = "bearerAuth")
    @RateLimit(capacity = 12, refillTokens = 2, refillSeconds = 8)
    public Page<Post> filterByTitle(
            @PathVariable String title,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return this.uow.postService.filterByTitle(title, pageable);
    }

}
