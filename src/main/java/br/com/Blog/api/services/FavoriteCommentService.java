package br.com.Blog.api.services;

import br.com.Blog.api.entities.Comment;
import br.com.Blog.api.entities.FavoriteComment;
import br.com.Blog.api.entities.User;
import br.com.Blog.api.repositories.FavoriteCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class FavoriteCommentService {

    private final FavoriteCommentRepository repository;

    @Async
    @Transactional
    public Page<FavoriteComment> GetAllFavoriteOfUser(User user, Pageable pageable){
        return this.repository.findAllByUser(user, pageable);
    }

    @Async
    @Transactional
    public FavoriteComment Delete(Long idItem){
        FavoriteComment favorite = this.get(idItem);

        this.repository.delete(favorite);
        return favorite;
    }

    @Async
    @Transactional
    public FavoriteComment create(Comment comment, User user){
        Boolean check = this.repository.existsByUserAndComment(user, comment);

        if (check)
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Item already exists");

        FavoriteComment favorite = new FavoriteComment();

        favorite.setComment(comment);
        favorite.setUser(user);

        return this.repository.save(favorite);
    }

    @Async
    @Transactional
    public Boolean existsItemSalve(User user, Comment comment){
        return this.repository.existsByUserAndComment(user, comment);
    }

    @Async
    @Transactional
    public FavoriteComment get(Long favoriteId) {
        FavoriteComment favorite = this.repository.findById(favoriteId).orElse(null);

        if (favorite == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        return favorite;
    }

}
