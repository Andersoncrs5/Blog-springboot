package br.com.Blog.api.controllers;

import br.com.Blog.api.config.annotation.RateLimit;
import br.com.Blog.api.controllers.setUnitOfWork.UnitOfWork;
import br.com.Blog.api.entities.*;
import br.com.Blog.api.entities.enums.ActionSumOrReduceComment;
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

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/v1/favoriteComment")
public class FavoriteCommentController {

    @Autowired
    private UnitOfWork uow;

    @RateLimit(capacity = 20, refillTokens = 5, refillSeconds = 10)
    @GetMapping("exists/{commentId}")
    @SecurityRequirement(name = "bearerAuth")
    @ResponseStatus(HttpStatus.OK)
    public Map<String ,Boolean> exists(@PathVariable Long commentId, HttpServletRequest request) {
        Long id = this.uow.jwtService.extractId(request);
        User user = this.uow.userService.get(id);
        Comment comment = this.uow.commentService.Get(commentId);
        var result = this.uow.favoriteCommentService.existsItemSalve(user, comment);

        return Map.of("result", result);
    }

    @RateLimit(capacity = 20, refillTokens = 5, refillSeconds = 15)
    @DeleteMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> delete(@PathVariable Long id, HttpServletRequest request){
        FavoriteComment item = this.uow.favoriteCommentService.get(id);
        FavoriteComment favorite = this.uow.favoriteCommentService.Delete(item);
        CommentMetrics commentMetrics = this.uow.commentMetricsService.get(favorite.getComment());
        this.uow.commentMetricsService.sumOrReduceFavorite(commentMetrics, ActionSumOrReduceComment.REDUCE);
        UserMetrics userMetrics = this.uow.userMetricsService.get(favorite.getUser());
        this.uow.userMetricsService.sumOrRedSavedCommentsCount(userMetrics, SumOrReduce.REDUCE);

        Map<String, Object> response = this.uow.responseDefault.response(
                "Comment removed with favorite!",
                200,
                request.getRequestURL().toString(),
                null,
                true
        );

        return ResponseEntity.ok().body(response);
    }

    @RateLimit(capacity = 20, refillTokens = 5, refillSeconds = 15)
    @GetMapping("/GetAllFavoriteOfUser")
    @SecurityRequirement(name = "bearerAuth")
    @ResponseStatus(HttpStatus.OK)
    public Page<FavoriteComment> GetAllFavoriteOfUser(
            HttpServletRequest request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Long id = this.uow.jwtService.extractId(request);
        User user = this.uow.userService.getV2(id);

        return this.uow.favoriteCommentService.GetAllFavoriteOfUser(user, pageable);
    }

    @PostMapping("/add/{commentId}")
    @SecurityRequirement(name = "bearerAuth")
    @RateLimit(capacity = 10, refillTokens = 2, refillSeconds = 8)
    public ResponseEntity<?> create(@PathVariable Long commentId , HttpServletRequest request) {
        Long id = this.uow.jwtService.extractId(request);

        User user = this.uow.userService.getV2(id);
        Comment comment = this.uow.commentService.Get(commentId);
        FavoriteComment favorite = this.uow.favoriteCommentService.create(comment, user);

        UserMetrics userMetrics = this.uow.userMetricsService.get(favorite.getUser());
        this.uow.userMetricsService.sumOrRedSavedCommentsCount(userMetrics, SumOrReduce.SUM);
        CommentMetrics commentMetrics = this.uow.commentMetricsService.get(favorite.getComment());
        this.uow.commentMetricsService.sumOrReduceFavorite(commentMetrics, ActionSumOrReduceComment.SUM);

        Map<String, Object> response = this.uow.responseDefault.response(
                "Comment has been favorited successfully!!",
                201,
                request.getRequestURL().toString(),
                favorite,
                true
        );
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
