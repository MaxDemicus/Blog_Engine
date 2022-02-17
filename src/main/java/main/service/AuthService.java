package main.service;

import main.response.UserResponse;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {

    /**
     * Возвращает информацию о текущем авторизованном пользователе, если он авторизован
     * @return true и информация о текущем пользователе, если он авторизован, и false, если нет
     */
    public Map<String, Object> check() {
        if (true) // пока не реализована авторизация
            return Map.of("result", false);
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("result", true);
        UserResponse user = new UserResponse(576, "Дмитрий Петров", "/avatars/ab/cd/ef/52461.jpg", "perov@petroff.ru", true, 56, true);// заглушка
        responseBody.put("user", user);
        return responseBody;
    }
}
