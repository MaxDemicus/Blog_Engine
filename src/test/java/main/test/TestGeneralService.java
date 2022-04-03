package main.test;

import main.service.GeneralService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class TestGeneralService {

    @Autowired
    private GeneralService generalService;

    @DisplayName("Запрос каптчи")
    @Test
    @Transactional
    void testSaveImage() {
        try {
            Path testPath = Path.of("src/main/resources/static/default-1.png");
            byte[] content = Files.readAllBytes(testPath);
            MultipartFile image = new MockMultipartFile("testImage", "default-1.png", "image/png", content);
            ResponseEntity<Object> response = generalService.saveImage(image);
            assertEquals(200, response.getStatusCodeValue());
            Object body = response.getBody();
            assertTrue(body instanceof String);
            Path path = Path.of("src/main/resources" + body);
            assertTrue(Files.isRegularFile(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
