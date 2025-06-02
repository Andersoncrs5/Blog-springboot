package br.com.Blog.api.unitary;

import br.com.Blog.api.config.JwtService;
import br.com.Blog.api.entities.User;
import br.com.Blog.api.repositories.PostRepository;
import br.com.Blog.api.repositories.UserRepository;
import br.com.Blog.api.services.CustomUserDetailsService;
import br.com.Blog.api.services.UserService;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserUnitaryTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private PostRepository postRepository;

    @Mock
    private AuthenticationManager authManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private CustomUserDetailsService userDetailsService;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateUser() {
        User user = new User();

        user.setName("test");
        user.setEmail("test@gmail.com");
        user.setPassword("12345678");

        when(passwordEncoder.encode(anyString())).thenReturn("12345678");
        when(this.userRepository.save(user)).thenReturn(user);

        User userCreated = this.userService.create(user);

        assertNotNull(userCreated, "User come null of userService.create");
        assertEquals(userCreated.getEmail(), user.getEmail(), "Emails are differents");
        assertEquals(userCreated.getName(), user.getName(), "Emails are differents");

        verify(userRepository, times(1)).save(user);
        verify(passwordEncoder, times(1)).encode("12345678");
    }

    @Test
    public void testGetUserIfThrowBadRequest() {
        ResponseStatusException badRequet1 = assertThrows(ResponseStatusException.class, () -> {
           this.userService.get(0L);
        });

        ResponseStatusException badRequet2 = assertThrows(ResponseStatusException.class, () -> {
            this.userService.get(-1L);
        });

        assertNotNull(badRequet1);
        assertNotNull(badRequet2);
        assertEquals(HttpStatus.BAD_REQUEST,badRequet1.getStatusCode());
        assertEquals(HttpStatus.BAD_REQUEST,badRequet2.getStatusCode());
    }

    @Test
    public void testGetUserIfThrowNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        ResponseStatusException badRequet1 = assertThrows(ResponseStatusException.class, () -> {
            this.userService.get(999L);
        });

        assertNotNull(badRequet1);
        assertEquals(HttpStatus.NOT_FOUND,badRequet1.getStatusCode());

        verify(userRepository, times(1)).findById(999L);
    }

    @Test
    public void testGetUser() {
        User user = new User();

        user.setId(1L);
        user.setName("test");
        user.setEmail("test@gmail.com");
        user.setPassword("12345678");

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        User userFound = userService.get(user.getId());

        assertNotNull(userFound, "User come null of userService.get");

        assertEquals(userFound.getEmail(), user.getEmail(), "Emails are differents");
        assertEquals(userFound.getName(), user.getName(), "Name are differents");
        verify(userRepository, times(1)).findById(user.getId());
    }

    @Test
    public void testDeleteUser() {
        User user = new User();

        user.setId(1L);
        user.setName("test");
        user.setEmail("test@gmail.com");
        user.setPassword("12345678");

        doNothing().when(this.userRepository).delete(user);

        this.userService.delete(user);

        verify(this.userRepository, times(1)).delete(user);
    }

    @Test
    public void testUpdateUser() {
        User userInDb = new User();
        userInDb.setId(1L);
        userInDb.setName("original");
        userInDb.setEmail("test@gmail.com");
        userInDb.setPassword("oldpassword");

        User userInput = new User();
        userInput.setName("test update");
        userInput.setEmail("test@gmail.com");
        userInput.setPassword("12345678");

        User userUpdatedMock = new User();
        userUpdatedMock.setId(1L);
        userUpdatedMock.setName("test update");
        userUpdatedMock.setEmail("test@gmail.com");
        userUpdatedMock.setPassword("12345678");

        when(passwordEncoder.encode("12345678")).thenReturn("12345678");

        when(userRepository.save(any(User.class))).thenReturn(userUpdatedMock);

        User updatedUser = this.userService.update(userInDb, userInput);

        assertNotNull(updatedUser);
        assertEquals("test update", updatedUser.getName());
        assertEquals("test@gmail.com", updatedUser.getEmail());
        assertEquals("12345678", updatedUser.getPassword());

        verify(userRepository, times(1)).save(userInDb);
    }

    @Test
    public void testIfUserLogin() {
        String token = "token-example";
        String refreshToken = "refreshToken-example";

        User userInDb = new User();
        userInDb.setId(1L);
        userInDb.setName("original");
        userInDb.setEmail("test@gmail.com");
        userInDb.setPassword("12345678");

        UserDetails principal = mock(UserDetails.class);

        when(userRepository.findByEmail(userInDb.getEmail())).thenReturn(userInDb);

        when(passwordEncoder.matches("12345678", userInDb.getPassword())).thenReturn(true);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(principal);

        when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);

        when(jwtService.generateToken(principal, userInDb.getId())).thenReturn(token);
        when(jwtService.generateRefreshtoken(principal, userInDb.getId())).thenReturn(refreshToken);

        when(userRepository.save(userInDb)).thenReturn(userInDb);

        var response = this.userService.login(userInDb.getEmail(), userInDb.getPassword());

        assertNotNull(response);
        assertNotNull(response.get("token"));
        assertNotNull(response.get("refresh"));

        verify(jwtService, times(1)).generateToken(principal, userInDb.getId());
        verify(jwtService, times(1)).generateRefreshtoken(principal, userInDb.getId());
        verify(userRepository, times(1)).findByEmail(userInDb.getEmail());
        verify(userRepository, times(1)).save(userInDb);
    }

    @Test
    public void testlogout() {
        User userInDb = new User();

        userInDb.setId(1L);
        userInDb.setName("original");
        userInDb.setEmail("test@gmail.com");
        userInDb.setPassword("12345678");

        when(userRepository.save(any(User.class))).thenReturn(userInDb);

        User user = this.userService.logout(userInDb);

        assertNotNull(user);

        assertEquals(user.getEmail(), userInDb.getEmail());
        assertEquals(user.getName(), userInDb.getName());
        assertEquals("", user.getRefreshToken());

        verify(userRepository, times(1)).save(userInDb);
    }

    @Test
    public void testRefreshToken() {
        User userInDb = new User();
        userInDb.setId(1L);
        userInDb.setName("original");
        userInDb.setEmail("test@gmail.com");
        userInDb.setPassword("12345678");

        String refreshToken = "refresh-token example";
        String token = "new-token";
        String newRefresh = "new-refresh-token";

        Claims claims = mock(Claims.class);
        when(claims.getExpiration()).thenReturn(new Date(System.currentTimeMillis() + 100000));
        when(claims.getSubject()).thenReturn(userInDb.getEmail());
        when(claims.get("userId", Long.class)).thenReturn(userInDb.getId());

        when(jwtService.extractAllClaims(refreshToken)).thenReturn(claims);

        UserDetails userDetails = mock(UserDetails.class);
        when(userDetailsService.loadUserByUsername(userInDb.getEmail())).thenReturn(userDetails);

        when(jwtService.generateToken(userDetails, userInDb.getId())).thenReturn(token);
        when(jwtService.generateRefreshtoken(userDetails, userInDb.getId())).thenReturn(newRefresh);

        var response = this.userService.refreshToken(refreshToken);

        assertNotNull(response);
        assertEquals(token, response.get("token"));
        assertEquals(newRefresh, response.get("refresh"));

        verify(jwtService, times(1)).extractAllClaims(refreshToken);
        verify(userDetailsService, times(1)).loadUserByUsername(userInDb.getEmail());
        verify(jwtService, times(1)).generateToken(userDetails, userInDb.getId());
        verify(jwtService, times(1)).generateRefreshtoken(userDetails, userInDb.getId());
    }

    @Test
    public void testThrowBadRequestInRefreshtoken() {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            this.userService.refreshToken("");
        });

        assertNotNull(exception);
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }

    @Test
    public void testThrowUnauthorizedInLogin() {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            this.userService.login("test@gmail.com", anyString());
        });

        assertNotNull(exception);
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
    }

}
