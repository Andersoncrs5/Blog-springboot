package br.com.Blog.api.services;

import br.com.Blog.api.entities.*;
import br.com.Blog.api.entities.enums.LikeOrUnLike;
import br.com.Blog.api.repositories.CommentLikeRepository;
import br.com.Blog.api.repositories.CommentMetricsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class CommentLikeService {

    private final CommentLikeRepository repository;
    private final CommentMetricsRepository metricsRepository;
    private final CommentMetricsService metricsService;

    @Async
    @Transactional
    public CommentLike reactToComment(User user, Comment comment, LikeOrUnLike action) {
        CommentMetrics metrics = metricsService.get(comment);

        boolean alreadyReacted = repository.existsByUserAndComment(user, comment);
        if (alreadyReacted) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User already reacted to this comment");
        }

        CommentLike reaction = new CommentLike();
        reaction.setUser(user);
        reaction.setComment(comment);
        reaction.setStatus(action);

        if (action == LikeOrUnLike.LIKE) {
            metrics.setLikes(metrics.getLikes() + 1);
        } else {
            metrics.setDislikes(metrics.getDislikes() + 1);
        }

        metricsRepository.save(metrics);
        return repository.save(reaction);
    }

    @Async
    @Transactional
    public CommentLike removeReaction(Long reactionId) {
        if (reactionId == null || reactionId <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Id is required");
        }

        CommentLike reaction = repository.findById(reactionId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND)
        );

        CommentMetrics metrics = metricsService.get(reaction.getComment());

        if (reaction.getStatus() == LikeOrUnLike.LIKE) {
            metrics.setLikes(Math.max(0, metrics.getLikes() - 1));
        } else {
            metrics.setDislikes(Math.max(0, metrics.getDislikes() - 1));
        }

        metricsRepository.save(metrics);
        repository.delete(reaction);
        return reaction;
    }

    @Async
    @Transactional(readOnly = true)
    public boolean exists(Long userId, Long commentId) {
        return repository.existsByUserIdAndCommentId(userId, commentId);
    }

    @Async
    @Transactional(readOnly = true)
    public ResponseEntity<?> getAllByUser(User user, Pageable pageable) {
        Page<CommentLike> likes = repository.findAllByUser(user, pageable);
        return new ResponseEntity<>(likes, HttpStatus.OK);
    }

}
