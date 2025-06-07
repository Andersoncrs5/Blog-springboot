package br.com.Blog.api.unitary;

import br.com.Blog.api.entities.Followers;
import br.com.Blog.api.entities.User;
import br.com.Blog.api.repositories.FollowersRepository;
import br.com.Blog.api.services.FollowersService;
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

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FollowersUnitaryTest {

    @Mock
    private FollowersRepository repository;

    @InjectMocks
    private FollowersService service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testFollow() {
        Map<String, User> users = this.setUsers();

        User user = users.get("user");
        User followed = users.get("user1");

        Followers follower = new Followers();
        follower.setId(1L);
        follower.setFollower(user);
        follower.setFollowed(followed);

        when(repository.existsByFollowerAndFollowed(user, followed)).thenReturn(false);
        when(repository.save(any(Followers.class))).thenReturn(follower);

        Followers result = this.service.follow(user, followed);

        assertNotNull(result);
        assertEquals(user, result.getFollower());
        assertEquals(followed, result.getFollowed());
        assertNotNull(result.getId());

        verify(repository, times(1)).save(any(Followers.class));
        verify(repository, times(1)).existsByFollowerAndFollowed(user, followed);

        InOrder inOrder = inOrder(repository);

        inOrder.verify(repository).existsByFollowerAndFollowed(user, followed);
        inOrder.verify(repository).save(any(Followers.class));
    }

    @Test
    public void testUnfollow() {
        Map<String, User> users = this.setUsers();

        User user = users.get("user");
        User followed = users.get("user1");

        Followers save = new Followers();
        save.setId(1L);
        save.setFollower(user);
        save.setFollowed(followed);

        when(repository.findByFollowerAndFollowed(user, followed)).thenReturn(save);
        doNothing().when(repository).delete(save);

        var result = this.service.unfollow(user, followed);

        assertNotNull(result);
        assertEquals(save.getFollower(), result.getFollower());
        assertEquals(save.getFollowed(), result.getFollowed());
        assertEquals(save.getId(), result.getId());

        verify(repository, times(1)).findByFollowerAndFollowed(user, followed);
        verify(repository, times(1)).delete(save);

        InOrder inOrder = inOrder(repository);

        inOrder.verify(repository).findByFollowerAndFollowed(user, followed);
        inOrder.verify(repository).delete(save);
    }

    @Test
    public void testAreFollowingTrue() {
        Map<String, User> users = this.setUsers();

        User user = users.get("user");
        User followed = users.get("user1");

        Followers save = new Followers();
        save.setId(1L);
        save.setFollower(user);
        save.setFollowed(followed);

        when(repository.existsByFollowerAndFollowed(user, followed)).thenReturn(true);

        boolean result = this.service.areFollowing(user, followed);

        assertTrue(result);

        verify(repository, times(1)).existsByFollowerAndFollowed(user, followed);
    }

    @Test
    public void testAreFollowingFalse() {
        Map<String, User> users = this.setUsers();

        User user = users.get("user");
        User followed = users.get("user1");

        when(repository.existsByFollowerAndFollowed(user, followed)).thenReturn(false);

        boolean result = this.service.areFollowing(user, followed);

        assertFalse(result);

        verify(repository, times(1)).existsByFollowerAndFollowed(user, followed);
    }

    @Test
    public void testGetMutualFollowed() {
        User user1 = new User();
        user1.setId(1L);

        User user2 = new User();
        user2.setId(2L);

        User mutual1 = new User();
        mutual1.setId(10L);

        User mutual2 = new User();
        mutual2.setId(11L);

        List<User> mutualList = List.of(mutual1, mutual2);
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> mutualPage = new PageImpl<>(mutualList, pageable, mutualList.size());

        when(repository.findMutualFollowed(user1, user2, pageable)).thenReturn(mutualPage);

        ResponseEntity<?> response = service.getMutualFollowed(user1, user2, pageable);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        @SuppressWarnings("unchecked")
        Page<User> resultPage = (Page<User>) response.getBody();
        assertNotNull(resultPage);
        assertEquals(2, resultPage.getContent().size());
        assertEquals(mutual1.getId(), resultPage.getContent().get(0).getId());

        verify(repository, times(1)).findMutualFollowed(user1, user2, pageable);
    }

    @Test
    public void testGetAllFollowed() {
        User user = new User();
        user.setId(1L);
        user.setName("Usuário");

        User followed1 = new User();
        followed1.setId(2L);

        User followed2 = new User();
        followed2.setId(3L);

        Followers f1 = new Followers();
        f1.setId(1L);
        f1.setFollower(user);
        f1.setFollowed(followed1);

        Followers f2 = new Followers();
        f2.setId(2L);
        f2.setFollower(user);
        f2.setFollowed(followed2);

        List<Followers> follows = List.of(f1, f2);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Followers> page = new PageImpl<>(follows, pageable, follows.size());

        when(repository.findAllByFollower(user, pageable)).thenReturn(page);

        Page<Followers> result = service.getAllFollowed(user, pageable);

        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals(f1.getFollowed().getId(), result.getContent().get(0).getFollowed().getId());

        verify(repository, times(1)).findAllByFollower(user, pageable);
    }


    private Map<String, User> setUsers() {
        User user = new User();

        user.setId(1L);
        user.setName("test");
        user.setEmail("test@gmail.com");
        user.setPassword("12345678");

        User user1 = new User();

        user.setId(2L);
        user.setName("test");
        user.setEmail("test1@gmail.com");
        user.setPassword("12345678");

        return Map.of("user", user, "user1", user1);

    }
}
