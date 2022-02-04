package main.service;

import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AuthService {

    public Map<String, Object> check() {
        return Map.of("result", false);
    }
}
