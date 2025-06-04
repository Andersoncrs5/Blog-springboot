package br.com.Blog.api.services;

import br.com.Blog.api.entities.Post;
import br.com.Blog.api.entities.PostLike;
import br.com.Blog.api.entities.PostMetrics;
import br.com.Blog.api.entities.User;
import br.com.Blog.api.entities.enums.LikeOrUnLike;
import br.com.Blog.api.entities.enums.SumOrReduce;
import br.com.Blog.api.repositories.PostLikeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PostLikeService {

    @Autowired
    private PostLikeRepository repository;

    @Async
    @Transactional
    public PostLike reactToPost(User user, Post post, LikeOrUnLike action) {
        boolean alreadyReacted = this.repository.existsByUserAndPost(user, post);

        if (alreadyReacted) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User already reacted to this post");
        }

        PostLike like = new PostLike();

        like.setUser(user);
        like.setPost(post);
        like.setStatus(action);

        return this.repository.save(like);
    }

    @Async
    @Transactional
    public PostLike removeReaction(Long id) {
        if (id == null || id <= 0 ) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Id is required");
        }

        PostLike like = this.repository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND)
        );

        this.repository.delete(like);
        return like;
    }

    @Async
    @Transactional(readOnly = true)
    public boolean exists(User user, Post post) {
        return this.repository.existsByUserAndPost(user, post);
    }

    @Async
    @Transactional
    public Page<PostLike> getAllByUser(User user, Pageable pageable) {
        return this.repository.findAllByUser(user, pageable);
    }

}