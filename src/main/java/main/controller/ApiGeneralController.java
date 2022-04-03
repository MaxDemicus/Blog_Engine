package main.controller;

import main.request.CommentRequest;
import main.response.CalendarResponse;
import main.response.InitResponse;
import main.response.TagResponse;
import main.service.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class ApiGeneralController {

    private final GlobalSettingService globalSettingService;
    private final InitResponse initResponse;
    private final TagsService tagsService;
    private final PostService postService;
    private final GeneralService generalService;
    private final CommentService commentService;

    public ApiGeneralController(GlobalSettingService globalSettingService, InitResponse initResponse, TagsService tagsService, PostService postService, GeneralService generalService, CommentService commentService) {
        this.globalSettingService = globalSettingService;
        this.initResponse = initResponse;
        this.tagsService = tagsService;
        this.postService = postService;
        this.generalService = generalService;
        this.commentService = commentService;
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

    @PostMapping(path = "/image", consumes = { "multipart/form-data" })
    @PreAuthorize("hasAuthority('WRITE')")
    public ResponseEntity<Object> postImage(MultipartFile image) {
        return generalService.saveImage(image);
    }

    @PostMapping("/comment")
    @PreAuthorize("hasAuthority('WRITE')")
    public ResponseEntity<Object> postComment(@RequestBody CommentRequest request) {
        return commentService.addComment(request);
    }
}
