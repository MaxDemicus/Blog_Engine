package main.test;

import main.model.GlobalSetting;
import main.model.User;
import main.repository.GlobalSettingRepository;
import main.repository.UserRepository;
import main.request.LoginRequest;
import main.request.ProfileRequest;
import main.response.ResponseWithErrors;
import main.response.StatisticResponse;
import main.service.AuthService;
import main.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class TestUserService {

    public static final String TEST_IMAGE = "src/main/resources/static/default-1.png";

    @Autowired
    private AuthService authService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GlobalSettingRepository globalSettingRepository;

    @DisplayName("Изменение профиля")
    @Test
    @Transactional
    void testEditProfile() {
        authService.login(new LoginRequest("email1@mail.ru", "password1"));

        ProfileRequest request = new ProfileRequest("имя","email2@mail.ru", "pass", (byte) 0);
        ResponseWithErrors response = userService.editProfile(request, null).getBody();
        assertNotNull(response);
        assertFalse(response.isResult());
        assertThat(response.getErrors()).containsKeys("email", "name", "password");

        request = new ProfileRequest("user4","email4@mail.ru", null, (byte) 0);
        response = userService.editProfile(request, null).getBody();
        assertNotNull(response);
        assertTrue(response.isResult());
        Optional<User> user = userRepository.findById(1);
        assertTrue(user.isPresent());
        assertEquals("user4", user.get().getName());
        assertEquals("email4@mail.ru", user.get().getEmail());
        assertEquals("$2a$12$w3FAazMc7Pe0iPbdZKHrfuo13mq1VfN6Hbb3eF4K5V0wkyKUaQRpG", user.get().getPassword());

        authService.login(new LoginRequest("email4@mail.ru", "password1"));
        request = new ProfileRequest("user4","email4@mail.ru", "password4", (byte) 0);
        response = userService.editProfile(request, null).getBody();
        assertNotNull(response);
        assertTrue(response.isResult());
        user = userRepository.findById(1);
        assertTrue(user.isPresent());
        assertEquals("password4", user.get().getPassword());

        request = new ProfileRequest("user4","email4@mail.ru", null, (byte) 1);
        response = userService.editProfile(request, null).getBody();
        assertNotNull(response);
        assertTrue(response.isResult());
        user = userRepository.findById(1);
        assertTrue(user.isPresent());
        assertNull(user.get().getPhoto());

        try {
            request = new ProfileRequest("user4","email4@mail.ru", null, (byte) 0);
            Path testPath = Path.of(TEST_IMAGE);
            byte[] content = Files.readAllBytes(testPath);
            MultipartFile image = new MockMultipartFile("testImage", "default-1.png", "image/png", content);
            response = userService.editProfile(request, image).getBody();
            assertNotNull(response);
            assertTrue(response.isResult());
            user = userRepository.findById(1);
            assertTrue(user.isPresent());
            assertNotNull(user.get().getPhoto());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @DisplayName("Получение статистики постов")
    @Test
    @Transactional
    void testGetStatistics() {
        User testUser = userRepository.findByEmail("email3@mail.ru");
        User testModerator = userRepository.findByEmail("email2@mail.ru");

        //тест при включенном публичном доступе
        StatisticResponse response = userService.getStatistics(testUser, false).getBody();
        assertEquals(5, response.getPostsCount());
        assertEquals(5, response.getLikesCount());
        assertEquals(1, response.getDislikesCount());
        assertEquals(23, response.getViewsCount());
        assertEquals(1_224_880_346L, response.getFirstPublication());

        //доступ к полной статистике блога без авторизации
        response = userService.getStatistics(null, true).getBody();
        assertEquals(9, response.getPostsCount());
        assertEquals(5, response.getLikesCount());
        assertEquals(5, response.getDislikesCount());
        assertEquals(38, response.getViewsCount());
        assertEquals(1_224_880_346L, response.getFirstPublication());

        //отключаем публичный доступ к статистике
        GlobalSetting statisticsSetting = globalSettingRepository.findById(3).orElseThrow();
        statisticsSetting.setValue("NO");
        globalSettingRepository.saveAndFlush(statisticsSetting);

        //тест при выключенном публичном доступе
        int code = userService.getStatistics(testUser, false).getStatusCodeValue();
        assertEquals(401, code);
        code = userService.getStatistics(testModerator, false).getStatusCodeValue();
        assertEquals(200, code);
    }
}
