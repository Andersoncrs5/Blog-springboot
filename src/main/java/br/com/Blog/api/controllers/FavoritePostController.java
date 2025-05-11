package br.com.Blog.api.controllers;

import br.com.Blog.api.config.JwtService;
import br.com.Blog.api.services.FavoritePostService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/favorite")
@RequiredArgsConstructor
public class FavoritePostController {

    private final FavoritePostService service;
    private final JwtService jwtService;

    @GetMapping("exists/{idPost}")
    public ResponseEntity<?> exists(@PathVariable Long idPost, HttpServletRequest request) {

        String authHeader = request.getHeader("Authorization");
        String token = authHeader.substring(7);
        Long id = jwtService.extractUserId(token);
        return this.service.existsItemSalve(id, idPost);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> delete(@PathVariable Long id){
        return this.service.Delete(id);
    }

    @GetMapping("GetAllFavoritePostOfUser")
    public ResponseEntity<?> GetAllFavoritePostOfUser(
            HttpServletRequest request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
        ) {
        Pageable pageable = PageRequest.of(page, size);

        String authHeader = request.getHeader("Authorization");
        String token = authHeader.substring(7);
        Long id = jwtService.extractUserId(token);
        return this.service.GetAllFavoritePostOfUser(id, pageable);
    }

    @PostMapping("/{postId}")
    public ResponseEntity<?> delete(@PathVariable Long postId , HttpServletRequest request) {

        String authHeader = request.getHeader("Authorization");
        String token = authHeader.substring(7);
        Long id = jwtService.extractUserId(token);
        return this.service.create(postId, id);
    }

}
