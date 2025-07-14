package br.com.Blog.api.controllers;

import br.com.Blog.api.config.annotation.RateLimit;
import br.com.Blog.api.controllers.setUnitOfWork.UnitOfWork;
import br.com.Blog.api.entities.Comment;
import br.com.Blog.api.entities.CommentLike;
import br.com.Blog.api.entities.CommentMetrics;
import br.com.Blog.api.entities.User;
import br.com.Blog.api.entities.enums.ActionSumOrReduceComment;
import br.com.Blog.api.entities.enums.LikeOrUnLike;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/v1/commentLike")
@RequiredArgsConstructor
public class CommentLikeController {

    private final UnitOfWork uow;

    @RateLimit(capacity = 20, refillTokens = 5, refillSeconds = 10)
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/{type}/{commentId}")
    public ResponseEntity<?> react(
            @PathVariable String type,
            @PathVariable Long commentId,
            HttpServletRequest request
    ) {

        Long userId = this.uow.jwtService.extractId(request);
        LikeOrUnLike action;
        User user = this.uow.userService.getV2(userId);
        Comment comment = this.uow.commentService.Get(commentId);
        action = LikeOrUnLike.valueOf(type.toUpperCase());
        CommentLike commentLike = this.uow.commentLikeService.reactToComment(user, comment, action);
        CommentMetrics metrics = this.uow.commentMetricsService.get(comment);

        this.uow.commentMetricsService.sumOrRedLikeOrDislike(metrics, ActionSumOrReduceComment.SUM, action);

        Map<String, Object> response = this.uow.responseDefault.response(
                commentLike.getStatus() + " added",
                200,
                request.getRequestURL().toString(),
                commentLike,
                true
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RateLimit(capacity = 20, refillTokens = 5, refillSeconds = 10)
    @DeleteMapping("/{likeId}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> remove(@PathVariable Long likeId, HttpServletRequest request) {
        CommentLike commentLike = this.uow.commentLikeService.removeReaction(likeId);

        CommentMetrics metrics = this.uow.commentMetricsService.get(commentLike.getComment());

        this.uow.commentMetricsService.sumOrRedLikeOrDislike(metrics, ActionSumOrReduceComment.REDUCE, commentLike.getStatus());

        Map<String, Object> response = this.uow.responseDefault.response(
                commentLike.getStatus() + " removed",
                200,
                request.getRequestURL().toString(),
                commentLike,
                true
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RateLimit(capacity = 20, refillTokens = 5, refillSeconds = 10)
    @GetMapping("/exists/{commentId}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> exists(
            @PathVariable Long commentId,
            HttpServletRequest request
    ) {
        Long userId = this.uow.jwtService.extractId(request);
        boolean exists = this.uow.commentLikeService.exists(userId, commentId);

        Map<String, Object> response = this.uow.responseDefault.response(
                "",
                200,
                request.getRequestURL().toString(),
                exists,
                true
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RateLimit(capacity = 20, refillTokens = 5, refillSeconds = 8)
    @GetMapping("/getAllByUser")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> getAllByUser(
            HttpServletRequest request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Long userId = this.uow.jwtService.extractId(request);
        User user = this.uow.userService.getV2(userId);
        Pageable pageable = PageRequest.of(page, size);
        return this.uow.commentLikeService.getAllByUser(user, pageable);
    }
}
