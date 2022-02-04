package main.response;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class InitResponse {

    @Value("${blog.title}")
    @Getter
    @Setter
    private String title;

    @Value("${blog.subtitle}")
    @Getter
    @Setter
    private String subtitle;

    @Value("${blog.phone}")
    @Getter
    @Setter
    private String phone;

    @Value("${blog.email}")
    @Getter
    @Setter
    private String email;

    @Value("${blog.copyright}")
    @Getter
    @Setter
    private String copyright;

    @Value("${blog.copyrightFrom}")
    @Getter
    @Setter
    private String copyrightFrom;
}
