package br.com.Blog.api.services;

import br.com.Blog.api.entities.Comment;
import br.com.Blog.api.entities.FavoriteComment;
import br.com.Blog.api.entities.User;
import br.com.Blog.api.entities.enums.ActionSumOrReduceComment;
import br.com.Blog.api.entities.enums.SumOrReduce;
import br.com.Blog.api.repositories.FavoriteCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class FavoriteCommentService {

    private final FavoriteCommentRepository repository;
    private final UserService userService;
    private final CommentService commentService;
    private final CommentMetricsService commentMetricsService;
    private final UserMetricsService userMetricsService;


    @Async
    @Transactional
    public ResponseEntity<?> GetAllFavoriteOfUser(Long userId, Pageable pageable){
        User user = this.userService.Get(userId);
        Page<FavoriteComment> page = this.repository.findAllByUser(user, pageable);
        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    @Async
    @Transactional
    public ResponseEntity<?> Delete(Long idItem){
        FavoriteComment favorite = this.get(idItem);

        this.repository.delete(favorite);
        this.commentMetricsService.sumOrReduceFavorite(favorite.getComment(), ActionSumOrReduceComment.REDUCE);
        this.userMetricsService.sumOrRedSavedCommentsCount(favorite.getUser(), SumOrReduce.REDUCE);
        return new ResponseEntity<>("favorite comment deleted ", HttpStatus.OK);
    }

    @Async
    @Transactional
    public ResponseEntity<?> create(Long commentId, Long userId){
        User user = this.userService.Get(userId);
        Comment comment = this.commentService.Get(commentId);

        Boolean check = this.repository.existsByUserAndComment(user, comment);

        if (!check)
            return new ResponseEntity<>("Item already exists", HttpStatus.CONFLICT);

        FavoriteComment favorite = new FavoriteComment();

        favorite.setComment(comment);
        favorite.setUser(user);

        this.userMetricsService.sumOrRedSavedCommentsCount(user, SumOrReduce.SUM);
        this.commentMetricsService.sumOrReduceFavorite(comment, ActionSumOrReduceComment.SUM);

        this.repository.save(favorite);
        return new ResponseEntity<>("Comment saved with favorite!!", HttpStatus.CREATED);

    }

    @Async
    public Boolean existsItemSalve(Long userId, Long commentId){
        User user = this.userService.Get(userId);
        Comment comment = this.commentService.Get(commentId);

        return this.repository.existsByUserAndComment(user, comment);
    }

    public FavoriteComment get(Long favoriteId) {
        FavoriteComment favorite = this.repository.findById(favoriteId).orElse(null);

        if (favorite == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        return favorite;
    }

}
