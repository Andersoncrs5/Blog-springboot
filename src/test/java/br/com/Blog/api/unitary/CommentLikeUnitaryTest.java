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
    private CommentMetricsService metricsService;

    @Mock
    private CommentMetricsRepository metricsRepository;

    @Mock
    private CommentLikeRepository commentLikeRepository;

    @InjectMocks
    private CommentLikeService service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testReactToCommentLikeSuccessfully() {
        User user = new User();
        user.setId(1L);

        Comment comment = new Comment();
        comment.setId(1L);

        CommentMetrics metrics = new CommentMetrics();
        metrics.setId(1L);
        metrics.setLikes(0L);
        metrics.setDislikes(0L);

        CommentLike savedLike = new CommentLike();
        savedLike.setId(1L);
        savedLike.setUser(user);
        savedLike.setComment(comment);
        savedLike.setStatus(LikeOrUnLike.LIKE);

        // mocks
        when(metricsService.get(comment)).thenReturn(metrics);
        when(commentLikeRepository.existsByUserAndComment(user, comment)).thenReturn(false);
        when(commentLikeRepository.save(any(CommentLike.class))).thenReturn(savedLike);
        when(metricsRepository.save(metrics)).thenReturn(metrics);

        CommentLike result = service.reactToComment(user, comment, LikeOrUnLike.LIKE);

        assertNotNull(result);
        assertEquals(user, result.getUser());
        assertEquals(comment, result.getComment());
        assertEquals(LikeOrUnLike.LIKE, result.getStatus());

        assertEquals(1, metrics.getLikes());
        assertEquals(0, metrics.getDislikes());

        verify(metricsService).get(comment);
        verify(commentLikeRepository).existsByUserAndComment(user, comment);
        verify(metricsRepository).save(metrics);
        verify(commentLikeRepository).save(any(CommentLike.class));
    }

    @Test
    void testReactToCommentAlreadyReactedThrowsConflict() {
        User user = new User();
        user.setId(1L);

        Comment comment = new Comment();
        comment.setId(1L);

        CommentMetrics metrics = new CommentMetrics();

        when(metricsService.get(comment)).thenReturn(metrics);
        when(commentLikeRepository.existsByUserAndComment(user, comment)).thenReturn(true);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            service.reactToComment(user, comment, LikeOrUnLike.LIKE);
        });

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        verify(commentLikeRepository, never()).save(any());
        verify(metricsRepository, never()).save(any());
    }

    @Test
    void testReactToCommentDislikeSuccessfully() {
        User user = new User();
        Comment comment = new Comment();

        CommentMetrics metrics = new CommentMetrics();
        metrics.setLikes(3L);
        metrics.setDislikes(1L);

        CommentLike savedDislike = new CommentLike();
        savedDislike.setUser(user);
        savedDislike.setComment(comment);
        savedDislike.setStatus(LikeOrUnLike.UNLIKE);

        when(metricsService.get(comment)).thenReturn(metrics);
        when(commentLikeRepository.existsByUserAndComment(user, comment)).thenReturn(false);
        when(commentLikeRepository.save(any(CommentLike.class))).thenReturn(savedDislike);

        CommentLike result = service.reactToComment(user, comment, LikeOrUnLike.UNLIKE);

        assertNotNull(result);
        assertEquals(2, metrics.getDislikes());
        assertEquals(3, metrics.getLikes());

        verify(metricsRepository).save(metrics);
    }

    @Test
    void testRemoveReactionSuccessfully() {
        Long reactionId = 1L;
        User user = new User();
        Comment comment = new Comment();

        CommentLike reaction = new CommentLike();
        reaction.setId(reactionId);
        reaction.setStatus(LikeOrUnLike.LIKE);
        reaction.setComment(comment);
        reaction.setUser(user);

        CommentMetrics metrics = new CommentMetrics();
        metrics.setLikes(5L);
        metrics.setDislikes(3L);

        when(commentLikeRepository.findById(reactionId)).thenReturn(Optional.of(reaction));
        when(metricsService.get(comment)).thenReturn(metrics);

        CommentLike result = service.removeReaction(reactionId);

        assertNotNull(result);
        assertEquals(reactionId, result.getId());
        assertEquals(4, metrics.getLikes()); // decrementado

        verify(metricsService).get(comment);
        verify(metricsRepository).save(metrics);
        verify(commentLikeRepository).delete(reaction);
    }

    @Test
    void testRemoveReactionWithInvalidIdThrowsBadRequest() {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            service.removeReaction(0L);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        verify(commentLikeRepository, never()).findById(any());
    }

    @Test
    void testRemoveReactionNotFoundThrowsException() {
        Long reactionId = 99L;

        when(commentLikeRepository.findById(reactionId)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            service.removeReaction(reactionId);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    void testExistsReturnsTrue() {
        Long userId = 1L;
        Long commentId = 2L;

        when(commentLikeRepository.existsByUserIdAndCommentId(userId, commentId)).thenReturn(true);

        boolean result = service.exists(userId, commentId);

        assertTrue(result);
        verify(commentLikeRepository).existsByUserIdAndCommentId(userId, commentId);
    }

    @Test
    void testGetAllByUserReturnsPage() {
        User user = new User();
        Pageable pageable = PageRequest.of(0, 10);

        Page<CommentLike> mockPage = new PageImpl<>(List.of(new CommentLike()));

        when(commentLikeRepository.findAllByUser(user, pageable)).thenReturn(mockPage);

        ResponseEntity<?> response = service.getAllByUser(user, pageable);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockPage, response.getBody());

        verify(commentLikeRepository).findAllByUser(user, pageable);
    }


}
