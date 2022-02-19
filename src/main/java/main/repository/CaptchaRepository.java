package main.repository;

import main.model.CaptchaCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CaptchaRepository extends JpaRepository<CaptchaCode, Integer> {

    /**
     * Сохраняет в базу данных коды каптчи
     * @param code основной код, изображённый на картинке для пользователя
     * @param secretCode уникальный идентификатор, по которому будет найден код в таблице при проверке
     */
    @Query(value = "insert into captcha_codes (code, secret_code, time) values (:code, :secretCode, now())", nativeQuery = true)
    @Modifying
    void saveCaptcha(String code, String secretCode);

    /**
     * Удаляет устаревшие каптчи из таблицы.
     * @param captchaObsoletePeriod Время устаревания в минутах. Задано в файле конфигурации в параметре captcha_obsolete_period (по умолчанию 60).
     */
    @Query(value = "delete from captcha_codes where time < (now() - interval :captchaObsoletePeriod minute)", nativeQuery = true)
    @Modifying
    void deleteObsolete(int captchaObsoletePeriod);
}
