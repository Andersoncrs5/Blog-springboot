package br.com.Blog.api.services;

import br.com.Blog.api.entities.Comment;
import br.com.Blog.api.entities.CommentLike;
import br.com.Blog.api.entities.User;
import br.com.Blog.api.repositories.CommentLikeRepository;
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
public class CommentLikeService {

    private final CommentLikeRepository repository;
    private final UserService userService;
    private final CommentService commentService;

    @Async
    @Transactional
    public ResponseEntity<?> save(Long userId, Long commentId) {
        User user = this.userService.Get(userId);
        Comment comment = this.commentService.Get(commentId);

        boolean check = this.exists(userId, commentId);

        if (check) {
            return new ResponseEntity<>("Comment already has like", HttpStatus.CONFLICT);
        }

        CommentLike like = new CommentLike();
        like.setComment(comment);
        like.setUser(user);
        this.repository.save(like);

        return new ResponseEntity<>("Like added successfully", HttpStatus.OK);
    }

    @Async
    @Transactional
    public ResponseEntity<?> remove(Long id) {
        if (id == null || id <= 0) {
            return new ResponseEntity<>("Id is required", HttpStatus.BAD_REQUEST);
        }

        CommentLike like = this.repository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND)
        );

        this.repository.delete(like);

        return new ResponseEntity<>("Like removed", HttpStatus.OK);
    }

    @Async
    @Transactional
    public boolean exists(Long userId, Long commentId) {
        User user = this.userService.Get(userId);
        Comment comment = this.commentService.Get(commentId);

        return this.repository.existsByUserAndComment(user, comment);
    }

    @Async
    @Transactional
    public ResponseEntity<?> getAllByUser(Long userId, Pageable pageable) {
        User user = this.userService.Get(userId);

        Page<CommentLike> likes = this.repository.findAllByUser(user, pageable);

        return new ResponseEntity<>(likes, HttpStatus.OK);
    }

    @Async
    @Transactional
    public ResponseEntity<?> countLikeByComment(Long commentId) {
        Comment comment = this.commentService.Get(commentId);

        Integer count = this.repository.countAllByComment(comment);

        return new ResponseEntity<>(count, HttpStatus.OK);
    }
}
