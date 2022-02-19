package main.service;

import com.github.cage.Cage;
import com.github.cage.GCage;
import main.repository.CaptchaRepository;
import main.response.UserResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class AuthService {

    @Value("${captcha_obsolete_period:60}")
    int captchaObsoletePeriod;

    private final CaptchaRepository captchaRepository;

    public AuthService(CaptchaRepository captchaRepository) {
        this.captchaRepository = captchaRepository;
    }

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

    /**
     * Генерирует коды капчи, - отображаемый и секретный, - сохраняет их в базу данных.
     * Также метод удаляет устаревшие капчи из базы.
     * @return возвращает Map с двумя ключами:
     * секретный код secret
     * и изображение размером 100х35 с отображённым на ней основным кодом капчи image в виде строки формата base64
     */
    @Transactional
    public Map<String, String> getCaptchaCode() {
        Cage cage = new GCage();
        String code = cage.getTokenGenerator().next();
        byte[] imageBytes = cage.draw(code);
        String image64 = Base64.getEncoder().encodeToString(imageBytes);
        String secret = UUID.nameUUIDFromBytes(imageBytes).toString().replaceAll("-", "");
        captchaRepository.saveCaptcha(code, secret);
        captchaRepository.deleteObsolete(captchaObsoletePeriod);
        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("secret", secret);
        responseBody.put("image", "data:image/png;base64, " + image64);
        return responseBody;
    }
}
