package com.matpires.login_cookie.security;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimitService {

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();
    private final Map<String, Integer> failedAttempts = new ConcurrentHashMap<>();

    // 🔐 Criar bucket dinâmico baseado nas falhas
    private Bucket createBucket(int attempts) {

        int capacity;
        Duration refill;

        if (attempts < 3) {
            capacity = 5;
            refill = Duration.ofMinutes(1);
        } else if (attempts < 6) {
            capacity = 3;
            refill = Duration.ofMinutes(2);
        } else {
            capacity = 1;
            refill = Duration.ofMinutes(5);
        }

        Bandwidth limit = Bandwidth.builder()
                .capacity(capacity)
                .refillGreedy(capacity, refill)
                .build();

        return Bucket.builder()
                .addLimit(limit)
                .build();
    }

    public Bucket resolveBucket(String key) {
        int attempts = failedAttempts.getOrDefault(key, 0);

        return buckets.compute(key, (k, existing) -> {
            if (existing == null) {
                return createBucket(attempts);
            }
            return existing;
        });
    }

    // 🚨 Incrementa falhas
    public void incrementFailedAttempts(String key) {
        int attempts = failedAttempts.getOrDefault(key, 0) + 1;
        failedAttempts.put(key, attempts);

        // 🔄 recria bucket com nova penalidade
        buckets.put(key, createBucket(attempts));
    }

    // ✅ Reset total (login sucesso)
    public void reset(String key) {
        failedAttempts.remove(key);
        buckets.remove(key);
    }
}
