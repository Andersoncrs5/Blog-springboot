package br.com.Blog.api.controllers;

import br.com.Blog.api.config.annotation.RateLimit;
import br.com.Blog.api.controllers.setUnitOfWork.UnitOfWork;
import br.com.Blog.api.entities.FavoritePost;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/v1/favoritePost")
@RequiredArgsConstructor
public class FavoritePostController {

    private final UnitOfWork uow;

    @RateLimit(capacity = 20, refillTokens = 2, refillSeconds = 8)
    @GetMapping("exists/{idPost}")
    public boolean exists(@PathVariable Long idPost, HttpServletRequest request) {
        Long id = this.uow.jwtService.extractId(request);
        return this.uow.favoritePostService.existsItemSalve(id, idPost);
    }

    @DeleteMapping("{id}")
    @RateLimit(capacity = 15, refillTokens = 2, refillSeconds = 8)
    public ResponseEntity<?> delete(@PathVariable Long id, HttpServletRequest request){
        FavoritePost favoritePost = this.uow.favoritePostService.Delete(id);
        this.uow.postMetricsService.sumOrReduceFavorite(favoritePost.getPost(), ActionSumOrReduceComment.REDUCE);
        this.uow.userMetricsService.sumOrRedSavedPostsCount(favoritePost.getUser(), SumOrReduce.REDUCE);

        var response = this.uow.responseDefault.response("Removed",200,request.getRequestURL().toString(), null, true);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("GetAllFavoritePostOfUser")
    @Transactional(readOnly = true)
    @ResponseStatus(HttpStatus.OK)
    @RateLimit(capacity = 10, refillTokens = 2, refillSeconds = 10)
    public Page<FavoritePost> GetAllFavoritePostOfUser(
            HttpServletRequest request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
        ) {
        Pageable pageable = PageRequest.of(page, size);
        Long id = this.uow.jwtService.extractId(request);

        return this.uow.favoritePostService.GetAllFavoritePostOfUser(id, pageable);
    }

    @PostMapping("/{postId}")
    @RateLimit(capacity = 10, refillTokens = 2, refillSeconds = 10)
    public ResponseEntity<?> create(@PathVariable Long postId , HttpServletRequest request) {

        Long id = this.uow.jwtService.extractId(request);
        FavoritePost favoritePost = this.uow.favoritePostService.create(postId, id);

        this.uow.userMetricsService.sumOrRedSavedPostsCount(favoritePost.getUser(), SumOrReduce.SUM);
        this.uow.postMetricsService.sumOrReduceFavorite(favoritePost.getPost(), ActionSumOrReduceComment.SUM);

        var response = this.uow.responseDefault.response("Favorited",200,request.getRequestURL().toString(), favoritePost, true);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

}
