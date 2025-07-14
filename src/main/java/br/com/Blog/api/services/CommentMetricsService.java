package br.com.Blog.api.services;

import br.com.Blog.api.entities.Comment;
import br.com.Blog.api.entities.CommentMetrics;
import br.com.Blog.api.entities.PostMetrics;
import br.com.Blog.api.entities.enums.ActionSumOrReduceComment;
import br.com.Blog.api.entities.enums.LikeOrUnLike;
import br.com.Blog.api.repositories.CommentMetricsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class CommentMetricsService {

    @Autowired
    private CommentMetricsRepository metricsRepository;

    @Transactional
    public CommentMetrics sumOrRedLikeOrDislike(CommentMetrics metrics, ActionSumOrReduceComment action, LikeOrUnLike likeOrUnLike) {
        if (action == ActionSumOrReduceComment.SUM && likeOrUnLike == LikeOrUnLike.LIKE ) {
            metrics.setLikes(metrics.getLikes() + 1);
        }

        if (action == ActionSumOrReduceComment.SUM && likeOrUnLike == LikeOrUnLike.UNLIKE ) {
            metrics.setDislikes(metrics.getDislikes() + 1);
        }

        if (action == ActionSumOrReduceComment.REDUCE && likeOrUnLike == LikeOrUnLike.LIKE ) {
            metrics.setLikes(metrics.getLikes() - 1);
        }

        if (action == ActionSumOrReduceComment.REDUCE && likeOrUnLike == LikeOrUnLike.UNLIKE ) {
            metrics.setDislikes(metrics.getDislikes() - 1);
        }

        return this.metricsRepository.save(metrics);
    }

    @Transactional(readOnly = true)
    public CommentMetrics get(Comment comment) {
        if (comment.getId() <= 0 ) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Comment is required");
        }

        Optional<CommentMetrics> metrics = this.metricsRepository.findByComment(comment);

        if (metrics.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Metric not found");
        }

        return metrics.get();
    }

    @Transactional
    public CommentMetrics create(Comment comment) {
        CommentMetrics metrics = new CommentMetrics();
        metrics.setComment(comment);

        return this.metricsRepository.save(metrics);
    }

    @Transactional
    public CommentMetrics sumView(Comment comment) {
        if (comment.getId() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        Optional<CommentMetrics> metricsOptional = this.metricsRepository.findByComment(comment);

        if (metricsOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        CommentMetrics metrics = metricsOptional.get();

        metrics.setLastInteractionAt(LocalDateTime.now());
        metrics.setViewsCount(metrics.getViewsCount() + 1);

        return this.metricsRepository.save(metrics);
    }

    @Transactional
    public void sumEdited(CommentMetrics metrics) {
        metrics.setLastEditedAt(LocalDateTime.now());
        metrics.setEditedTimes(metrics.getEditedTimes() + 1);

        this.metricsRepository.save(metrics);
    }

    @Transactional
    public CommentMetrics sumOrReduceFavorite(CommentMetrics metric, ActionSumOrReduceComment action) {
        if (metric.getId() <= 0L) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        if (action == ActionSumOrReduceComment.SUM) {
            metric.setFavorites(metric.getFavorites() + 1);
        }

        if (action == ActionSumOrReduceComment.REDUCE) {
            metric.setFavorites(metric.getFavorites() - 1);
        }

        metric.setLastInteractionAt(LocalDateTime.now());
        return this.metricsRepository.save(metric);
    }
}
