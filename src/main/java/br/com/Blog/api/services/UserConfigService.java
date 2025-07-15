package br.com.Blog.api.services;

import br.com.Blog.api.entities.User;
import br.com.Blog.api.entities.UserConfig;
import br.com.Blog.api.repositories.UserConfigRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserConfigService {

    private final UserConfigRepository repository;
    private final RedisTemplate<String, Object> redisTemplate;

    public UserConfig get(User user) {
        log.info("Starting search by user config");
        if (user.getId() <= 0) {
            log.info("user was not passed to the get method inside UserConfigRepository");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is required");
        }

        Optional<UserConfig> optional = this.repository.findByUser(user);

        if (optional.isEmpty()) {
            log.info("User config not found! returning ResponseStatusException");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User config not found");
        }

        log.info("User config founded! returning UserConfig");
        return optional.get();
    }

    public UserConfig getInCached(User user) {
        log.info("Starting search by user config");
        if (user.getId() <= 0) {
            log.info("user was not passed to the get method inside UserConfigRepository");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is required");
        }

        final String KEY = user.getId()+"_config";
        log.info("Searching user config in cache...");
        UserConfig userConfig = (UserConfig) this.redisTemplate.opsForValue().get(KEY);

        if (userConfig != null) {
            log.info("User config founded in cache! Returning");
            return userConfig;
        }

        log.info("User config not found in cache. Searching in database...");
        Optional<UserConfig> optional = this.repository.findByUser(user);

        if (optional.isEmpty()) {
            log.info("User config not found in database.");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User config not found");
        }
        log.info("Saved UserConfig in cache");

        redisTemplate.opsForValue().set(KEY, optional.get(), Duration.ofMinutes(10));

        log.info("returning UserConfig");
        return optional.get();
    }

    @Async
    public void delete(UserConfig config) {
        log.info("Starting delete user config");
        if (config.getId() <= 0) {
            log.info("Config was not passed to the delete method inside UserConfigRepository! returning ResponseStatusException");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is required");
        }

        this.repository.delete(config);
        log.info("Config deleted!");
    }

    @Async
    public void save(User user, UserConfig config) {
        log.info("Starting save user config");

        if (user.getId() <= 0) {
            log.info("user was not passed to the save method inside UserConfigRepository");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is required");
        }
        config.setId(null);
        config.setUser(user);

        this.repository.save(config);
    }

    public UserConfig update(UserConfig configToUpdate, UserConfig configOriginal) {
        log.info("Starting UserConfig update");
        configToUpdate.setId(configOriginal.getId());
        configToUpdate.setUser(configOriginal.getUser());

        log.info("User config updated");
        return this.repository.save(configToUpdate);
    }

}
