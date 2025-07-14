package br.com.Blog.api.services;

import br.com.Blog.api.entities.*;
import br.com.Blog.api.entities.enums.LikeOrUnLike;
import br.com.Blog.api.repositories.CommentLikeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CommentLikeService {

    @Autowired
    private CommentLikeRepository repository;

    @Transactional
    public CommentLike reactToComment(User user, Comment comment, LikeOrUnLike action) {
        boolean alreadyReacted = repository.existsByUserAndComment(user, comment);
        if (alreadyReacted) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User already reacted to this comment");
        }

        CommentLike reaction = new CommentLike();
        reaction.setUser(user);
        reaction.setComment(comment);
        reaction.setStatus(action);

        return repository.save(reaction);
    }

    @Transactional
    public CommentLike removeReaction(Long reactionId) {
        if (reactionId == null || reactionId <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Id is required");
        }

        CommentLike reaction = repository.findById(reactionId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND)
        );

        repository.delete(reaction);
        return reaction;
    }

    @Transactional(readOnly = true)
    public boolean exists(Long userId, Long commentId) {
        return repository.existsByUserIdAndCommentId(userId, commentId);
    }

    @Transactional(readOnly = true)
    public ResponseEntity<?> getAllByUser(User user, Pageable pageable) {
        Page<CommentLike> likes = repository.findAllByUser(user, pageable);
        return new ResponseEntity<>(likes, HttpStatus.OK);
    }

}
