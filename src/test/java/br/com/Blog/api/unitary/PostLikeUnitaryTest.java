package br.com.Blog.api.unitary;

import br.com.Blog.api.entities.Category;
import br.com.Blog.api.entities.Post;
import br.com.Blog.api.entities.PostLike;
import br.com.Blog.api.entities.User;
import br.com.Blog.api.entities.enums.LikeOrUnLike;
import br.com.Blog.api.repositories.PostLikeRepository;
import br.com.Blog.api.services.PostLikeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PostLikeUnitaryTest {

    @Mock
    private PostLikeRepository repository;

    @InjectMocks
    private PostLikeService service;

    @BeforeEach()
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAllByUser() {
        User user = new User();
        user.setId(1L);
        user.setName("Usuário de Teste");

        Post post1 = new Post();
        post1.setId(100L);

        Post post2 = new Post();
        post2.setId(101L);

        PostLike like1 = new PostLike();
        like1.setId(1L);
        like1.setUser(user);
        like1.setPost(post1);
        like1.setStatus(LikeOrUnLike.LIKE);

        PostLike like2 = new PostLike();
        like2.setId(2L);
        like2.setUser(user);
        like2.setPost(post2);
        like2.setStatus(LikeOrUnLike.UNLIKE);

        List<PostLike> likes = List.of(like1, like2);
        Pageable pageable = PageRequest.of(0, 10);
        Page<PostLike> page = new PageImpl<>(likes, pageable, likes.size());

        when(repository.findAllByUser(user, pageable)).thenReturn(page);

        Page<PostLike> result = service.getAllByUser(user, pageable);

        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals(like1.getId(), result.getContent().get(0).getId());
        assertEquals(like2.getId(), result.getContent().get(1).getId());

        verify(repository, times(1)).findAllByUser(user, pageable);
    }

    @Test
    public void testReactToPostLike() {
        Map<String, Object> userAndPost = this.setUserAndPost();
        User user = (User) userAndPost.get("user");
        Post post = (Post) userAndPost.get("post");

        assertNotNull(user.getId());
        assertNotNull(post.getId());

        PostLike likeSave = new PostLike();
        likeSave.setUser(user);
        likeSave.setPost(post);
        likeSave.setStatus(LikeOrUnLike.LIKE);
        likeSave.setId(1L);

        when(repository.existsByUserAndPost(any(User.class), any(Post.class))).thenReturn(false);
        when(repository.save(any(PostLike.class))).thenReturn(likeSave);

        PostLike result = this.service.reactToPost(user, post, LikeOrUnLike.LIKE);

        assertNotNull(result);
        assertEquals(user, result.getUser());
        assertEquals(post, result.getPost());
        assertEquals(LikeOrUnLike.LIKE, result.getStatus());
        assertNotNull(result.getId());

        verify(repository, times(1)).existsByUserAndPost(any(User.class), any(Post.class));
        verify(repository, times(1)).save(any(PostLike.class));

        InOrder inOrder = inOrder(repository);
        inOrder.verify(repository).existsByUserAndPost(any(User.class), any(Post.class));
        inOrder.verify(repository).save(any(PostLike.class));
    }

    @Test
    public void testReactToPostDislike() {
        Map<String, Object> userAndPost = this.setUserAndPost();
        User user = (User) userAndPost.get("user");
        Post post = (Post) userAndPost.get("post");

        assertNotNull(user.getId());
        assertNotNull(post.getId());

        PostLike likeSave = new PostLike();
        likeSave.setUser(user);
        likeSave.setPost(post);
        likeSave.setStatus(LikeOrUnLike.UNLIKE);
        likeSave.setId(1L);

        when(repository.existsByUserAndPost(any(User.class), any(Post.class))).thenReturn(false);
        when(repository.save(any(PostLike.class))).thenReturn(likeSave);

        PostLike result = this.service.reactToPost(user, post, LikeOrUnLike.UNLIKE);

        assertNotNull(result);
        assertEquals(user, result.getUser());
        assertEquals(post, result.getPost());
        assertEquals(LikeOrUnLike.UNLIKE, result.getStatus());
        assertNotNull(result.getId());

        verify(repository, times(1)).existsByUserAndPost(any(User.class), any(Post.class));
        verify(repository, times(1)).save(any(PostLike.class));

        InOrder inOrder = inOrder(repository);
        inOrder.verify(repository).existsByUserAndPost(any(User.class), any(Post.class));
        inOrder.verify(repository).save(any(PostLike.class));
    }

    @Test
    public void testThrowBadRequestInRemoverAction() {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, ()-> {
            this.service.removeReaction(0L);
        });

        ResponseStatusException exception1 = assertThrows(ResponseStatusException.class, ()-> {
            this.service.removeReaction(-1L);
        });

        assertNotNull(exception);
        assertNotNull(exception1);

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals(HttpStatus.BAD_REQUEST, exception1.getStatusCode());

        verify(repository, never()).findById(anyLong());
        verify(repository, never()).delete(any(PostLike.class));
    }

    @Test
    public void testRemoverAction() {
        Map<String, Object> userAndPost = this.setUserAndPost();
        User user = (User) userAndPost.get("user");
        Post post = (Post) userAndPost.get("post");

        assertNotNull(user.getId());
        assertNotNull(post.getId());

        PostLike likeSave = new PostLike();
        likeSave.setUser(user);
        likeSave.setPost(post);
        likeSave.setStatus(LikeOrUnLike.UNLIKE);
        likeSave.setId(1L);

        when(repository.findById(likeSave.getId())).thenReturn(Optional.of(likeSave));
        doNothing().when(repository).delete(likeSave);

        PostLike result = this.service.removeReaction(likeSave.getId());

        assertNotNull(result);
        assertEquals(user, result.getUser());
        assertEquals(post, result.getPost());
        assertEquals(LikeOrUnLike.UNLIKE, result.getStatus());
        assertNotNull(result.getId());

        verify(repository, times(1)).findById(likeSave.getId());
        verify(repository, times(1)).delete(likeSave);

        InOrder inOrder = inOrder(repository);
        inOrder.verify(repository).findById(likeSave.getId());
        inOrder.verify(repository).delete(likeSave);
    }

    @Test
    public void testExistsTrue() {
        Map<String, Object> userAndPost = this.setUserAndPost();
        User user = (User) userAndPost.get("user");
        Post post = (Post) userAndPost.get("post");

        assertNotNull(user.getId());
        assertNotNull(post.getId());

        when(repository.existsByUserAndPost(user, post)).thenReturn(true);

        boolean result = this.service.exists(user, post);

        assertTrue(result);

        verify(repository, times(1)).existsByUserAndPost(user, post);
    }

    @Test
    public void testExistsFalse() {
        Map<String, Object> userAndPost = this.setUserAndPost();
        User user = (User) userAndPost.get("user");
        Post post = (Post) userAndPost.get("post");

        assertNotNull(user.getId());
        assertNotNull(post.getId());

        when(repository.existsByUserAndPost(user, post)).thenReturn(false);

        boolean result = this.service.exists(user, post);

        assertFalse(result);

        verify(repository, times(1)).existsByUserAndPost(user, post);
    }

    private Map<String, Object> setUserAndPost() {
        User user = new User();

        user.setId(1L);
        user.setName("test");
        user.setEmail("test@gmail.com");
        user.setPassword("12345678");

        Category category = new Category();

        category.setId(1L);
        category.setName("TI");
        category.setUser(user);

        Post postSaved = new Post();
        postSaved.setId(1L);
        postSaved.setTitle("post 1");
        postSaved.setContent("post 1");
        postSaved.setReadingTime(9);
        postSaved.setSlug("123456");
        postSaved.setUser(user);
        postSaved.setCategory(category);

        Map<String, Object> map = new HashMap<>();

        map.put("user", user);
        map.put("post", postSaved);

        return map;
    }

}
