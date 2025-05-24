package br.com.Blog.api.services;

import br.com.Blog.api.entities.Followers;
import br.com.Blog.api.entities.User;
import br.com.Blog.api.repositories.FollowersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

@Service
@RequiredArgsConstructor
@RequestMapping("/v1/followers")
public class FollowersService {

    private final FollowersRepository repository;
    private final UserService userService;

    public ResponseEntity<?> follow(Long userId, Long followedId) {
        User user = userService.Get(userId);
        User followed = userService.Get(followedId);

        boolean alreadyFollowing = repository.existsByFollowerAndFollowed(user, followed);

        if (alreadyFollowing) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("You are already following this user");
        }

        Followers follower = new Followers();
        follower.setFollower(user);
        follower.setFollowed(followed);
        repository.save(follower);

        return ResponseEntity.status(HttpStatus.CREATED).body("User followed successfully");
    }

    public ResponseEntity<?> unfollow(Long userId, Long followedId) {
        User user = userService.Get(userId);
        User followed = userService.Get(followedId);

        Followers followerRecord = repository.findByFollowerAndFollowed(user, followed);

        if (followerRecord == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("You are not following this user");
        }

        repository.delete(followerRecord);
        return ResponseEntity.ok("Unfollowed successfully");
    }

    public ResponseEntity<?> getAllFollowed(Long userId, Pageable pageable) {
        User user = userService.Get(userId);
        Page<Followers> followed = repository.findAllByFollowed(user, pageable);
        return ResponseEntity.ok(followed);
    }

    public Boolean areFollowing(Long userId, Long followedId) {
        User user = userService.Get(userId);
        User followed = userService.Get(followedId);
        return repository.existsByFollowerAndFollowed(user, followed);
    }

    public ResponseEntity<?> getMutualFollowed(Long user1Id, Long user2Id, Pageable pageable) {
        User user1 = userService.Get(user1Id);
        User user2 = userService.Get(user2Id);

        Page<User> mutuals = repository.findMutualFollowed(user1, user2, pageable);
        return ResponseEntity.ok(mutuals);
    }
}
