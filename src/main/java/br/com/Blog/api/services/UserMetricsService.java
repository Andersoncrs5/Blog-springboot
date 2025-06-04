package br.com.Blog.api.services;

import br.com.Blog.api.entities.User;
import br.com.Blog.api.entities.UserMetrics;
import br.com.Blog.api.entities.enums.FollowerOrFollowering;
import br.com.Blog.api.entities.enums.LikeOrUnLike;
import br.com.Blog.api.entities.enums.SumOrReduce;
import br.com.Blog.api.repositories.UserMetricsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
public class UserMetricsService {

    @Autowired
    private UserMetricsRepository repository;

    @Async
    @Transactional(readOnly = true)
    public UserMetrics get(User user) {
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User must not be null");
        }

        return this.repository.findByUser(user)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User metrics not found"));
    }

    @Async
    @Transactional
    public void create(User user) {
        UserMetrics metric = new UserMetrics();
        metric.setUser(user);
        metric.setId(null);

        this.repository.save(metric);
    }

    @Async
    @Transactional
    public void setLastLogin(User user) {
        UserMetrics metric = this.get(user);
        metric.setLastLogin(LocalDateTime.now());
        this.repository.save(metric);
    }

    @Async
    @Transactional
    public void incrementMetric(UserMetrics metrics, FollowerOrFollowering action) {
        switch (action) {
            case FOLLOWERING -> metrics.setFollowingCount(metrics.getFollowingCount() + 1);
            case FOLLOWER -> metrics.setFollowersCount(metrics.getFollowersCount() + 1);
        }

        repository.save(metrics);
    }

    @Async
    @Transactional
    public void decrementMetric(UserMetrics metrics, FollowerOrFollowering action) {

        switch (action) {
            case FOLLOWERING -> metrics.setFollowingCount(metrics.getFollowingCount() - 1);
            case FOLLOWER -> metrics.setFollowersCount(metrics.getFollowersCount() - 1);
        }

        repository.save(metrics);
    }

    @Async
    @Transactional
    public void sumOrRedPostsCount(UserMetrics metrics, SumOrReduce action) {
        if (action == SumOrReduce.SUM) {
            metrics.setPostsCount(metrics.getPostsCount() + 1);
        }

        if (action == SumOrReduce.REDUCE) {
            metrics.setPostsCount(metrics.getPostsCount() - 1);
        }

        this.repository.save(metrics);
    }

    @Async
    @Transactional
    public void sumOrRedCommentsCount(UserMetrics metrics, SumOrReduce action) {
        if (action == SumOrReduce.SUM) {
            metrics.setCommentsCount(metrics.getCommentsCount() + 1);
        }

        if (action == SumOrReduce.REDUCE) {
            metrics.setCommentsCount(metrics.getCommentsCount() - 1);
        }

        this.repository.save(metrics);
    }

    @Async
    @Transactional
    public void sumOrRedLikesOrDislikeGivenCount(UserMetrics metrics, SumOrReduce action, LikeOrUnLike likeOrUnLike) {
        if (action == SumOrReduce.SUM && likeOrUnLike == LikeOrUnLike.LIKE ) {
            metrics.setLikesGivenCount(metrics.getLikesGivenCount() + 1);
            metrics.setLikesGivenCountCreateByDay(metrics.getLikesGivenCountCreateByDay() + 1);
        }

        if (action == SumOrReduce.REDUCE && likeOrUnLike == LikeOrUnLike.LIKE ) {
            metrics.setLikesGivenCount(metrics.getLikesGivenCount() - 1);
            metrics.setLikesGivenCountCreateByDay(metrics.getLikesGivenCountCreateByDay() - 1);
        }

        if (action == SumOrReduce.SUM && likeOrUnLike == LikeOrUnLike.UNLIKE ) {
            metrics.setDeslikesGivenCount(metrics.getLikesGivenCount() + 1);
            metrics.setDeslikesGivenCountCreateByDay(metrics.getLikesGivenCountCreateByDay() + 1);
        }

        if (action == SumOrReduce.REDUCE && likeOrUnLike == LikeOrUnLike.UNLIKE ) {
            metrics.setDeslikesGivenCount(metrics.getLikesGivenCount() - 1);
            metrics.setDeslikesGivenCountCreateByDay(metrics.getLikesGivenCountCreateByDay() - 1);
        }

        this.repository.save(metrics);
    }

    @Async
    @Transactional
    public void sumOrRedSavedPostsCountFavorite(UserMetrics metrics, SumOrReduce action) {
        if (action == SumOrReduce.SUM) {
            metrics.setSavedPostsCount(metrics.getSavedPostsCount() + 1);
        }

        if (action == SumOrReduce.REDUCE) {
            metrics.setSavedPostsCount(metrics.getSavedPostsCount() - 1);
        }

        this.repository.save(metrics);
    }

    @Async
    @Transactional
    public void sumOrRedSavedCommentsCount(UserMetrics metrics, SumOrReduce action) {
        if (action == SumOrReduce.SUM) {
            metrics.setSavedCommentsCount(metrics.getSavedCommentsCount() + 1);

        }

        if (action == SumOrReduce.REDUCE) {
            metrics.setSavedCommentsCount(metrics.getSavedCommentsCount() - 1);

        }

        this.repository.save(metrics);
    }
}