package br.com.Blog.api.services;

import br.com.Blog.api.entities.Comment;
import br.com.Blog.api.entities.FavoriteComment;
import br.com.Blog.api.entities.User;
import br.com.Blog.api.repositories.FavoriteCommentRepository;
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
public class FavoriteCommentService {

    @Autowired
    private FavoriteCommentRepository repository;

    @Transactional(readOnly = true)
    public Page<FavoriteComment> GetAllFavoriteOfUser(User user, Pageable pageable){
        return this.repository.findAllByUser(user, pageable);
    }

    @Transactional
    public FavoriteComment Delete(FavoriteComment favorite){
        this.repository.delete(favorite);
        return favorite;
    }

    @Transactional
    public FavoriteComment create(Comment comment, User user){
        Boolean check = this.repository.existsByUserAndComment(user, comment);

        if (check)
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Comment already exists");

        FavoriteComment favorite = new FavoriteComment();

        favorite.setComment(comment);
        favorite.setUser(user);

        return this.repository.save(favorite);
    }

    @Transactional
    public Boolean existsItemSalve(User user, Comment comment){
        return this.repository.existsByUserAndComment(user, comment);
    }

    @Transactional
    public FavoriteComment get(Long favoriteId) {
        if (favoriteId <= 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        FavoriteComment favorite = this.repository.findById(favoriteId).orElse(null);

        if (favorite == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        return favorite;
    }

}
