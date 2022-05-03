package main.test;

import main.service.GlobalSettingService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.transaction.Transactional;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class TestGlobalSettingService {

    @Autowired
    private GlobalSettingService globalSettingService;

    @DisplayName("Получение списка глобальных настроек")
    @Test
    void testGetSettings(){
        Map<String, Boolean> response = globalSettingService.getSettings();
        assertThat(response).containsOnly(
                entry("MULTIUSER_MODE", true),
                entry("POST_PREMODERATION", true),
                entry("STATISTICS_IS_PUBLIC", true)
        );
    }

    @DisplayName("Сохранение глобальных настроек")
    @Test
    @Transactional
    void testSaveSettings(){
        Map<String, Boolean> request = Map.of(
                "MULTIUSER_MODE", false,
                "POST_PREMODERATION", true,
                "STATISTICS_IS_PUBLIC", false);
        globalSettingService.saveSettings(request);
        Map<String, Boolean> response = globalSettingService.getSettings();
        assertThat(response).containsOnly(
                entry("MULTIUSER_MODE", false),
                entry("POST_PREMODERATION", true),
                entry("STATISTICS_IS_PUBLIC", false)
        );
    }
}
