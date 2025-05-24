package br.com.Blog.api.controllers;

import br.com.Blog.api.config.JwtService;
import br.com.Blog.api.config.annotation.RateLimit;
import br.com.Blog.api.services.FavoritePostService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/v1/favoritePost")
@RequiredArgsConstructor
public class FavoritePostController {

    private final FavoritePostService service;
    private final JwtService jwtService;

    @RateLimit(capacity = 20, refillTokens = 2, refillSeconds = 8)
    @GetMapping("exists/{idPost}")
    public ResponseEntity<?> exists(@PathVariable Long idPost, HttpServletRequest request) {
        Long id = jwtService.extractId(request);
        return this.service.existsItemSalve(id, idPost);
    }

    @DeleteMapping("{id}")
    @RateLimit(capacity = 15, refillTokens = 2, refillSeconds = 8)
    public ResponseEntity<?> delete(@PathVariable Long id){
        return this.service.Delete(id);
    }

    @GetMapping("GetAllFavoritePostOfUser")
    @Transactional(readOnly = true)
    @RateLimit(capacity = 10, refillTokens = 2, refillSeconds = 10)
    public ResponseEntity<?> GetAllFavoritePostOfUser(
            HttpServletRequest request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
        ) {
        Pageable pageable = PageRequest.of(page, size);
        Long id = jwtService.extractId(request);

        return this.service.GetAllFavoritePostOfUser(id, pageable);
    }

    @PostMapping("/{postId}")
    @RateLimit(capacity = 10, refillTokens = 2, refillSeconds = 10)
    public ResponseEntity<?> delete(@PathVariable Long postId , HttpServletRequest request) {

        Long id = jwtService.extractId(request);
        return this.service.create(postId, id);
    }

}
