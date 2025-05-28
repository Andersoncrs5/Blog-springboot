package br.com.Blog.api.controllers;

import br.com.Blog.api.config.annotation.RateLimit;
import br.com.Blog.api.controllers.setUnitOfWork.UnitOfWork;
import br.com.Blog.api.entities.FavoriteComment;
import br.com.Blog.api.entities.enums.ActionSumOrReduceComment;
import br.com.Blog.api.entities.enums.SumOrReduce;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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
    @SecurityRequirement(name = "bearerAuth")
    @ResponseStatus(HttpStatus.OK)
    public Boolean exists(@PathVariable Long commentId, HttpServletRequest request) {
        Long id = this.uow.jwtService.extractId(request);
        return this.uow.favoriteCommentService.existsItemSalve(id, commentId);
    }

    @RateLimit(capacity = 20, refillTokens = 5, refillSeconds = 15)
    @DeleteMapping("{id}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> delete(@PathVariable Long id, HttpServletRequest request){
        FavoriteComment favorite = this.uow.favoriteCommentService.Delete(id);
        this.uow.commentMetricsService.sumOrReduceFavorite(favorite.getComment(), ActionSumOrReduceComment.REDUCE);
        this.uow.userMetricsService.sumOrRedSavedCommentsCount(favorite.getUser(), SumOrReduce.REDUCE);

        var response = this.uow.responseDefault.response("Removed",201,request.getRequestURL().toString(), null, true);

        return ResponseEntity.ok().body(response);
    }

    @RateLimit(capacity = 20, refillTokens = 5, refillSeconds = 15)
    @GetMapping("GetAllFavoriteOfUser")
    @SecurityRequirement(name = "bearerAuth")
    public Page<FavoriteComment> GetAllFavoriteOfUser(
            HttpServletRequest request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Long id = this.uow.jwtService.extractId(request);

        return this.uow.favoriteCommentService.GetAllFavoriteOfUser(id, pageable);
    }

    @PostMapping("/{CommentId}")
    @SecurityRequirement(name = "bearerAuth")
    @RateLimit(capacity = 10, refillTokens = 2, refillSeconds = 8)
    public ResponseEntity<?> create(@PathVariable Long CommentId , HttpServletRequest request) {
        Long id = this.uow.jwtService.extractId(request);

        FavoriteComment favorite = this.uow.favoriteCommentService.create(CommentId, id);

        this.uow.userMetricsService.sumOrRedSavedCommentsCount(favorite.getUser(), SumOrReduce.SUM);
        this.uow.commentMetricsService.sumOrReduceFavorite(favorite.getComment(), ActionSumOrReduceComment.SUM);

        var response = this.uow.responseDefault.response("Comment add how favorite!!",201,request.getRequestURL().toString(), null, true);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
