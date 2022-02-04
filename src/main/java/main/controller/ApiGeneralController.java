package main.controller;

import main.response.InitResponse;
import main.service.GlobalSettingService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class ApiGeneralController {

    private final GlobalSettingService globalSettingService;
    private final InitResponse initResponse;

    public ApiGeneralController(GlobalSettingService globalSettingService, InitResponse initResponse) {
        this.globalSettingService = globalSettingService;
        this.initResponse = initResponse;
    }

    @GetMapping("/init")
    private InitResponse init(){
        return initResponse;
    }

    @GetMapping("/settings")
    private Map<String, Boolean> settings(){
        return globalSettingService.getSettings();
    }
}
