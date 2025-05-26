package br.com.Blog.api.services;

import br.com.Blog.api.entities.FavoritePost;
import br.com.Blog.api.entities.Post;
import br.com.Blog.api.entities.User;
import br.com.Blog.api.repositories.FavoritePostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class FavoritePostService {

    private final FavoritePostRepository repository;
    private final UserService userService;
    private final PostService postService;
    private final PostMetricsService postMetricsService;
    private final UserMetricsService userMetricsService;

    @Async
    public ResponseEntity<?> GetAllFavoritePostOfUser(Long userId, Pageable pageable){
        User user = this.userService.get(userId);
        return new ResponseEntity<>(this.repository.findAllByUser(user, pageable), HttpStatus.OK);
    }

    @Async
    @Transactional
    public FavoritePost Delete(Long idItem){
        FavoritePost favoritePost = this.get(idItem);

        this.repository.delete(favoritePost);

        return favoritePost;
    }

    @Async
    @Transactional
    public FavoritePost create(Long postId, Long userId){
        User user = this.userService.get(userId);
        Post post = this.postService.Get(postId);

        boolean check = this.repository.existsByUserAndPost(user, post);

        if (check)
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Item already exists");

        FavoritePost fpToCreate = new FavoritePost();

        fpToCreate.setPost(post);
        fpToCreate.setUser(user);

        return this.repository.save(fpToCreate);
    }

    @Async
    public ResponseEntity<?> existsItemSalve(Long idUser, Long idPost){
        boolean check = this.repository.existsByUserIdAndPostId(idUser, idPost);

        if (!check)
            return new ResponseEntity<>(false, HttpStatus.NOT_FOUND );

        return new ResponseEntity<>(true, HttpStatus.FOUND );
    }

    private FavoritePost get(Long id) {
        if (id == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Id is required");

        FavoritePost favoritePost = this.repository.findById(id).orElse(null);

        if (favoritePost == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Favorite post not found");

        return favoritePost;
    }

}