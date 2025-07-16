package br.com.Blog.api.services;

import br.com.Blog.api.entities.*;
import br.com.Blog.api.entities.enums.LikeOrUnLike;
import br.com.Blog.api.repositories.CommentLikeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
public class CommentLikeService {

    @Autowired
    private CommentLikeRepository repository;

    @Transactional
    public CommentLike reactToComment(User user, Comment comment, LikeOrUnLike action) {
        log.info("reacting to the comment...");

        boolean alreadyReacted = repository.existsByUserAndComment(user, comment);
        if (alreadyReacted) {
            log.info("Error user already reacted the comment");
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User already reacted to this comment");
        }

        CommentLike reaction = new CommentLike();
        reaction.setUser(user);
        reaction.setComment(comment);
        reaction.setStatus(action);

        log.info("React saved");
        return repository.save(reaction);
    }

    @Transactional
    public CommentLike removeReaction(Long reactionId) {
        log.info("removed reaction of comment....");
        if (reactionId <= 0) {
            log.info("Id came null");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Id is required");
        }

        CommentLike reaction = repository.findById(reactionId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND)
        );

        log.info("Deleting reaction...");
        repository.delete(reaction);
        log.info("Reaction deleted!");
        return reaction;
    }

    @Transactional(readOnly = true)
    public boolean exists(Long userId, Long commentId) {
        log.info("Checking reaction exists");
        boolean exists = repository.existsByUserIdAndCommentId(userId, commentId);
        log.info("Returning result");
        return exists;
    }

    @Transactional(readOnly = true)
    public ResponseEntity<?> getAllByUser(User user, Pageable pageable) {
        log.info("looking for reacted comments...");
        Page<CommentLike> likes = repository.findAllByUser(user, pageable);

        log.info("returning reacted comments");
        return new ResponseEntity<>(likes, HttpStatus.OK);
    }

}
