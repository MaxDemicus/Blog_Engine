package main.service;

import main.response.ResponseWithErrors;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

@Service
public class GeneralService {

    public final int MAX_FILE_SIZE = 10_485_760; //10MB

    /**
     * Загружает на сервер изображение
     *
     * @param image файл с изображением
     * @return путь до изображения на сервере
     */
    public ResponseEntity<Object> saveImage(MultipartFile image) {
        System.out.println();
        if (image.getSize() > MAX_FILE_SIZE) {
            return getErrorResponse("Размер файла превышает допустимый размер");
        }
        if (!Objects.equals(image.getContentType(), "image/jpeg") && !Objects.equals(image.getContentType(), "image/png")) {
            return getErrorResponse("Формат файла не jpg или png");
        }
        String randDir = String.format("/%s/%s/%s/%s.jpg", getRandomString(2), getRandomString(2), getRandomString(2), getRandomString(5));
        String path = "src/main/resources/static/upload" + randDir;
        try {
            Files.createDirectories(Path.of(path).getParent());
            FileOutputStream stream = new FileOutputStream(path);
            stream.write(image.getBytes());
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok("/static/upload" + randDir);
    }

    private String getRandomString(int length) {
            char[] symbols = "0123456789abcdefghijklmnopqrstuvwxyz".toCharArray();
            Random rnd = new Random();
            StringBuilder result = new StringBuilder(length);
            for (int i = 0; i < length; i++) {
                result.append(symbols[rnd.nextInt(36)]);
            }
            return result.toString();
    }

    private ResponseEntity<Object> getErrorResponse(String error) {
        ResponseWithErrors response = new ResponseWithErrors(false);
        Map<String, String> errors = Map.of("image", error);
        response.setErrors(errors);
        return ResponseEntity.badRequest().body(response);
    }
}
