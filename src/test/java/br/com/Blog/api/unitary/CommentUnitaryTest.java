package br.com.Blog.api.unitary;

import br.com.Blog.api.entities.Category;
import br.com.Blog.api.entities.Comment;
import br.com.Blog.api.entities.Post;
import br.com.Blog.api.entities.User;
import br.com.Blog.api.repositories.CommentRepository;
import br.com.Blog.api.services.CommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CommentUnitaryTest {

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private CommentService commentService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetComment() {
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

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setContent("content");
        comment.setName("user");
        comment.setUser(user);
        comment.setPost(postSaved);

        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));

        Comment commentFound = this.commentService.Get(1L);

        assertNotNull(commentFound);
        assertEquals(commentFound.getId(), comment.getId());
        assertEquals(commentFound.getContent(), comment.getContent());
        assertEquals(postSaved, commentFound.getPost());
        assertEquals(user, commentFound.getUser());

        verify(commentRepository, times(1)).findById(1L);
    }

    @Test
    public void testThrowBadRequest() {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            this.commentService.Get(-1L);
        });

        ResponseStatusException exception1 = assertThrows(ResponseStatusException.class, () -> {
            this.commentService.Get(0L);
        });

        assertNotNull(exception);
        assertNotNull(exception1);

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals(HttpStatus.BAD_REQUEST, exception1.getStatusCode());
    }

    @Test
    public void testThrowNotFound() {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            this.commentService.Get(9L);
        });

        assertNotNull(exception);
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());

        verify(commentRepository, times(1)).findById(9L);
    }

    @Test
    public void testCreateComment() {
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

        Comment commentToSaved = new Comment();
        commentToSaved.setContent("content update");
        commentToSaved.setName("user");

        Comment commentSaved = new Comment();
        commentSaved.setId(1L);
        commentSaved.setContent("content update");
        commentSaved.setName("user");
        commentSaved.setUser(user);
        commentSaved.setPost(postSaved);

        when(commentRepository.save(any(Comment.class))).thenReturn(commentSaved);

        Comment commentSaved1 = this.commentService.Create(commentToSaved, user, postSaved);

        assertNotNull(commentSaved1);
        assertEquals(commentSaved.getId(), commentSaved1.getId());
        assertEquals(commentSaved.getContent(), commentSaved1.getContent());
        assertEquals(postSaved, commentSaved1.getPost());
        assertEquals(user, commentSaved1.getUser());

        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    public void testDeleteComment() {
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

        doNothing().when(commentRepository).delete(commentExists);

        Comment commentDeleted = this.commentService.Delete(commentExists);

        assertNotNull(commentDeleted);
        assertEquals(commentExists.getId(), commentDeleted.getId());
        assertEquals(commentExists.getContent(), commentDeleted.getContent());
        assertEquals(postSaved, commentDeleted.getPost());
        assertEquals(user, commentDeleted.getUser());

        verify(commentRepository, times(1)).delete(commentExists);
    }

    @Test
    public void testUpdateComment() {
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

        Comment commentExist = new Comment();
        commentExist.setId(1L);
        commentExist.setContent("content");
        commentExist.setName("user");
        commentExist.setUser(user);
        commentExist.setPost(postSaved);

        Comment commentToUpdate = new Comment();
        commentToUpdate.setContent("content update");
        commentToUpdate.setName("user");

        Comment commentUpdated = new Comment();
        commentUpdated.setId(1L);
        commentUpdated.setContent("content update");
        commentUpdated.setName("user");
        commentUpdated.setUser(user);
        commentUpdated.setPost(postSaved);

        when(this.commentRepository.save(commentExist)).thenReturn(commentUpdated);

        Comment result = this.commentService.Update(commentExist, commentToUpdate);

        assertNotNull(result);
        assertEquals(commentUpdated.getId(), result.getId());
        assertEquals(commentUpdated.getContent(), result.getContent());
        assertEquals(postSaved, result.getPost());
        assertEquals(user, result.getUser());

        verify(this.commentRepository, times(1)).save(commentExist);
    }

}
