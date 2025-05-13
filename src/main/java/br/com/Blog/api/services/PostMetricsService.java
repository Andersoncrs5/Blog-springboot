package br.com.Blog.api.services;

import br.com.Blog.api.entities.Post;
import br.com.Blog.api.entities.PostMetrics;
import br.com.Blog.api.entities.enums.ActionSumOrReduceComment;
import br.com.Blog.api.repositories.PostMetricsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;


@Service
public class PostMetricsService {

    @Autowired
    private PostMetricsRepository repository;

    @Async
    @Transactional
    public PostMetrics get(Post post) {
        if (post == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        PostMetrics metric = this.repository.findByPost(post);

        if (metric == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        return metric;
    }

    @Async
    @Transactional
    public void sumOrReduceComments(Post post, ActionSumOrReduceComment action) {
        PostMetrics metric = this.get(post);

        if (action == ActionSumOrReduceComment.SUM) {
            metric.setComments(metric.getComments() + 1);
        } else {
            metric.setComments(metric.getComments() - 1);
        }

        metric.setLastInteractionAt(LocalDateTime.now());

        this.repository.save(metric);
    }

    @Async
    @Transactional
    public void sumOrReduceFavorite(Post post, ActionSumOrReduceComment action) {
        PostMetrics metric = this.get(post);

        if (action == ActionSumOrReduceComment.SUM) {
            metric.setFavorites(metric.getFavorites() + 1);
        } else {
            metric.setFavorites(metric.getFavorites() - 1);
        }

        metric.setLastInteractionAt(LocalDateTime.now());
        this.repository.save(metric);
    }

    @Async
    @Transactional
    public void clicks(Post post){
        PostMetrics metric = this.get(post);

        metric.setClicks(metric.getClicks() + 1);

        metric.setLastInteractionAt(LocalDateTime.now());
        this.repository.save(metric);
    }

    @Async
    @Transactional
    public void viewed(Post post){
        PostMetrics metric = this.get(post);

        metric.setViewed(metric.getViewed() + 1);

        metric.setLastInteractionAt(LocalDateTime.now());
        this.repository.save(metric);
    }

    @Async
    @Transactional
    public void editedTimes(Post post){
        PostMetrics metric = this.get(post);
        metric.setEditedTimes(metric.getEditedTimes() + 1);

        this.repository.save(metric);
    }


}
