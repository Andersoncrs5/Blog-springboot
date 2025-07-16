package br.com.Blog.api.services;

import br.com.Blog.api.entities.FavoritePost;
import br.com.Blog.api.entities.Post;
import br.com.Blog.api.entities.User;
import br.com.Blog.api.repositories.FavoritePostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
public class FavoritePostService {

    @Autowired
    private FavoritePostRepository repository;

    @Transactional
    public Page<FavoritePost> GetAllFavoritePostOfUser(User user, Pageable pageable){
        log.info("Searching all favorite posts of user");
        Page<FavoritePost> allByUser = this.repository.findAllByUser(user, pageable);
        log.info("Returning all favorite posts of user");
        return allByUser;
    }

    @Transactional
    public FavoritePost Delete(FavoritePost favoritePost) {
        log.info("Deleting favorite post....");
        this.repository.delete(favoritePost);

        log.info("Favorite post deleted!");
        return favoritePost;
    }

    @Transactional
    public FavoritePost create(Post post, User user){
        log.info("Saving new favorite post...");
        log.info("checking if favorite post exists...");
        boolean check = this.repository.existsByUserAndPost(user, post);

        if (check) {
            log.info("Favorite post exists");
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Item already exists");
        }

        FavoritePost fpToCreate = new FavoritePost();

        fpToCreate.setPost(post);
        fpToCreate.setUser(user);

        FavoritePost save = this.repository.save(fpToCreate);
        log.info("Favorite post saved!");
        return save;
    }

    @Transactional
    public boolean existsItemSalve(User user, Post post){
        log.info("Checking favorite post exists...");
        boolean exists = this.repository.existsByUserAndPost(user, post);
        log.info("returning resulted");
        return exists;
    }

    @Transactional
    public FavoritePost get(Long id) {
        log.info("Getting favorite post by id: " + id);
        if (id <= 0) {
            log.info("id is null! Returning ResponseStatusException...");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Id is required");
        }

        log.info("Searching favorite post in database....");
        FavoritePost favoritePost = this.repository.findById(id).orElse(null);

        if (favoritePost == null) {
            log.info("Favorite post not found! Returning ResponseStatusException...");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Favorite post not found");
        }

        log.info("Favorite post founded! Returning entity...");
        return favoritePost;
    }

}