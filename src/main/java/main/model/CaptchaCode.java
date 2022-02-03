package main.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

@Entity(name = "captcha_codes")
public class CaptchaCode {

    public CaptchaCode() {
        this.time = new Timestamp(System.currentTimeMillis());
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private int id;

    @NotNull
    @Getter
    private Timestamp time;

    @NotNull
    @Getter
    @Setter
    private byte code;

    @NotNull
    @Getter
    @Setter
    @Column(name = "secret_code")
    private byte secretCode;
}
