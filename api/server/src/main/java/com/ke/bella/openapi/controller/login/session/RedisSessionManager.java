package com.ke.bella.openapi.controller.login.session;

import com.ke.bella.openapi.common.model.Operator;
import com.ke.bella.openapi.controller.login.user.IUserRepo;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import jakarta.servlet.http.HttpServletRequest;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
public class RedisSessionManager implements SessionManager, TicketManager {

    private final SessionProperty sessionProperty;
    private final RedisTemplate<String, Operator> redisTemplate;
    private final StringRedisTemplate stringRedisTemplate;
    @Setter
    private IUserRepo userRepo;
    private String sessionPrefix = "bella-openapi-session-user:";
    private String ticketPrefix = "bella-openapi-oauth-ticket:";

    public RedisSessionManager(SessionProperty sessionProperty, RedisTemplate<String, Operator> redisTemplate,
            StringRedisTemplate stringRedisTemplate) { // Constructor name
                                                       // changed
        this.sessionProperty = sessionProperty;
        this.redisTemplate = redisTemplate;
        this.stringRedisTemplate = stringRedisTemplate;
        if(sessionProperty.getSessionPrefix() != null) {
            sessionPrefix = sessionProperty.getSessionPrefix();
        }
    }

    @Override
    public boolean userRepoInitialized() {
        return userRepo != null;
    }

    @Override
    public String create(Operator sessionInfo, HttpServletRequest request) {
        // 如果配置了用户持久化，则进行持久化
        if(userRepo != null) {
            sessionInfo = userRepo.persist(sessionInfo);
        }
        return createSession(sessionInfo);
    }

    @Override
    public String create(String secret, HttpServletRequest request) {
        if(userRepo == null) {
            throw new UnsupportedOperationException();
        }
        Operator operator = userRepo.checkSecret(secret);
        if(operator == null) {
            return null;
        }
        return createSession(operator);
    }

    @Override
    public String createByPassword(String email, String password, HttpServletRequest request) {
        if(userRepo == null) {
            throw new UnsupportedOperationException();
        }
        Operator operator = userRepo.checkPassword(email, password);
        if(operator == null) {
            return null;
        }
        return createSession(operator);
    }

    @Override
    public Operator register(String email, String password, String userName) {
        if(userRepo == null) {
            throw new UnsupportedOperationException();
        }
        return userRepo.register(email, password, userName);
    }

    private String createSession(Operator sessionInfo) {
        String id = UUID.randomUUID().toString();
        ValueOperations<String, Operator> ops = redisTemplate.opsForValue();
        ops.set(sessionPrefix + id, sessionInfo, sessionProperty.getMaxInactiveInterval(), TimeUnit.MINUTES);
        return id;
    }

    @Override
    public Operator getSession(HttpServletRequest request) {
        String id = extractToken(request);
        if(id == null) {
            return null;
        }
        return loadById(id);
    }

    @Override
    public void destroySession(HttpServletRequest request) {
        String sessionId = extractToken(request);
        if(sessionId != null) {
            deleteById(sessionId);
        }
    }

    @Override
    public void renew(HttpServletRequest request) {
        try {
            String id = extractToken(request);
            if(id != null) {
                expire(id, sessionProperty.getMaxInactiveInterval());
            }
        } catch (Exception e) {
            log.warn("session renew error", e);
        }
    }

    @Override
    public void saveTicket(String ticket) {
        stringRedisTemplate.opsForValue().set(getTicketKey(ticket), ticket, 10, TimeUnit.MINUTES);
    }

    @Override
    public boolean isValidTicket(String ticket) {
        // Ensure stringRedisTemplate is not null if this method can be called
        // before full initialization
        Boolean hasKey = stringRedisTemplate.hasKey(getTicketKey(ticket));
        return Boolean.TRUE.equals(hasKey); // Handle null from hasKey
    }

    @Override
    public void removeTicket(String ticket) {
        stringRedisTemplate.delete(getTicketKey(ticket));
    }

    private void deleteById(String id) {
        redisTemplate.delete(sessionPrefix + id);
    }

    private Operator loadById(String id) {
        ValueOperations<String, Operator> ops = redisTemplate.opsForValue();
        return ops.get(sessionPrefix + id);
    }

    private void expire(String id, int maxInactiveInterval) {
        redisTemplate.expire(sessionPrefix + id, maxInactiveInterval, TimeUnit.MINUTES);
    }

    private String extractToken(HttpServletRequest request) {
        if(request == null) {
            return null;
        }
        return request.getHeader(sessionProperty.getAuthTokenHeader());
    }

    private String getTicketKey(String ticket) {
        return ticketPrefix + ticket;
    }

}
