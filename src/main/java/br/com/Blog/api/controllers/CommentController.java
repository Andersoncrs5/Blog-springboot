package br.com.Blog.api.controllers;

import br.com.Blog.api.DTOs.CommentDTO;
import br.com.Blog.api.services.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comment")
public class CommentController {

    private final CommentService service;

    @GetMapping("{id}")
    public ResponseEntity<?> get(@PathVariable Long id){
        return this.service.Get(id);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> delete(@PathVariable Long id){
        return this.service.Delete(id);
    }

    @PostMapping("{idUser}/{idPost}")
    public ResponseEntity<?> Create(
            @RequestBody @Valid CommentDTO dto, @PathVariable Long idUser, @PathVariable Long idPost){
        return this.service.Create(dto.MappearCommentToCreate(), idUser, idPost);
    }

    @PutMapping
    public ResponseEntity<?> Update(@RequestBody @Valid CommentDTO dto){
        return this.service.Update(dto.MappearCommentToUpdate());
    }

    @GetMapping("/GetAllCommentsOfPost/{id}")
    public ResponseEntity<?> GetAllCommentsOfPost(@PathVariable Long id){
        return this.service.GetAllCommentsOfPost(id);
    }

}