package main.repository;

import main.model.GlobalSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface GlobalSettingRepository extends JpaRepository<GlobalSetting, Integer> {

    /**
     * Ищет значение настройки по кодовому наименованию
     *
     * @param code кодовое название настройки
     * @return значение YES или NO
     */
    @Query(value = "select value from global_settings where code = ?1", nativeQuery = true)
    String findValueByCode(String code);
}
