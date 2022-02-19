package main.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

@Entity(name = "captcha_codes")
@Getter
@Setter
public class CaptchaCode {

    public CaptchaCode() {
        this.time = new Timestamp(System.currentTimeMillis());
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private int id;

    @NotNull
    private Timestamp time;

    @NotNull
    @Column(columnDefinition = "tinytext")
    private String code;

    @NotNull
    @Column(name = "secret_code", columnDefinition = "tinytext")
    private String secretCode;
}
