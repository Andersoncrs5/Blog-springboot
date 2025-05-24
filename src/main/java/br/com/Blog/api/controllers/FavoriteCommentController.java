package br.com.Blog.api.controllers;

import br.com.Blog.api.config.JwtService;
import br.com.Blog.api.services.FavoriteCommentService;
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

    private final FavoriteCommentService service;
    private final JwtService jwtService;

    @GetMapping("exists/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public Boolean exists(@PathVariable Long commentId, HttpServletRequest request) {
        Long id = jwtService.extractId(request);
        return this.service.existsItemSalve(id, commentId);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> delete(@PathVariable Long id){
        return this.service.Delete(id);
    }

    @GetMapping("GetAllFavoriteOfUser")
    public ResponseEntity<?> GetAllFavoriteOfUser(
            HttpServletRequest request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Long id = jwtService.extractId(request);

        return this.service.GetAllFavoriteOfUser(id, pageable);
    }

    @PostMapping("/{CommentId}")
    public ResponseEntity<?> create(@PathVariable Long CommentId , HttpServletRequest request) {
        Long id = jwtService.extractId(request);

        return this.service.create(CommentId, id);
    }
}
