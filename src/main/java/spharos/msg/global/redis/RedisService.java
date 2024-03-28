package spharos.msg.global.redis;

import java.util.Optional;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisService {

    private static final String REDIS_KEY_PREFIX = "JwtRefreshToken:";
    private static final String EMAIL_AUTH_KEY = "EmailAuthentication:";
    //RedisTemplate 주입
    private final RedisTemplate<String, String> redisTemplate;

    // Redis에 토큰 저장
    public void saveRefreshToken(String uuid, String token, long expirationTimeMs) {
        String key = REDIS_KEY_PREFIX + uuid;
        redisTemplate.opsForValue().set(key, token, expirationTimeMs, TimeUnit.MILLISECONDS);
    }

    // Redis에서 토큰 조회
    public String getRefreshToken(String uuid) {
        String key = REDIS_KEY_PREFIX + uuid;
        return redisTemplate.opsForValue().get(key);
    }

    // Redis에서 토큰 존재 유무 확인
    public Boolean isRefreshTokenExist(String uuid) {
        String key = REDIS_KEY_PREFIX + uuid;
        Optional<String> value = Optional.ofNullable(redisTemplate.opsForValue().get(key));
        return value.isPresent() && !value.get().isEmpty();
    }

    // Redis에서 토큰 삭제
    public void deleteRefreshToken(String uuid) {
        String key = REDIS_KEY_PREFIX + uuid;
        redisTemplate.delete(key);
    }

    // Email 인증 키 저장
    public void saveEmailSecretKey(String email, String secretKey, long expirationTimeMs) {
        String key = EMAIL_AUTH_KEY + email;
        redisTemplate.opsForValue().set(key, secretKey, expirationTimeMs, TimeUnit.MILLISECONDS);
    }

    // Email 인증 키 삭제
    public void deleteEmailSecretKey(String email) {
        String key = EMAIL_AUTH_KEY + email;
        redisTemplate.delete(key);
    }

    // Email 인증 키 유무 확인
    public Boolean isEmailSecretKeyExist(String email) {
        String key = EMAIL_AUTH_KEY + email;
        Optional<String> value = Optional.ofNullable(redisTemplate.opsForValue().get(key));
        return value.isPresent() && !value.get().isEmpty();
    }

    public String getEmailSecretKey(String email) {
        String key = EMAIL_AUTH_KEY + email;
        return redisTemplate.opsForValue().get(key);
    }
}