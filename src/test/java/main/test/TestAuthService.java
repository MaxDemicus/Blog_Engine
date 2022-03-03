package main.test;

import main.model.CaptchaCode;
import main.repository.CaptchaRepository;
import main.repository.UserRepository;
import main.request.RegisterRequest;
import main.service.AuthService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class TestAuthService {

    @Autowired
    private AuthService authService;

    @Autowired
    private CaptchaRepository captchaRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager em;

    @DisplayName("Запрос каптчи")
    @Test
    @Transactional
    void testGetCaptchaCode(){
        em.createNativeQuery("insert into captcha_codes (code, secret_code, time) values ('obsolete_code', 'secret', '1970-01-01 00:00:00')").executeUpdate();
        String secret = authService.getCaptchaCode().get("secret");
        long savedCode = (long) em.createQuery("select count(cc) from captcha_codes cc where secret_code = :secret").setParameter("secret", secret).getSingleResult();
        assertEquals(1, savedCode, "новая каптча не записалась в базу");
        byte obsolete = captchaRepository.checkCaptcha("obsolete_code", "secret");
        assertEquals(0, obsolete, "устаревшая каптча не удалена");
    }

    @DisplayName("Регистрация нового пользователя")
    @Test
    @Transactional
    void testRegister(){
        captchaRepository.save(new CaptchaCode("code", "secret"));

        //Проверка неправильных данных
        RegisterRequest testRequest = new RegisterRequest("email1@mail.ru", "pass", "имя", "wrong_code", "secret");
        Map<String, Object> response = authService.register(testRequest);
        assertThat(response).contains(entry("result", false));
        assertThat(response).as("ошибки вводных данных не обнаружены").containsKey("errors");
        if (response.containsKey("errors")) {
            Map<String, String> errors = (Map<String, String>) response.get("errors");
            assertThat(errors).as("ошибки вводных данных не обнаружены").containsKeys("email", "name", "password", "captcha");
        }

        //Проверка правильных данных
        testRequest = new RegisterRequest("test_mail@mail.ru", "password", "name", "code", "secret");
        response = authService.register(testRequest);
        assertEquals(1, userRepository.countByEmail("test_mail@mail.ru"), "Пользователь не добавлен в базу");
        assertThat(response).as("Формат ответа не верный").containsOnly(entry("result", true));
    }


}
