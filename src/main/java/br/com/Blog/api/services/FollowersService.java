package br.com.Blog.api.services;

import br.com.Blog.api.entities.Followers;
import br.com.Blog.api.entities.User;
import br.com.Blog.api.repositories.FollowersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@RequestMapping("/v1/followers")
public class FollowersService {

    private final FollowersRepository repository;
    private final UserService userService;

    @Async
    @Transactional
    public Followers follow(Long userId, Long followedId) {
        User user = userService.get(userId);
        User followed = userService.get(followedId);

        boolean alreadyFollowing = repository.existsByFollowerAndFollowed(user, followed);

        if (alreadyFollowing) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "You are already following this user");
        }

        Followers follower = new Followers();
        follower.setFollower(user);
        follower.setFollowed(followed);

        return repository.save(follower);
    }

    @Async
    @Transactional
    public Followers unfollow(Long userId, Long followedId) {
        User user = userService.get(userId);
        User followed = userService.get(followedId);

        Followers followerRecord = repository.findByFollowerAndFollowed(user, followed);

        if (followerRecord == null) {
            return null;
        }

        repository.delete(followerRecord);
        return followerRecord;
    }

    @Async
    @Transactional(readOnly = true)
    public ResponseEntity<?> getAllFollowed(Long userId, Pageable pageable) {
        User user = userService.get(userId);
        Page<Followers> followed = repository.findAllByFollowed(user, pageable);
        return ResponseEntity.ok(followed);
    }

    @Async
    @Transactional(readOnly = true)
    public Boolean areFollowing(Long userId, Long followedId) {
        User user = userService.get(userId);
        User followed = userService.get(followedId);
        return repository.existsByFollowerAndFollowed(user, followed);
    }

    @Async
    @Transactional(readOnly = true)
    public ResponseEntity<?> getMutualFollowed(Long user1Id, Long user2Id, Pageable pageable) {
        User user1 = userService.get(user1Id);
        User user2 = userService.get(user2Id);

        Page<User> mutuals = repository.findMutualFollowed(user1, user2, pageable);
        return ResponseEntity.ok(mutuals);
    }
}
