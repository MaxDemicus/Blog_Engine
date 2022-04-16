package main.test;

import main.model.CaptchaCode;
import main.repository.CaptchaRepository;
import main.repository.UserRepository;
import main.request.LoginRequest;
import main.request.RegisterRequest;
import main.response.LoginResponse;
import main.response.ResponseWithErrors;
import main.service.AuthService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

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
        String secret = authService.getCaptchaCode().getSecret();
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
        ResponseWithErrors response = authService.register(testRequest);
        assertFalse(response.isResult());
        assertThat(response.getErrors()).as("ошибки вводных данных не обнаружены").containsKeys("email", "name", "password", "captcha");

        //Проверка правильных данных
        testRequest = new RegisterRequest("test_mail@mail.ru", "password", "name", "code", "secret");
        response = authService.register(testRequest);
        assertNotNull(userRepository.findByEmail("test_mail@mail.ru"), "Пользователь не добавлен в базу");
        assertTrue(response.isResult());
        assertNull(response.getErrors(), "Формат ответа не верный");
    }

    @DisplayName("Авторизация")
    @Test
    @Transactional
    void testLoginCheckLogout() {
        LoginResponse response = authService.check();
        assertFalse(response.isResult());

        response = authService.login(new LoginRequest("wrong@mail.ru", "password"));
        assertFalse(response.isResult());

        response = authService.login(new LoginRequest("email1@mail.ru", "password1"));
        assertTrue(response.isResult());
        assertNotNull(response.getUser());

        response = authService.check();
        assertTrue(response.isResult());
        assertNotNull(response.getUser());

        authService.logout();
        response = authService.check();
        assertFalse(response.isResult());
    }

    @DisplayName("Восстановление пароля")
    @Test
    @Transactional
    void testRestorePass() {
        assertFalse(authService.restorePassword("email5@mail.ru").isResult());
        assertTrue(authService.restorePassword("email1@mail.ru").isResult());
    }
}
