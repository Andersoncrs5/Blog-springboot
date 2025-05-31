package br.com.Blog.api.controllers;

import br.com.Blog.api.config.annotation.RateLimit;
import br.com.Blog.api.controllers.setUnitOfWork.UnitOfWork;
import br.com.Blog.api.entities.FavoritePost;
import br.com.Blog.api.entities.Post;
import br.com.Blog.api.entities.PostMetrics;
import br.com.Blog.api.entities.User;
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

@RestController
@RequestMapping("/v1/favoritePost")
public class FavoritePostController {

    @Autowired
    private UnitOfWork uow;

    @RateLimit(capacity = 20, refillTokens = 2, refillSeconds = 8)
    @GetMapping("exists/{idPost}")
    @SecurityRequirement(name = "bearerAuth")
    public Map<String, Boolean> exists(@PathVariable Long idPost, HttpServletRequest request) {
        Long id = this.uow.jwtService.extractId(request);
        var result = this.uow.favoritePostService.existsItemSalve(id, idPost);

        return Map.of("result", result);
    }

    @DeleteMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @RateLimit(capacity = 15, refillTokens = 2, refillSeconds = 8)
    public ResponseEntity<?> delete(@PathVariable Long id, HttpServletRequest request){
        FavoritePost favoritePost = this.uow.favoritePostService.Delete(id);
        PostMetrics metrics = this.uow.postMetricsService.get(favoritePost.getPost());
        this.uow.postMetricsService.sumOrReduceFavorite(metrics, ActionSumOrReduceComment.REDUCE);
        this.uow.userMetricsService.sumOrRedSavedPostsCount(favoritePost.getUser(), SumOrReduce.REDUCE);

        var response = this.uow.responseDefault.response(
                "Post removed with favorite!",
                200,
                request.getRequestURL().toString(),
                null,
                true
        );
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/GetAllFavoritePostOfUser")
    @ResponseStatus(HttpStatus.OK)
    @SecurityRequirement(name = "bearerAuth")
    @RateLimit(capacity = 10, refillTokens = 2, refillSeconds = 10)
    public Page<FavoritePost> GetAllFavoritePostOfUser(
            HttpServletRequest request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
        ) {
        Pageable pageable = PageRequest.of(page, size);
        Long id = this.uow.jwtService.extractId(request);
        User user = this.uow.userService.get(id);

        return this.uow.favoritePostService.GetAllFavoritePostOfUser(user, pageable);
    }

    @PostMapping("/add/{postId}")
    @RateLimit(capacity = 14, refillTokens = 2, refillSeconds = 8)
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> create(@PathVariable Long postId , HttpServletRequest request) {
        Long id = this.uow.jwtService.extractId(request);
        User user = this.uow.userService.get(id);
        Post post = this.uow.postService.Get(postId);

        FavoritePost favoritePost = this.uow.favoritePostService.create(post, user);
        PostMetrics metrics = this.uow.postMetricsService.get(post);
        this.uow.userMetricsService.sumOrRedSavedPostsCount(user, SumOrReduce.SUM);
        this.uow.postMetricsService.sumOrReduceFavorite(metrics, ActionSumOrReduceComment.SUM);

        Map<String, Object> response = this.uow.responseDefault.response(
                "Post has been favorited successfully",
                201,
                request.getRequestURL().toString(),
                favoritePost,
                true
        );

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

}
