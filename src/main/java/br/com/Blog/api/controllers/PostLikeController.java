package br.com.Blog.api.controllers;

import br.com.Blog.api.config.JwtService;
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
@RequestMapping("/postLike")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class PostLikeController {

    private final PostLikeService service;
    private final JwtService jwtService;

    @PostMapping("/{type}/{postId}")
    public ResponseEntity<?> react(
            @PathVariable String type,
            @PathVariable Long postId,
            HttpServletRequest request
    ) {
        LikeOrUnLike action;

        action = LikeOrUnLike.valueOf(type.toUpperCase());
        String authHeader = request.getHeader("Authorization");
        String token = authHeader.substring(7);
        Long id = jwtService.extractUserId(token);

        return this.service.reactToPost(id, postId, action);
    }

    @DeleteMapping("/{likeId}")
    public ResponseEntity<?> remove(@PathVariable Long likeId) {
        return this.service.removeReaction(likeId);
    }

    @GetMapping("/{postId}")
    @ResponseStatus(HttpStatus.OK)
    public boolean exists(
            @PathVariable Long postId,
            HttpServletRequest request
    ) {
        String authHeader = request.getHeader("Authorization");
        String token = authHeader.substring(7);
        Long id = jwtService.extractUserId(token);

        return this.service.exists(id, postId);
    }

    public ResponseEntity<?> getAllByUser(
            HttpServletRequest request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);

        String authHeader = request.getHeader("Authorization");
        String token = authHeader.substring(7);
        Long id = jwtService.extractUserId(token);

        return this.service.getAllByUser(id, pageable);
    }

}
