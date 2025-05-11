package br.com.Blog.api.controllers;

import br.com.Blog.api.DTOs.CategoryDTO;
import br.com.Blog.api.entities.Category;
import br.com.Blog.api.services.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/category")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService service;

    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public Category get(@PathVariable Long id){
        return this.service.get(id);
    }

    @GetMapping
    public ResponseEntity<?> getAll(){
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

    @PutMapping("{id}")
    public ResponseEntity<?> update(@PathVariable Long id ,@RequestBody @Valid CategoryDTO dto){
        return this.service.update(id, dto.MappearCategoryToUpdate());
    }

}


