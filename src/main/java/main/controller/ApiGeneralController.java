package main.controller;

import main.response.CalendarResponse;
import main.response.InitResponse;
import main.response.TagResponse;
import main.service.GlobalSettingService;
import main.service.PostService;
import main.service.TagsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class ApiGeneralController {

    private final GlobalSettingService globalSettingService;
    private final InitResponse initResponse;
    private final TagsService tagsService;
    private final PostService postService;

    public ApiGeneralController(GlobalSettingService globalSettingService, InitResponse initResponse, TagsService tagsService, PostService postService) {
        this.globalSettingService = globalSettingService;
        this.initResponse = initResponse;
        this.tagsService = tagsService;
        this.postService = postService;
    }

    @GetMapping("/init")
    public InitResponse init(){
        return initResponse;
    }

    @GetMapping("/settings")
    public Map<String, Boolean> getSettings(){
        return globalSettingService.getSettings();
    }

    @GetMapping("/tag")
    public TagResponse getTags(@RequestParam(defaultValue = "") String query){
        return tagsService.getTags(query);
    }

    @GetMapping("/calendar")
    public CalendarResponse getCalendar(@RequestParam(required = false) String year){
        return postService.getCalendar(year);
    }
}
