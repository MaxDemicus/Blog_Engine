package main.model;

import lombok.Getter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

@Entity(name = "captcha_codes")
@Getter
public class CaptchaCode {

    public CaptchaCode(String code, String secretCode) {
        this.code = code;
        this.secretCode = secretCode;
        this.time = new Timestamp(System.currentTimeMillis());
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotNull
    private final Timestamp time;

    @NotNull
    @Column(columnDefinition = "tinytext")
    private final String code;

    @NotNull
    @Column(name = "secret_code", columnDefinition = "tinytext")
    private final String secretCode;
}
