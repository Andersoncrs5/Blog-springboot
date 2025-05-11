package br.com.Blog.api.controllers;

import br.com.Blog.api.DTOs.LoginDTO;
import br.com.Blog.api.DTOs.UserDTO;
import br.com.Blog.api.config.JwtService;
import br.com.Blog.api.entities.User;
import br.com.Blog.api.services.UserService;
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
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;
    private final JwtService jwtService;

    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("me")
    @ResponseStatus(HttpStatus.OK)
    public User get(HttpServletRequest request) {

        String authHeader = request.getHeader("Authorization");
        String token = authHeader.substring(7);
        Long id = jwtService.extractUserId(token);

        return this.service.Get(id);
    }

    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/ListPostsOfUser")
    public ResponseEntity<?> ListPostsOfUser(
            HttpServletRequest request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);

        String authHeader = request.getHeader("Authorization");
        String token = authHeader.substring(7);
        Long id = jwtService.extractUserId(token);
        return this.service.ListPostsOfUser(id, pageable);
    }

    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/ListCommentsOfUser")
    public ResponseEntity<?> ListCommentsOfUser(
            HttpServletRequest request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);

        String authHeader = request.getHeader("Authorization");
        String token = authHeader.substring(7);
        Long id = jwtService.extractUserId(token);
        return this.service.ListCommentsOfUser(id, pageable);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid UserDTO dto){
        return this.service.Create(dto.MappearUserToCreate());
    }

    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping
    public ResponseEntity<?> delete(HttpServletRequest request) {

        String authHeader = request.getHeader("Authorization");
        String token = authHeader.substring(7);
        Long id = jwtService.extractUserId(token);
        return this.service.Delete(id);
    }

    @SecurityRequirement(name = "bearerAuth")
    @PutMapping
    public ResponseEntity<?> update(@RequestBody @Valid UserDTO dto, HttpServletRequest request) {

        String authHeader = request.getHeader("Authorization");
        String token = authHeader.substring(7);
        Long id = jwtService.extractUserId(token);
        return this.service.Update(id, dto.MappearUserToUpdate());
    }

    @PostMapping("login")
    public ResponseEntity<?> Login(@RequestBody @Valid LoginDTO dto){
        return this.service.Login(dto.email(), dto.password());
    }

}
