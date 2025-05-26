package br.com.Blog.api.controllers;

import br.com.Blog.api.config.annotation.RateLimit;
import br.com.Blog.api.controllers.setUnitOfWork.UnitOfWork;
import br.com.Blog.api.entities.FavoriteComment;
import br.com.Blog.api.entities.enums.ActionSumOrReduceComment;
import br.com.Blog.api.entities.enums.SumOrReduce;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/v1/favoriteComment")
@RequiredArgsConstructor
public class FavoriteCommentController {

    private final UnitOfWork uow;

    @RateLimit(capacity = 20, refillTokens = 5, refillSeconds = 10)
    @GetMapping("exists/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public Boolean exists(@PathVariable Long commentId, HttpServletRequest request) {
        Long id = this.uow.jwtService.extractId(request);
        return this.uow.favoriteCommentService.existsItemSalve(id, commentId);
    }

    @RateLimit(capacity = 20, refillTokens = 5, refillSeconds = 15)
    @DeleteMapping("{id}")
    public ResponseEntity<?> delete(@PathVariable Long id){
        FavoriteComment favorite = this.uow.favoriteCommentService.Delete(id);
        this.uow.commentMetricsService.sumOrReduceFavorite(favorite.getComment(), ActionSumOrReduceComment.REDUCE);
        this.uow.userMetricsService.sumOrRedSavedCommentsCount(favorite.getUser(), SumOrReduce.REDUCE);

        return ResponseEntity.ok().body("Removed");
    }

    @RateLimit(capacity = 20, refillTokens = 5, refillSeconds = 15)
    @GetMapping("GetAllFavoriteOfUser")
    public ResponseEntity<?> GetAllFavoriteOfUser(
            HttpServletRequest request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Long id = this.uow.jwtService.extractId(request);

        return this.uow.favoriteCommentService.GetAllFavoriteOfUser(id, pageable);
    }

    @PostMapping("/{CommentId}")
    @RateLimit(capacity = 10, refillTokens = 2, refillSeconds = 8)
    public ResponseEntity<?> create(@PathVariable Long CommentId , HttpServletRequest request) {
        Long id = this.uow.jwtService.extractId(request);

        FavoriteComment favorite = this.uow.favoriteCommentService.create(CommentId, id);

        this.uow.userMetricsService.sumOrRedSavedCommentsCount(favorite.getUser(), SumOrReduce.SUM);
        this.uow.commentMetricsService.sumOrReduceFavorite(favorite.getComment(), ActionSumOrReduceComment.SUM);

        return new ResponseEntity<>("Comment favorited!", HttpStatus.CREATED);
    }
}
