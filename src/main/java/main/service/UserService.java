package main.service;

import main.model.User;
import main.repository.UserRepository;
import main.request.ProfileRequest;
import main.response.ResponseWithErrors;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final GeneralService generalService;

    public UserService(UserRepository userRepository, GeneralService generalService) {
        this.userRepository = userRepository;
        this.generalService = generalService;
    }

    /**
     * Возвращает текущего авторизованного пользователя
     *
     * @return текущий пользователь или null, если авторизация не выполнена
     */
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            return userRepository.findByEmail(authentication.getName());
        }
        return null;
    }

    /**
     * Изменяет данные профиля
     *
     * @param photo картинка для размещения в профиле
     * @param request данные, введённые пользователем в форму
     * @return True, усли сохранение успешно, false и список ошибок, если нет
     */
    public ResponseEntity<ResponseWithErrors> editProfile(ProfileRequest request, MultipartFile photo) {
        User user = getCurrentUser();
        Map<String, String> errors = new HashMap<>();
        if (!user.getEmail().equalsIgnoreCase(request.getEmail()) && userRepository.findByEmail(request.getEmail()) != null) {
            errors.put("email", "Этот e-mail уже зарегистрирован");
        }
        if (photo != null && photo.getSize() > GeneralService.MAX_FILE_SIZE) {
            errors.put("photo", "Фото слишком большое, нужно не более 5 Мб");
        }
        if (!request.getName().matches("\\w+")) {
            errors.put("name", "Имя указано неверно");
        }
        if (request.getPassword() != null && request.getPassword().length() < 6) {
            errors.put("password", "Пароль короче 6-ти символов");
        }
        if (!errors.isEmpty()) {
            return ResponseEntity.ok(new ResponseWithErrors(errors));
        }
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        if (request.getPassword() != null) {
            user.setPassword(request.getPassword());
        }
        if (request.getRemovePhoto() == 1) {
            user.setPhoto(null);
        }
        if (request.getRemovePhoto() == 0 && photo != null) {
            MultipartFile croppedPhoto = cropPhoto(photo);
            ResponseEntity<Object> pathToPhoto = generalService.saveImage(croppedPhoto);
            if (pathToPhoto.getStatusCodeValue() == 200 && pathToPhoto.getBody() != null) {
                user.setPhoto(pathToPhoto.getBody().toString());
            }
        }
        userRepository.saveAndFlush(user);
        return ResponseEntity.ok(new ResponseWithErrors(true));
    }

    private MultipartFile cropPhoto(MultipartFile originalPhoto) {
        MockMultipartFile result = new MockMultipartFile(originalPhoto.getName(), originalPhoto.getOriginalFilename(), originalPhoto.getContentType(), new byte[0]);
        try (ByteArrayInputStream bais = new ByteArrayInputStream(originalPhoto.getBytes());
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            BufferedImage image = ImageIO.read(bais);
            int xStart = image.getWidth() / 2 - 18;
            int yStart = image.getHeight() / 2 - 18;
            BufferedImage croppedImage = image.getSubimage(xStart, yStart, 36, 36);
            ImageIO.write(croppedImage, "jpg", baos);
            result = new MockMultipartFile(originalPhoto.getName(), originalPhoto.getOriginalFilename(), originalPhoto.getContentType(), baos.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
