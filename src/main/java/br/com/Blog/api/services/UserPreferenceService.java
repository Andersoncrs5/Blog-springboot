package br.com.Blog.api.services;

import br.com.Blog.api.entities.Category;
import br.com.Blog.api.entities.User;
import br.com.Blog.api.entities.UserPreference;
import br.com.Blog.api.repositories.UserPreferenceRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserPreferenceService {

    private final UserPreferenceRepository repository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    @Transactional(readOnly = true)
    public UserPreference get(Long id) {
        log.info("starting to look for preference");
        if (id <= 0) {
            log.info("id to search for preference is null");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Id is required");
        }

        Optional<UserPreference> preference = this.repository.findById(id);

        if (preference.isEmpty()) {
            log.info("Preference not found");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Preference not found");
        }

        log.info("Preference founded and returned");
        return preference.get();
    }

    @Transactional
    public void remove(UserPreference preference) {
        log.info("Starting the preference delete");
        if (preference.getId() <= 0) {
            log.info("Id to search preference is null");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Preference is required");
        }

        this.repository.delete(preference);
        log.info("Preference deleted!");
    }

    @Transactional
    public UserPreference save(User user, Category category) {
        log.info("Saved new preference to user: " + user.getEmail());
        UserPreference preference = UserPreference.builder().user(user).category(category).build();

        log.info("preference saved for user: " + user.getEmail());
        return this.repository.save(preference);
    }

    @Transactional(readOnly = true)
    public Page<UserPreference> getAllOfUser(User user, Pageable pageable) {
        log.info("Starting search by preferences of user paged");
        return this.repository.findAllByUser(user, pageable);
    }

    @Transactional(readOnly = true)
    public List<UserPreference> getAllOfUser(User user) {
        log.info("Starting search by preferences of user listed");
        return this.repository.findAllByUser(user);
    }

    @Transactional(readOnly = true)
    public List<UserPreference> getAllOfUserInCache(User user) {
        log.info("Starting search by preferences of user listed in cache");

        String key = user.getId() + "_preference";

        Object cached = redisTemplate.opsForValue().get(key);

        if (cached != null) {
            log.info("Preferences found in cache");
            return objectMapper.convertValue(cached,new TypeReference<List<UserPreference>>() {});
        }

        log.info("Preferences not found in cache, querying database");

        List<UserPreference> allByUser = this.repository.findAllByUser(user);
        redisTemplate.opsForValue().set(key, allByUser, Duration.ofMinutes(5));

        log.info("Preferences cached successfully for userId {}", user.getId());
        return allByUser;
    }

}