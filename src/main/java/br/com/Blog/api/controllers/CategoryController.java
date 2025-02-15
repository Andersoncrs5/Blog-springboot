package br.com.Blog.api.controllers;

import br.com.Blog.api.DTOs.CategoryDTO;
import br.com.Blog.api.services.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/category")
public class CategoryController {

    private final CategoryService service;

    @GetMapping("{id}")
    public ResponseEntity<?> get(@PathVariable Long id){
        return this.service.get(id);
    }

    @GetMapping
    public ResponseEntity<?> get(){
        return this.service.getAll();
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> delete(@PathVariable Long id){
        return this.service.delete(id);
    }

    @PostMapping("{idUser}")
    public ResponseEntity<?> create(@RequestBody @Valid CategoryDTO dto, @PathVariable Long idUser){
        return this.service.create(dto.MappearCategoryToCreate(), idUser);
    }

    @PutMapping()
    public ResponseEntity<?> update(@RequestBody @Valid CategoryDTO dto){
        return this.service.update(dto.MappearCategoryToUpdate());
    }

}
