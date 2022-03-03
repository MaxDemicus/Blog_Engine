package main.repository;

import main.model.CaptchaCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CaptchaRepository extends JpaRepository<CaptchaCode, Integer> {

    /**
     * Ищет в таблице Captcha_Codes количество записей с указанными кодами
     * @param code код каптчи, введённый пользователем
     * @param secretCode идентификационный код каптчи в базе
     * @return количество найденных записей
     */
    @Query("select count(cc) from captcha_codes cc where code = :code and secret_code = :secretCode")
    byte checkCaptcha(String code, String secretCode);

    /**
     * Удаляет устаревшие каптчи из таблицы.
     * @param captchaObsoletePeriod Время устаревания в минутах. Задано в файле конфигурации в параметре captcha_obsolete_period (по умолчанию 60).
     */
    @Query(value = "delete from captcha_codes where time < timestampadd(minute, -:captchaObsoletePeriod, now())", nativeQuery = true)
    @Modifying
    void deleteObsolete(int captchaObsoletePeriod);
}
