package main.test;

import main.response.InitResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class TestInit {

    @Autowired
    private InitResponse initResponse;

    @Test
    void test(){
        Assertions.assertEquals("InitResponse(title=DevPub, subtitle=Рассказы разработчиков, phone=+7 903 666-44-55, email=mail@mail.ru, copyright=Дмитрий Сергеев, copyrightFrom=2005)" , initResponse.toString());
    }
}
