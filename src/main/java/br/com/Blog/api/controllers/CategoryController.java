package br.com.Blog.api.controllers;

import br.com.Blog.api.DTOs.CategoryDTO;
import br.com.Blog.api.config.JwtService;
import br.com.Blog.api.config.annotation.RateLimit;
import br.com.Blog.api.entities.Category;
import br.com.Blog.api.services.CategoryService;
import br.com.Blog.api.services.response.ResponseDefault;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/category")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService service;
    private final JwtService jwtService;
    private final ResponseDefault responseDefault;

    @RateLimit(capacity = 20, refillTokens = 5, refillSeconds = 10)
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("{id}")
    public ResponseEntity<?> get(@PathVariable Long id){
        return new ResponseEntity<>(this.service.get(id), HttpStatus.OK);
    }

    @RateLimit(capacity = 20, refillTokens = 5, refillSeconds = 5)
    @GetMapping("")
    public ResponseEntity<?> getAll(){
        return new ResponseEntity<>(this.service.getAll(), HttpStatus.OK);
    }

    @RateLimit(capacity = 10, refillTokens = 5, refillSeconds = 10)
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id, HttpServletRequest request){
        this.service.delete(id);

        var response = this.responseDefault.response("Task deleted with successfully", 200, request.getRequestURL().toString(), "", true);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RateLimit(capacity = 20, refillTokens = 5, refillSeconds = 20)
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping
    public ResponseEntity<?> create(
            @RequestBody @Valid CategoryDTO dto,
            HttpServletRequest request
    ){

        Long id = jwtService.extractId(request);

        var result = this.service.create(dto.MappearCategoryToCreate(), id);
        var response = this.responseDefault.response("Category created with successfully", 201, request.getRequestURL().toString(), result, true);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @RateLimit(capacity = 20, refillTokens = 5, refillSeconds = 20)
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Long id ,@RequestBody @Valid CategoryDTO dto,
            HttpServletRequest request
            ){

        var result = this.service.update(id, dto.MappearCategoryToUpdate());
        var response = this.responseDefault.response("Category update with successfully", 200, request.getRequestURL().toString(), result, true);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}


