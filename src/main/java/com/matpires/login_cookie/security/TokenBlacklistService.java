package com.matpires.login_cookie.security;

import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TokenBlacklistService {

    private final Set<String> blacklist = ConcurrentHashMap.newKeySet();

    public void blacklist(String jti) {
        blacklist.add(jti);
    }

    public boolean isBlacklisted(String jti) {
        return blacklist.contains(jti);
    }
}
