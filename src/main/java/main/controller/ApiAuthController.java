package main.controller;

import main.request.LoginRequest;
import main.request.RegisterRequest;
import main.request.RestoreRequest;
import main.response.CaptchaResponse;
import main.response.LoginResponse;
import main.response.ResponseWithErrors;
import main.service.AuthService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class ApiAuthController {

    private final AuthService authService;

    public ApiAuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/check")
    public LoginResponse check(){
        return authService.check();
    }

    @GetMapping("/captcha")
    public CaptchaResponse captcha(){
        return authService.getCaptchaCode();
    }

    @PostMapping("/register")
    public ResponseWithErrors register(@RequestBody RegisterRequest user){
        return authService.register(user);
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request){
        return authService.login(request);
    }

    @GetMapping("/logout")
    public LoginResponse logout(){
        return authService.logout();
    }

    @PostMapping("/restore")
    public ResponseWithErrors restorePass(@RequestBody RestoreRequest request) {
        return authService.restorePassword(request.getEmail());
    }
}
