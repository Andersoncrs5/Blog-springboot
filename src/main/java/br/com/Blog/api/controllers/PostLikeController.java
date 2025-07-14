package br.com.Blog.api.controllers;

import br.com.Blog.api.config.annotation.RateLimit;
import br.com.Blog.api.controllers.setUnitOfWork.UnitOfWork;
import br.com.Blog.api.entities.*;
import br.com.Blog.api.entities.enums.ActionSumOrReduceComment;
import br.com.Blog.api.entities.enums.LikeOrUnLike;
import br.com.Blog.api.entities.enums.SumOrReduce;
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
        User user = this.uow.userService.getV2(id);
        Post post = this.uow.postService.Get(postId);

        PostLike e = this.uow.postLikeService.reactToPost(user, post, action);

        PostMetrics postMetrics = this.uow.postMetricsService.get(post);

        this.uow.postMetricsService.sumOrReduceLikeOrDislike(postMetrics, ActionSumOrReduceComment.SUM, action);
        UserMetrics userMetrics = this.uow.userMetricsService.get(user);
        this.uow.userMetricsService.sumOrRedLikesOrDislikeGivenCount(userMetrics, SumOrReduce.SUM, action);

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
        PostLike reaction = this.uow.postLikeService.removeReaction(likeId);

        PostMetrics postMetrics = this.uow.postMetricsService.get(reaction.getPost());

        UserMetrics userMetrics = this.uow.userMetricsService.get(reaction.getUser());
        this.uow.postMetricsService.sumOrReduceLikeOrDislike(postMetrics, ActionSumOrReduceComment.REDUCE, reaction.getStatus());
        this.uow.userMetricsService.sumOrRedLikesOrDislikeGivenCount(userMetrics, SumOrReduce.REDUCE, reaction.getStatus());

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
        User user = this.uow.userService.getV2(id);
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
        User user = this.uow.userService.getV2(id);

        return this.uow.postLikeService.getAllByUser(user, pageable);
    }

}
