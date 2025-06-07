package br.com.Blog.api.unitary;

import br.com.Blog.api.entities.Comment;
import br.com.Blog.api.entities.CommentLike;
import br.com.Blog.api.entities.CommentMetrics;
import br.com.Blog.api.entities.User;
import br.com.Blog.api.entities.enums.LikeOrUnLike;
import br.com.Blog.api.repositories.CommentLikeRepository;
import br.com.Blog.api.repositories.CommentMetricsRepository;
import br.com.Blog.api.services.CommentLikeService;
import br.com.Blog.api.services.CommentMetricsService;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CommentLikeUnitaryTest {

    @Mock
    private CommentLikeRepository repository;

    @InjectMocks
    private CommentLikeService service;

    private User user;
    private Comment comment;
    private CommentLike like;

    @BeforeEach
    void setup() {
        user = new User();
        user.setId(1L);

        comment = new Comment();
        comment.setId(1L);

        like = new CommentLike();
        like.setId(1L);
        like.setUser(user);
        like.setComment(comment);
        like.setStatus(LikeOrUnLike.LIKE);
    }

    @Test
    void testReactToCommentSuccessfully() {
        when(repository.existsByUserAndComment(user, comment)).thenReturn(false);
        when(repository.save(any(CommentLike.class))).thenReturn(like);

        CommentLike result = service.reactToComment(user, comment, LikeOrUnLike.LIKE);

        assertNotNull(result);
        assertEquals(user, result.getUser());
        assertEquals(comment, result.getComment());
        assertEquals(LikeOrUnLike.LIKE, result.getStatus());

        verify(repository).existsByUserAndComment(user, comment);
        verify(repository).save(any(CommentLike.class));

        InOrder inOrder = inOrder(repository);

        inOrder.verify(repository).existsByUserAndComment(user, comment);
        inOrder.verify(repository).save(any(CommentLike.class));
    }

    @Test
    void testReactToCommentAlreadyReactedThrowsConflict() {
        when(repository.existsByUserAndComment(user, comment)).thenReturn(true);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            service.reactToComment(user, comment, LikeOrUnLike.LIKE);
        });

        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
        verify(repository, never()).save(any());
    }

    @Test
    void testRemoveReactionSuccessfully() {
        Long reactionId = 1L;
        when(repository.findById(reactionId)).thenReturn(Optional.of(like));

        CommentLike result = service.removeReaction(reactionId);

        assertEquals(like, result);
        verify(repository).delete(like);
    }

    @Test
    void testRemoveReactionWithInvalidIdThrowsBadRequest() {
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            service.removeReaction(0L);
        });

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        verify(repository, never()).findById(any());
    }

    @Test
    void testRemoveReactionNotFoundThrowsException() {
        Long invalidId = 99L;
        when(repository.findById(invalidId)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            service.removeReaction(invalidId);
        });

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void testExistsReturnsTrue() {
        Long userId = 1L;
        Long commentId = 1L;

        when(repository.existsByUserIdAndCommentId(userId, commentId)).thenReturn(true);

        boolean exists = service.exists(userId, commentId);

        assertTrue(exists);
        verify(repository).existsByUserIdAndCommentId(userId, commentId);
    }

    @Test
    void testGetAllByUserReturnsPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<CommentLike> mockPage = new PageImpl<>(List.of(like));

        when(repository.findAllByUser(user, pageable)).thenReturn(mockPage);

        ResponseEntity<?> response = service.getAllByUser(user, pageable);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockPage, response.getBody());
    }
}
