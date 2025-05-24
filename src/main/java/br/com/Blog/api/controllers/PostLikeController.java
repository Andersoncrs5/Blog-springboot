package br.com.Blog.api.controllers;

import br.com.Blog.api.config.JwtService;
import br.com.Blog.api.config.annotation.RateLimit;
import br.com.Blog.api.entities.enums.LikeOrUnLike;
import br.com.Blog.api.services.PostLikeService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/postLike")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class PostLikeController {

    private final PostLikeService service;
    private final JwtService jwtService;

    @PostMapping("/{type}/{postId}")
    @RateLimit(capacity = 10, refillTokens = 2, refillSeconds = 8)
    public ResponseEntity<?> react(
            @PathVariable String type,
            @PathVariable Long postId,
            HttpServletRequest request
    ) {
        LikeOrUnLike action;

        action = LikeOrUnLike.valueOf(type.toUpperCase());
        Long id = jwtService.extractId(request);

        return this.service.reactToPost(id, postId, action);
    }

    @RateLimit(capacity = 10, refillTokens = 2, refillSeconds = 8)
    @DeleteMapping("/{likeId}")
    public ResponseEntity<?> remove(@PathVariable Long likeId) {
        return this.service.removeReaction(likeId);
    }

    @RateLimit(capacity = 10, refillTokens = 2, refillSeconds = 8)
    @GetMapping("/{postId}")
    @ResponseStatus(HttpStatus.OK)
    public boolean exists(
            @PathVariable Long postId,
            HttpServletRequest request
    ) {
        Long id = jwtService.extractId(request);

        return this.service.exists(id, postId);
    }

    @RateLimit(capacity = 10, refillTokens = 2, refillSeconds = 8)
    @GetMapping("/getAllByUser")
    public ResponseEntity<?> getAllByUser(
            HttpServletRequest request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Long id = jwtService.extractId(request);

        return this.service.getAllByUser(id, pageable);
    }

}
