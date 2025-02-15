package br.com.Blog.api.controllers;

import br.com.Blog.api.DTOs.FavoritePostDTO;
import br.com.Blog.api.services.FavoritePostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class FavoritePostController {

    private final FavoritePostService service;

    @GetMapping("exists/{idUSer}/{idPost}")
    public ResponseEntity<?> exists(@PathVariable Long idUSer, @PathVariable Long idPost){
        return this.service.existsItemSalve(idUSer, idPost);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> delete(@PathVariable Long id){
        return this.service.Delete(id);
    }

    @GetMapping("GetAllFavoritePostOfUser/{idUser}")
    public ResponseEntity<?> GetAllFavoritePostOfUser(@PathVariable Long idUser){
        return this.service.GetAllFavoritePostOfUser(idUser);
    }

    @PostMapping()
    public ResponseEntity<?> delete(@RequestBody @Valid FavoritePostDTO dto){
        return this.service.create(dto);
    }

}
