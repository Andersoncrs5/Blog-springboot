package br.com.Blog.api.services;

import br.com.Blog.api.entities.Followers;
import br.com.Blog.api.entities.User;
import br.com.Blog.api.repositories.FollowersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@RequestMapping("/v1/followers")
public class FollowersService {

    @Autowired
    private FollowersRepository repository;

    @Transactional
    public Followers follow(User user, User followed) {
        boolean alreadyFollowing = repository.existsByFollowerAndFollowed(user, followed);

        if (alreadyFollowing) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "You are already following this user");
        }

        Followers follower = new Followers();
        follower.setFollower(user);
        follower.setFollowed(followed);
        follower.setId(null);

        return repository.save(follower);
    }

    @Transactional
    public Followers unfollow(User user, User followed) {
        Followers followerRecord = repository.findByFollowerAndFollowed(user, followed);

        if (followerRecord == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"You are not following this user");
        }

        repository.delete(followerRecord);
        return followerRecord;
    }

    @Transactional(readOnly = true)
    public Page<Followers> getAllFollowed(User user, Pageable pageable) {
        return repository.findAllByFollower(user, pageable);
    }

    @Transactional(readOnly = true)
    public ResponseEntity<?> getMutualFollowed(User user1, User user2, Pageable pageable) {
        Page<User> mutuals = repository.findMutualFollowed(user1, user2, pageable);
        return ResponseEntity.ok(mutuals);
    }

    @Transactional(readOnly = true)
    public Boolean areFollowing(User user, User followed) {
        return repository.existsByFollowerAndFollowed(user, followed);
    }
}
