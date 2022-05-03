package main.service;

import main.model.GlobalSetting;
import main.repository.GlobalSettingRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GlobalSettingService {

    private final GlobalSettingRepository globalSettingRepository;

    public GlobalSettingService(GlobalSettingRepository globalSettingRepository) {
        this.globalSettingRepository = globalSettingRepository;
    }

    /**
     * Возвращает глобальные настройки блога
     *
     * @return HashMap, где key - название настройки, value - значение настройки ('YES' или 'NO')
     */
    public Map<String, Boolean> getSettings() {
        Map<String, Boolean> settings = new HashMap<>();
        for (GlobalSetting setting : globalSettingRepository.findAll()) {
            settings.put(setting.getCode(), setting.getValue().equals("YES"));
        }
        return settings;
    }

    /**
     * Записывает глобальные настройки блога в базу данных
     *
     * @param request список настроек и их значений
     */
    public void saveSettings(Map<String, Boolean> request) {
        List<GlobalSetting> settings = globalSettingRepository.findAll();
        for (GlobalSetting setting : settings) {
            Boolean newValue = request.getOrDefault(setting.getCode(), null);
            if (newValue == null) {
                continue;
            }
            if (newValue) {
                setting.setValue("YES");
            } else {
                setting.setValue("NO");
            }
        }
        globalSettingRepository.saveAll(settings);
    }
}
