package br.com.Blog.api.services;

import br.com.Blog.api.entities.User;
import br.com.Blog.api.entities.UserMetrics;
import br.com.Blog.api.entities.enums.FollowerOrFollowering;
import br.com.Blog.api.entities.enums.SumOrReduce;
import br.com.Blog.api.repositories.UserMetricsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class UserMetricsService {

    @Autowired
    private UserMetricsRepository repository;

    @Async
    @Transactional
    public UserMetrics get(User user) {
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        Optional<UserMetrics> metric = this.repository.findByUser(user);

        if (metric.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        return metric.get();
    }

    @Async
    @Transactional
    public void FollowerOrFollowering(User user, FollowerOrFollowering action) {
        UserMetrics metrics = this.get(user);

        if (action == FollowerOrFollowering.FOLLOWERING )
            metrics.setFollowingCount(metrics.getFollowingCount() + 1);

        if (action == FollowerOrFollowering.FOLLOWER )
            metrics.setFollowersCount(metrics.getFollowersCount() + 1);

        this.repository.save(metrics);
    }

    @Async
    @Transactional
    public void removeFollowerOrFollowering(User user, FollowerOrFollowering action) {
        UserMetrics metrics = this.get(user);

        if (action == FollowerOrFollowering.FOLLOWERING )
            metrics.setFollowingCount(metrics.getFollowingCount() - 1);

        if (action == FollowerOrFollowering.FOLLOWER )
            metrics.setFollowersCount(metrics.getFollowersCount() - 1);

        this.repository.save(metrics);
    }

    @Async
    @Transactional
    public void sumOrRedPostsCount(User user, SumOrReduce action) {
        UserMetrics metrics = this.get(user);

        if (action == SumOrReduce.SUM)
            metrics.setPostsCount(metrics.getPostsCount() + 1);

        if (action == SumOrReduce.REDUCE)
            metrics.setPostsCount(metrics.getPostsCount() - 1);

        this.repository.save(metrics);

    }

    @Async
    @Transactional
    public void sumOrRedCommentsCount(User user, SumOrReduce action) {
        UserMetrics metrics = this.get(user);

        if (action == SumOrReduce.SUM)
            metrics.setCommentsCount(metrics.getCommentsCount() + 1);

        if (action == SumOrReduce.REDUCE)
            metrics.setCommentsCount(metrics.getCommentsCount() - 1);

        this.repository.save(metrics);
    }

    @Async
    @Transactional
    public void sumOrRedLikesGivenCount(User user, SumOrReduce action) {
        UserMetrics metrics = this.get(user);

        if (action == SumOrReduce.SUM)
            metrics.setLikesGivenCount(metrics.getLikesGivenCount() + 1);

        if (action == SumOrReduce.REDUCE)
            metrics.setLikesGivenCount(metrics.getLikesGivenCount() - 1);

        this.repository.save(metrics);
    }

    @Async
    @Transactional
    public void sumOrRedDisikesGivenCount(User user, SumOrReduce action) {
        UserMetrics metrics = this.get(user);

        if (action == SumOrReduce.SUM)
            metrics.setDeslikesGivenCount(metrics.getDeslikesGivenCount() + 1);

        if (action == SumOrReduce.REDUCE)
            metrics.setDeslikesGivenCount(metrics.getDeslikesGivenCount() - 1);

        this.repository.save(metrics);
    }

    @Async
    @Transactional
    public void sumOrRedSavedPostsCount(User user, SumOrReduce action) {
        UserMetrics metrics = this.get(user);

        if (action == SumOrReduce.SUM)
            metrics.setSavedPostsCount(metrics.getSavedPostsCount() + 1);

        if (action == SumOrReduce.REDUCE)
            metrics.setSavedPostsCount(metrics.getSavedPostsCount() - 1);


        this.repository.save(metrics);
    }

    @Async
    @Transactional
    public void sumOrRedSavedCommentsCount(User user, SumOrReduce action) {
        UserMetrics metrics = this.get(user);

        if (action == SumOrReduce.SUM)
            metrics.setSavedCommentsCount(metrics.getSavedCommentsCount() + 1);

        if (action == SumOrReduce.REDUCE)
            metrics.setSavedCommentsCount(metrics.getSavedCommentsCount() - 1);

        this.repository.save(metrics);
    }



}
