package br.com.Blog.api.controllers;

import br.com.Blog.api.config.JwtService;
import br.com.Blog.api.entities.enums.LikeOrUnLike;
import br.com.Blog.api.services.CommentLikeService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/commentLike")
@RequiredArgsConstructor
public class CommentLikeController {

    private final CommentLikeService service;
    private final JwtService jwtService;

    private Long extractUserId(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        String token = authHeader != null && authHeader.startsWith("Bearer ")
                ? authHeader.substring(7)
                : null;

        if (token == null) throw new IllegalArgumentException("Authorization token is missing or invalid.");

        return jwtService.extractUserId(token);
    }

    @PostMapping("/{type}/{commentId}")
    public ResponseEntity<?> react(
            @PathVariable String type,
            @PathVariable Long commentId,
            HttpServletRequest request
    ) {

        Long userId = extractUserId(request);
        LikeOrUnLike action;
        action = LikeOrUnLike.valueOf(type.toUpperCase());
        return service.reactToComment(userId, commentId, action);
    }

    @DeleteMapping("/{likeId}")
    public ResponseEntity<?> remove(@PathVariable Long likeId) {
        return service.removeReaction(likeId);
    }

    @GetMapping("/exists/{commentId}")
    public boolean exists(
            @PathVariable Long commentId,
            HttpServletRequest request
    ) {
        Long userId = extractUserId(request);
        return service.exists(userId, commentId);
    }

    @GetMapping("/user")
    public ResponseEntity<?> getAllByUser(
            HttpServletRequest request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Long userId = extractUserId(request);
        Pageable pageable = PageRequest.of(page, size);
        return service.getAllByUser(userId, pageable);
    }
}
