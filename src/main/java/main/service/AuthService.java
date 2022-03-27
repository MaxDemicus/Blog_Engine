package main.service;

import com.github.cage.Cage;
import com.github.cage.GCage;
import main.model.CaptchaCode;
import main.model.User;
import main.repository.CaptchaRepository;
import main.repository.PostRepository;
import main.repository.UserRepository;
import main.request.LoginRequest;
import main.request.RegisterRequest;
import main.response.CaptchaResponse;
import main.response.LoginResponse;
import main.response.ResponseWithErrors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

@Service
public class AuthService {

    @Value("${captcha_obsolete_period:60}")
    int captchaObsoletePeriod;

    private final CaptchaRepository captchaRepository;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final PostRepository postRepository;

    public AuthService(CaptchaRepository captchaRepository, UserRepository userRepository, AuthenticationManager authenticationManager, PostRepository postRepository) {
        this.captchaRepository = captchaRepository;
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.postRepository = postRepository;
    }

    /**
     * Возвращает информацию о текущем авторизованном пользователе, если он авторизован
     *
     * @return true и информация о текущем пользователе, если он авторизован, и false, если нет
     */
    public LoginResponse check() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            return getLoginResponse(authentication.getName());
        }
        return new LoginResponse(false);
    }

    /**
     * Проверяет введенные данные и производит авторизацию пользователя, если введенные данные верны
     *
     * @param request email и пароль
     * @return информацию о пользователе, если он авторизован и false, если не авторизован
     */
    public LoginResponse login(LoginRequest request) {
        try {
            Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEMail(), request.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(auth);
        } catch (AuthenticationException e) {
            return new LoginResponse(false);
        }
        return getLoginResponse(request.getEMail());
    }

    /**
     * Разлогинивает пользователя: удаляет идентификатор его сессии из списка авторизованных
     *
     * @return {"result": true}
     */
    public LoginResponse logout() {
        SecurityContextHolder.getContext().setAuthentication(null);
        return new LoginResponse(true);
    }

    private LoginResponse getLoginResponse(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            LoginResponse response = new LoginResponse(user.get());
            if (response.getUser().isModeration()) {
                response.getUser().setModerationCount(postRepository.countModeration());
            }
            return response;
        }
        return new LoginResponse(false);
    }

    /**
     * Генерирует коды капчи, - отображаемый и секретный, - сохраняет их в базу данных.
     * Также метод удаляет устаревшие капчи из базы.
     *
     * @return возвращает {@link CaptchaResponse} с двумя полями:
     * секретный код secret
     * и изображение размером 100х35 с отображённым на ней основным кодом капчи image в виде строки формата base64
     */
    @Transactional
    public CaptchaResponse getCaptchaCode() {
        Cage cage = new GCage();
        String code = cage.getTokenGenerator().next();
        byte[] imageBytes = cage.draw(code);
        String image64 = Base64.getEncoder().encodeToString(imageBytes);
        String secret = UUID.nameUUIDFromBytes(imageBytes).toString().replaceAll("-", "");
        captchaRepository.save(new CaptchaCode(code, secret));
        captchaRepository.deleteObsolete(captchaObsoletePeriod);
        return new CaptchaResponse(secret, "data:image/png;base64, " + image64);
    }

    /**
     * Создаёт пользователя в базе данных, если введённые данные верны. Если данные неверные -
     * пользователь не создаётся, а метод возвращает соответствующую ошибку.
     *
     * @param request объект {@link RegisterRequest} с данными пользователя и проверкой каптчи
     * @return {"result": true}, если проверка пройдена. Если не пройдена, то "result":false и Map "errors" с указанием ошибок
     */
    public ResponseWithErrors register(RegisterRequest request) {
        Map<String, String> errors = checkRegisterRequest(request);
        if (errors.isEmpty()) {
            userRepository.save(User.from(request));
            return new ResponseWithErrors(true);
        } else {
            return new ResponseWithErrors(errors);
        }
    }

    private Map<String, String> checkRegisterRequest(RegisterRequest user) {
        Map<String, String> errors = new HashMap<>();
        if (userRepository.findByEmail(user.getEMail()).isPresent()) {
            errors.put("email", "Этот e-mail уже зарегистрирован");
        }
        if (!user.getName().matches("\\w+")) {
            errors.put("name", "Имя указано неверно");
        }
        if (user.getPassword().length() < 6) {
            errors.put("password", "Пароль короче 6-ти символов");
        }
        if (captchaRepository.checkCaptcha(user.getCaptcha(), user.getCaptchaSecret()) == 0) {
            errors.put("captcha", "Код с картинки введён неверно");
        }
        return errors;
    }
}
