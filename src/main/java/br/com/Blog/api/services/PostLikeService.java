package br.com.Blog.api.services;

import br.com.Blog.api.entities.Post;
import br.com.Blog.api.entities.PostLike;
import br.com.Blog.api.entities.User;
import br.com.Blog.api.repositories.PostLikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class PostLikeService {

    private final PostLikeRepository repository;
    private final UserService userService;
    private final PostService postService;

    @Async
    @Transactional
    public ResponseEntity<?> save(Long userId, Long postId) {
        User user = this.userService.Get(userId);
        Post post = this.postService.Get(postId);

        boolean check = this.exists(userId, postId);

        if (check) {
            return new ResponseEntity<>("Post already have like", HttpStatus.CONFLICT);
        }

        PostLike like = new PostLike();
        like.setPost(post);
        like.setUser(user);
        this.repository.save(like);

        return new ResponseEntity<>("Like with success", HttpStatus.OK);
    }

    @Async
    @Transactional
    public ResponseEntity<?> remove(Long id) {
        if (id == null || id <= 0 ) {
            return new ResponseEntity<>("Id is required", HttpStatus.BAD_REQUEST);
        }

        PostLike like = this.repository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND)
        );

        this.repository.delete(like);

        return new ResponseEntity<>("Like removed", HttpStatus.OK);
    }

    @Async
    @Transactional
    public boolean exists(Long userId, Long postId) {
        User user = this.userService.Get(userId);
        Post post = this.postService.Get(postId);

        return this.repository.existsByUserAndPost(user, post);
    }

    @Async
    @Transactional
    public ResponseEntity<?> getAllByUser(Long userId, Pageable pageable) {
        User user = this.userService.Get(userId);

        Page<PostLike> likes = this.repository.findAllByUser(user, pageable);

        return new ResponseEntity<>(likes, HttpStatus.OK);
    }

    @Async
    @Transactional
    public ResponseEntity<?> countLikeByPost(Long postId) {
        Post post = this.postService.Get(postId);

        Integer count = this.repository.countAllByPost(post);

        return new ResponseEntity<>(count, HttpStatus.OK);
    }

}
