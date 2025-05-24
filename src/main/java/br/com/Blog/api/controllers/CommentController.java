package br.com.Blog.api.controllers;

import br.com.Blog.api.DTOs.CommentDTO;
import br.com.Blog.api.config.JwtService;
import br.com.Blog.api.config.annotation.RateLimit;
import br.com.Blog.api.entities.Comment;
import br.com.Blog.api.services.CommentMetricsService;
import br.com.Blog.api.services.CommentService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/comment")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService service;
    private final JwtService jwtService;
    private final CommentMetricsService metricsService;

    @RateLimit(capacity = 20, refillTokens = 5, refillSeconds = 10)
    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public Comment get(@PathVariable Long id){
        Comment comment = this.service.Get(id);
        this.metricsService.sumView(comment);

        return comment;
    }

    @RateLimit(capacity = 20, refillTokens = 5, refillSeconds = 15)
    @DeleteMapping("{id}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> delete(@PathVariable Long id){
        return this.service.Delete(id);
    }

    @PostMapping("/{postId}")
    @SecurityRequirement(name = "bearerAuth")
    @RateLimit(capacity = 20, refillTokens = 2, refillSeconds = 15)
    public ResponseEntity<?> Create(
            @RequestBody @Valid CommentDTO dto,
            @PathVariable Long postId,
            HttpServletRequest request
    ) {
        Long id = jwtService.extractId(request);
        return this.service.Create(dto.MappearCommentToCreate(), id, postId);
    }

    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/{id}")
    @RateLimit(capacity = 10, refillTokens = 2, refillSeconds = 15)
    public ResponseEntity<?> Update(@RequestBody @Valid CommentDTO dto, @PathVariable Long id){
        return this.service.Update(id, dto.MappearCommentToUpdate());
    }

    @RateLimit(capacity = 20, refillTokens = 5, refillSeconds = 10)
    @GetMapping("/GetAllCommentsOfPost/{id}")
    public ResponseEntity<?> GetAllCommentsOfPost(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return this.service.GetAllCommentsOfPost(id, pageable);
    }

}