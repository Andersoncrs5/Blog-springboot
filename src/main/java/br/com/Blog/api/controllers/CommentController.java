package br.com.Blog.api.controllers;

import br.com.Blog.api.DTOs.CommentDTO;
import br.com.Blog.api.config.JwtService;
import br.com.Blog.api.entities.Comment;
import br.com.Blog.api.services.CommentService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comment")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService service;
    private final JwtService jwtService;

    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public Comment get(@PathVariable Long id){
        return this.service.Get(id);
    }

    @DeleteMapping("{id}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> delete(@PathVariable Long id){
        return this.service.Delete(id);
    }

    @PostMapping("/{idPost}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> Create(
            @RequestBody @Valid CommentDTO dto,
            @PathVariable Long idPost,
            HttpServletRequest request
    ) {

        String authHeader = request.getHeader("Authorization");
        String token = authHeader.substring(7);
        Long id = jwtService.extractUserId(token);
        return this.service.Create(dto.MappearCommentToCreate(), id, idPost);
    }

    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/{id}")
    public ResponseEntity<?> Update(@RequestBody @Valid CommentDTO dto, @PathVariable Long id){
        return this.service.Update(id, dto.MappearCommentToUpdate());
    }

    @GetMapping("/GetAllCommentsOfPost/{id}")
    public ResponseEntity<?> GetAllCommentsOfPost(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return this.service.GetAllCommentsOfPost(id, pageable);
    }

}