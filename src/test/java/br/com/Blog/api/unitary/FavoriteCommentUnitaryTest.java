package br.com.Blog.api.unitary;

import br.com.Blog.api.entities.*;
import br.com.Blog.api.repositories.FavoriteCommentRepository;
import br.com.Blog.api.services.FavoriteCommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FavoriteCommentUnitaryTest {

    @Mock
    private FavoriteCommentRepository repository;

    @InjectMocks
    private FavoriteCommentService service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testDeleteFavoriteComment() {
        User user = new User();

        user.setId(1L);
        user.setName("user");
        user.setEmail("user@gmail.com");
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

        Comment commentExists = new Comment();
        commentExists.setId(1L);
        commentExists.setContent("content");
        commentExists.setName("user");
        commentExists.setUser(user);
        commentExists.setPost(postSaved);

        FavoriteComment favoriteComment = new FavoriteComment();
        favoriteComment.setId(1L);
        favoriteComment.setUser(user);
        favoriteComment.setComment(commentExists);

        doNothing().when(repository).delete(favoriteComment);

        var result = this.service.Delete(favoriteComment);

        assertNotNull(result);
        assertEquals(user, result.getUser());
        assertEquals(commentExists, result.getComment());
        assertEquals(favoriteComment.getId(), result.getId());

        verify(repository, times(1)).delete(favoriteComment);
    }

    @Test
    public void testGetFavoriteComment() {
        User user = new User();

        user.setId(1L);
        user.setName("user");
        user.setEmail("user@gmail.com");
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

        Comment commentExists = new Comment();
        commentExists.setId(1L);
        commentExists.setContent("content");
        commentExists.setName("user");
        commentExists.setUser(user);
        commentExists.setPost(postSaved);

        FavoriteComment favoriteComment = new FavoriteComment();
        favoriteComment.setId(1L);
        favoriteComment.setUser(user);
        favoriteComment.setComment(commentExists);

        when(repository.findById(1L)).thenReturn(Optional.of(favoriteComment));

        FavoriteComment result = this.service.get(1L);

        assertNotNull(result);
        assertEquals(user, result.getUser());
        assertEquals(commentExists, result.getComment());
        assertEquals(favoriteComment.getId(), result.getId());

        verify(repository, times(1)).findById(1L);
    }

    @Test
    public void testCreateFavoriteComment() {
        User user = new User();

        user.setId(1L);
        user.setName("user");
        user.setEmail("user@gmail.com");
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

        Comment commentExists = new Comment();
        commentExists.setId(1L);
        commentExists.setContent("content");
        commentExists.setName("user");
        commentExists.setUser(user);
        commentExists.setPost(postSaved);

        FavoriteComment favoriteComment = new FavoriteComment();
        favoriteComment.setUser(user);
        favoriteComment.setComment(commentExists);

        FavoriteComment savedFavoriteComment = new FavoriteComment();
        savedFavoriteComment.setId(1L);
        savedFavoriteComment.setUser(user);
        savedFavoriteComment.setComment(commentExists);

        when(repository.save(any(FavoriteComment.class))).thenReturn(savedFavoriteComment);

        FavoriteComment result = this.service.create(commentExists, user);

        assertNotNull(result, "Result is null");
        assertEquals(user, result.getUser(), "Result user and user are not equals");
        assertEquals(commentExists, result.getComment(), "Result comment and comment are not equals");
        assertNotNull(result.getId(), "ID is null");

        verify(repository, times(1)).save(any(FavoriteComment.class));
    }

    @Test
    public void testExistsFavoriteCommentTrue() {
        User user = new User();

        user.setId(1L);
        user.setName("user");
        user.setEmail("user@gmail.com");
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

        Comment commentExists = new Comment();
        commentExists.setId(1L);
        commentExists.setContent("content");
        commentExists.setName("user");
        commentExists.setUser(user);
        commentExists.setPost(postSaved);

        FavoriteComment favoriteComment = new FavoriteComment();
        favoriteComment.setId(1L);
        favoriteComment.setUser(user);
        favoriteComment.setComment(commentExists);

        when(repository.existsByUserAndComment(any(User.class), any(Comment.class))).thenReturn(true);

        boolean result = this.service.existsItemSalve(user, commentExists);

        assertTrue(result);

        verify(repository, times(1)).existsByUserAndComment(user, commentExists);
    }

    @Test
    public void testExistsFavoriteCommentFalse() {
        User user = new User();

        user.setId(1L);
        user.setName("user");
        user.setEmail("user@gmail.com");
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

        Comment commentExists = new Comment();
        commentExists.setId(1L);
        commentExists.setContent("content");
        commentExists.setName("user");
        commentExists.setUser(user);
        commentExists.setPost(postSaved);

        when(repository.existsByUserAndComment(any(User.class), any(Comment.class))).thenReturn(false);

        boolean result = this.service.existsItemSalve(user, commentExists);

        assertFalse(result);

        verify(repository, times(1)).existsByUserAndComment(user, commentExists);
    }

    @Test
    public void testThrowBadRequestInGet() {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, ()->{
            this.service.get(-1L);
        });

        ResponseStatusException exception1 = assertThrows(ResponseStatusException.class, ()->{
            this.service.get(0L);
        });

        assertNotNull(exception);
        assertNotNull(exception1);

        assertEquals(HttpStatus.BAD_REQUEST,exception.getStatusCode());
        assertEquals(HttpStatus.BAD_REQUEST,exception1.getStatusCode());

        verify(repository, never()).findById(anyLong());
    }

    @Test
    public void testThrowNotFoundInExist() {
        User user = new User();

        user.setId(1L);
        user.setName("user");
        user.setEmail("user@gmail.com");
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

        Comment commentExists = new Comment();
        commentExists.setId(1L);
        commentExists.setContent("content");
        commentExists.setName("user");
        commentExists.setUser(user);
        commentExists.setPost(postSaved);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            this.service.get(999L);
        });

        assertNotNull(exception);
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    public void testGetAllFavoriteOfUser() {
        User user = new User();
        user.setId(1L);
        user.setName("user");
        user.setEmail("user@gmail.com");
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

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setContent("content");
        comment.setName("user");
        comment.setUser(user);
        comment.setPost(postSaved);

        FavoriteComment favorite = new FavoriteComment();
        favorite.setId(1L);
        favorite.setComment(comment);
        favorite.setUser(user);

        List<FavoriteComment> list = Collections.singletonList(favorite);
        Page<FavoriteComment> page = new PageImpl<>(list);

        Pageable pageable = PageRequest.of(0, 10);

        when(repository.findAllByUser(eq(user), eq(pageable))).thenReturn(page);

        Page<FavoriteComment> result = service.GetAllFavoriteOfUser(user, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(favorite, result.getContent().get(0));

        verify(repository, times(1)).findAllByUser(eq(user), eq(pageable));
    }

}
