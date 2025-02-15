package br.com.Blog.api.controllers;

import br.com.Blog.api.DTOs.LoginDTO;
import br.com.Blog.api.DTOs.UserDTO;
import br.com.Blog.api.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService service;

    @GetMapping("{id}")
    public ResponseEntity<?> get(@PathVariable Long id){
        return this.service.Get(id);
    }

    @GetMapping("/ListPostsOfUser/{id}")
    public ResponseEntity<?> ListPostsOfUser(@PathVariable Long id){
        return this.service.ListPostsOfUser(id);
    }

    @GetMapping("/ListCommentsOfUser/{id}")
    public ResponseEntity<?> ListCommentsOfUser(@PathVariable Long id){
        return this.service.ListCommentsOfUser(id);
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody @Valid UserDTO dto){
        return this.service.Create(dto.MappearUserToCreate());
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> delete(@PathVariable Long id){
        return this.service.Delete(id);
    }

    @PutMapping
    public ResponseEntity<?> update(@RequestBody @Valid UserDTO dto){
        return this.service.Update(dto.MappearUserToUpdate());
    }

    @PostMapping("Login")
    public ResponseEntity<?> Login(@RequestBody @Valid LoginDTO dto){
        return this.service.Login(dto);
    }

}
