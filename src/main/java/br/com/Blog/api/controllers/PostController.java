package br.com.Blog.api.controllers;

import br.com.Blog.api.DTOs.PostDTO;
import br.com.Blog.api.Specifications.PostSpecification;
import br.com.Blog.api.config.JwtService;
import br.com.Blog.api.entities.Category;
import br.com.Blog.api.entities.Post;
import br.com.Blog.api.services.PostLikeService;
import br.com.Blog.api.services.PostService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService service;
    private final JwtService jwtService;
    private final PostLikeService postLikeService;

    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public Post get(@PathVariable Long id){
        return this.service.Get(id);
    }

    @GetMapping
    public ResponseEntity<?> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) LocalDateTime createdAt,
            @RequestParam(required = false) String title

            ) {
        Pageable pageable = PageRequest.of(page, size);
        Specification<Post> spec = PostSpecification.filterBy(createdAt, title);
        return this.service.GetAll(pageable, spec);
    }

    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("{id}")
    public ResponseEntity<?> delete(@PathVariable Long id){
        return this.service.Delete(id);
    }

    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/{categoryId}")
    public ResponseEntity<?> Create(
            @RequestBody @Valid PostDTO dto,
            HttpServletRequest request,
            @PathVariable Long categoryId
    ) {

        String authHeader = request.getHeader("Authorization");
        String token = authHeader.substring(7);
        Long id = jwtService.extractUserId(token);
        return this.service.Create(dto.MappearPostToCreate(), id, categoryId);
    }

    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("{id}")
    public ResponseEntity<?> Update(@PathVariable Long id, @RequestBody @Valid PostDTO dto){
        return this.service.Update(id, dto.MappearPostToUpdate());
    }

    @GetMapping("/GetAllByCategory/{categoryId}")
    public ResponseEntity<?> GetAllByCategory(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return this.service.GetAllByCategory(categoryId, pageable);
    }

    @GetMapping("/countLikeByPost/{postId}")
    public ResponseEntity<?> countLikeByPost(@PathVariable Long postId) {
        return this.postLikeService.countLikeByPost(postId);
    }

    @GetMapping("/filterByTitle/{title}")
    public ResponseEntity<?> filterByTitle(
            @PathVariable String title,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return this.service.filterByTitle(title, pageable);
    }

}
