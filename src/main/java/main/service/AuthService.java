package main.service;

import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AuthService {

    /**
     * Возвращает информацию о текущем авторизованном пользователе, если он авторизован
     * @return информация о текущем пользователе, если он авторизован, и false, если нет
     */
    public Map<String, Object> check() {
        return Map.of("result", false);
    }
}
