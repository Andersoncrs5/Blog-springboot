package br.com.Blog.api.controllers;

import br.com.Blog.api.DTOs.PostDTO;
import br.com.Blog.api.services.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {

    private final PostService service;

    @GetMapping("{id}")
    public ResponseEntity<?> get(@PathVariable Long id){
        return this.service.Get(id);
    }

    @GetMapping
    public ResponseEntity<?> getAll(){
        return this.service.GetAll();
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> delete(@PathVariable Long id){
        return this.service.Delete(id);
    }

    @PostMapping("{idUser}")
    public ResponseEntity<?> Create(@RequestBody @Valid PostDTO dto, @PathVariable Long idUser){
        return this.service.Create(dto.MappearPostToCreate(), idUser);
    }

    @PutMapping
    public ResponseEntity<?> Update(@RequestBody @Valid PostDTO dto){
        return this.service.Update(dto.MappearPostToUpdate());
    }

}
