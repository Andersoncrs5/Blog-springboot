package br.com.Blog.api.services;

import br.com.Blog.api.entities.FavoritePost;
import br.com.Blog.api.entities.Post;
import br.com.Blog.api.entities.User;
import br.com.Blog.api.repositories.FavoritePostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class FavoritePostService {

    @Autowired
    private FavoritePostRepository repository;

    @Transactional
    public Page<FavoritePost> GetAllFavoritePostOfUser(User user, Pageable pageable){
        return this.repository.findAllByUser(user, pageable);
    }

    @Transactional
    public FavoritePost Delete(FavoritePost favoritePost){
        this.repository.delete(favoritePost);

        return favoritePost;
    }

    @Transactional
    public FavoritePost create(Post post, User user){
        boolean check = this.repository.existsByUserAndPost(user, post);

        if (check)
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Item already exists");

        FavoritePost fpToCreate = new FavoritePost();

        fpToCreate.setPost(post);
        fpToCreate.setUser(user);

        return this.repository.save(fpToCreate);
    }

    @Transactional
    public boolean existsItemSalve(User user, Post post){
        return this.repository.existsByUserAndPost(user, post);
    }

    @Transactional
    public FavoritePost get(Long id) {
        if (id <= 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Id is required");

        FavoritePost favoritePost = this.repository.findById(id).orElse(null);

        if (favoritePost == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Favorite post not found");

        return favoritePost;
    }

}