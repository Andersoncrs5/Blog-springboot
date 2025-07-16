package br.com.Blog.api.controllers;

import br.com.Blog.api.DTOs.CategoryDTO;
import br.com.Blog.api.config.annotation.RateLimit;
import br.com.Blog.api.controllers.setUnitOfWork.UnitOfWork;
import br.com.Blog.api.entities.Category;
import br.com.Blog.api.entities.User;
import br.com.Blog.api.services.response.ResponseDefault;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/category")
@RequiredArgsConstructor
public class CategoryController {

    private final ResponseDefault responseDefault;
    private final UnitOfWork uow;

    @RateLimit(capacity = 20, refillTokens = 5, refillSeconds = 10)
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable Long id, HttpServletRequest request){
        Category category = this.uow.categoryService.get(id);
        var response = this.uow.responseDefault.response(
                "Category found with successfully",
                200,
                request.getRequestURL().toString(),
                category,
                true
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RateLimit(capacity = 22, refillTokens = 5, refillSeconds = 5)
    @GetMapping
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> getAll(){
        List<Category> allV2 = this.uow.categoryService.getAllV2();
        return ResponseEntity.ok(allV2);
    }

    @RateLimit(capacity = 10, refillTokens = 5, refillSeconds = 10)
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id, HttpServletRequest request){
        Category category = this.uow.categoryService.get(id);
        this.uow.categoryService.delete(category);

        var response = this.responseDefault.response(
                "Category deleted with successfully",
                200,
                request.getRequestURL().toString(),
                "",
                true
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RateLimit(capacity = 20, refillTokens = 5, refillSeconds = 20)
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/")
    public ResponseEntity<?> create(
            @RequestBody @Valid CategoryDTO dto,
            HttpServletRequest request
    ){
        Long id = this.uow.jwtService.extractId(request);
        User user = this.uow.userService.getV2(id);
        Category result = this.uow.categoryService.create(dto.MappearToCategory(), user);
        var response = this.responseDefault.response(
                "Category created with successfully",
                201,
                request.getRequestURL().toString(),
                result,
                true
        );
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @RateLimit(capacity = 20, refillTokens = 5, refillSeconds = 20)
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Long id ,@RequestBody @Valid CategoryDTO dto,
            HttpServletRequest request
            ){

        Category category = this.uow.categoryService.get(id);
        var result = this.uow.categoryService.update(category, dto.MappearToCategory());
        var response = this.responseDefault.response(
                "Category update with successfully",
                200,
                request.getRequestURL().toString(),
                result,
                true
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}


