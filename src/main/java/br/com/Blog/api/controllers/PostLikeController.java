package br.com.Blog.api.controllers;

import br.com.Blog.api.config.JwtService;
import br.com.Blog.api.config.annotation.RateLimit;
import br.com.Blog.api.controllers.setUnitOfWork.UnitOfWork;
import br.com.Blog.api.entities.PostLike;
import br.com.Blog.api.entities.enums.LikeOrUnLike;
import br.com.Blog.api.services.PostLikeService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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

    private final UnitOfWork uow;

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

        String e = this.uow.postLikeService.reactToPost(id, postId, action);
        var response = this.uow.responseDefault.response(e,200,request.getRequestURL().toString(), null, true);
        return ResponseEntity.ok(response);
    }

    @RateLimit(capacity = 10, refillTokens = 2, refillSeconds = 8)
    @DeleteMapping("/{likeId}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> remove(@PathVariable Long likeId, HttpServletRequest request) {
        this.uow.postLikeService.removeReaction(likeId);
        var response = this.uow.responseDefault.response("Removed",200,request.getRequestURL().toString(), null, true);
        return ResponseEntity.ok(response);
    }

    @RateLimit(capacity = 10, refillTokens = 2, refillSeconds = 8)
    @GetMapping("/{postId}")
    @SecurityRequirement(name = "bearerAuth")
    @ResponseStatus(HttpStatus.OK)
    public boolean exists(
            @PathVariable Long postId,
            HttpServletRequest request
    ) {
        Long id = this.uow.jwtService.extractId(request);

        return this.uow.postLikeService.exists(id, postId);
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

        return this.uow.postLikeService.getAllByUser(id, pageable);
    }

}
