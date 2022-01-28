package main.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

@Entity(name = "captcha_codes")
public class CaptchaCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotNull
    private Timestamp time;

    @NotNull
    private byte code;

    @NotNull
    @Column(name = "secret_code")
    private byte secretCode;
}
