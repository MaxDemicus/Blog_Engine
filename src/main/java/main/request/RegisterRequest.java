package main.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class RegisterRequest {
    @JsonProperty("e_mail")
    String eMail;
    String password;
    String name;
    String captcha;
    @JsonProperty("captcha_secret")
    String captchaSecret;
}
