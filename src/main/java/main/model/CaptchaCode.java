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
    private byte code;

    @NotNull
    @Column(name = "secret_code")
    private byte secretCode;
}
