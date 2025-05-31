package br.com.Blog.api.controllers;

import br.com.Blog.api.config.annotation.RateLimit;
import br.com.Blog.api.controllers.setUnitOfWork.UnitOfWork;
import br.com.Blog.api.entities.Post;
import br.com.Blog.api.entities.PostLike;
import br.com.Blog.api.entities.User;
import br.com.Blog.api.entities.enums.LikeOrUnLike;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/v1/postLike")
@SecurityRequirement(name = "bearerAuth")
public class PostLikeController {

    @Autowired
    private UnitOfWork uow;

    @PostMapping("/{type}/{postId}")
    @SecurityRequirement(name = "bearerAuth")
    @RateLimit(capacity = 10, refillTokens = 2, refillSeconds = 8)
    public ResponseEntity<?> react(
            @PathVariable String type,
            @PathVariable Long postId,
            HttpServletRequest request
    ) {
        LikeOrUnLike action;

        action = LikeOrUnLike.valueOf(type.toUpperCase());
        Long id = this.uow.jwtService.extractId(request);
        User user = this.uow.userService.get(id);
        Post post = this.uow.postService.Get(postId);

        PostLike e = this.uow.postLikeService.reactToPost(user, post, action);

        var response = this.uow.responseDefault.response(
                "Reaction added successfully",
                200,
                request.getRequestURL().toString(),
                e,
                true
        );

        return ResponseEntity.ok(response);
    }

    @RateLimit(capacity = 10, refillTokens = 2, refillSeconds = 8)
    @DeleteMapping("/{likeId}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> remove(@PathVariable Long likeId, HttpServletRequest request) {
        this.uow.postLikeService.removeReaction(likeId);
        var response = this.uow.responseDefault.response(
                "Action Removed",
                200,
                request.getRequestURL().toString(),
                null,
                true
        );
        return ResponseEntity.ok(response);
    }

    @RateLimit(capacity = 10, refillTokens = 2, refillSeconds = 8)
    @GetMapping("exists/{postId}")
    @SecurityRequirement(name = "bearerAuth")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Boolean> exists(
            @PathVariable Long postId,
            HttpServletRequest request
    ) {
        Long id = this.uow.jwtService.extractId(request);
        User user = this.uow.userService.get(id);
        Post post = this.uow.postService.Get(postId);

        var result = this.uow.postLikeService.exists(user, post);
        return Map.of("result", result);
    }

    @RateLimit(capacity = 10, refillTokens = 2, refillSeconds = 8)
    @GetMapping("/getAllByUser")
    @SecurityRequirement(name = "bearerAuth")
    public Page<PostLike> getAllByUser(
            HttpServletRequest request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Long id = this.uow.jwtService.extractId(request);
        User user = this.uow.userService.get(id);

        return this.uow.postLikeService.getAllByUser(user, pageable);
    }

}
