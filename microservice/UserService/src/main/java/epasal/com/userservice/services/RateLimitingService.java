package epasal.com.userservice.services;

import epasal.com.userservice.exception.OtpTimeOutException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class RateLimitingService {
    private final static Duration REQUEST_TIMEOUT_DURATION = Duration.ofSeconds(30);
    private final static Duration MAX_REQUESTS_TIMEOUT = Duration.ofHours(24);
    private final static int MAX_REQUESTS = 5;
    private static final int MAX_ATTEMPTS = 5;
    private static final Duration WINDOW_DURATION = Duration.ofMinutes(10);
    private final StringRedisTemplate redisTemplate;

    public void requestTimeout(String email) {
        log.info("Checking rate limit for email: {}", email);
        String timeout = "user:timeout:" + email;
        String request = "user:request:" + email;

        Long ttl = redisTemplate.getExpire(timeout, TimeUnit.SECONDS);
        if (ttl != null && ttl > 0) {
            log.warn("Rate limit exceeded for email: {}. Time to wait: {} seconds", email, ttl);
            throw new OtpTimeOutException("Try again after " + ttl + " seconds");
        }

        Long count = redisTemplate.opsForValue().increment(request, 1);
        if (count == 1) {
            redisTemplate.expire(request, MAX_REQUESTS_TIMEOUT);
        }
        if (count > MAX_REQUESTS) {
            log.warn("Maximum request limit reached for email: {}. Setting timeout for 24 hours.", email);
            redisTemplate.opsForValue().set(timeout, "true", MAX_REQUESTS_TIMEOUT);
            throw new OtpTimeOutException("Limit reached. Try again after 24 hours");
        }

        Duration cooldown = REQUEST_TIMEOUT_DURATION.multipliedBy(count);
        redisTemplate.opsForValue().set(timeout, "true", cooldown);
        log.info("Rate limit updated for email: {}. Current count: {}. Cooldown set to: {} seconds", email, count, cooldown.getSeconds());
    }

    public void resetLimit(String email) {
        log.info("Resetting rate limit for email: {}", email);
        String timeout = "user:timeout:" + email;
        String request = "user:request:" + email;

        redisTemplate.delete(timeout);
        redisTemplate.delete(request);
    }

    public void checkRateLimit(String email) throws OtpTimeOutException {
        log.info("Checking attempt limit for email: {}", email);
        String key = "user:attempts:" + email;

        Long attempts = redisTemplate.opsForValue().increment(key, 1);
        log.debug("Attempt count for email {}: {}", email, attempts);
        if (attempts == 1) {
            redisTemplate.expire(key, WINDOW_DURATION);
        }
        if (attempts > MAX_ATTEMPTS) {
            throw new OtpTimeOutException("Attempt Limit Exceeded. Please try again later.");
        }
    }
}
