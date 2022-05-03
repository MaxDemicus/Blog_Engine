package main.controller;

import main.request.CommentRequest;
import main.request.ModerateRequest;
import main.request.ProfileRequest;
import main.response.*;
import main.service.*;
import org.springframework.http.MediaType;
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
    private final UserService userService;

    public ApiGeneralController(GlobalSettingService globalSettingService, InitResponse initResponse, TagsService tagsService, PostService postService, GeneralService generalService, CommentService commentService, UserService userService) {
        this.globalSettingService = globalSettingService;
        this.initResponse = initResponse;
        this.tagsService = tagsService;
        this.postService = postService;
        this.generalService = generalService;
        this.commentService = commentService;
        this.userService = userService;
    }

    @GetMapping("/init")
    public InitResponse init(){
        return initResponse;
    }

    @GetMapping("/settings")
    public Map<String, Boolean> getSettings(){
        return globalSettingService.getSettings();
    }

    @PutMapping("/settings")
    @PreAuthorize("hasAuthority('MODERATE')")
    public void saveSettings(@RequestBody Map<String, Boolean> request){
        globalSettingService.saveSettings(request);
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

    @PostMapping("/moderation")
    @PreAuthorize("hasAuthority('MODERATE')")
    public ResponseWithErrors postModerate(@RequestBody ModerateRequest request) {
        return postService.moderatePost(request);
    }

    @PostMapping(path = "/profile/my", consumes = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("hasAuthority('WRITE')")
    public ResponseEntity<ResponseWithErrors> postProfileWithoutPhoto(@RequestBody ProfileRequest request) {
        return userService.editProfile(request, null);
    }

    @PostMapping(path = "/profile/my", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @PreAuthorize("hasAuthority('WRITE')")
    public ResponseEntity<ResponseWithErrors> postProfileWithPhoto(@ModelAttribute ProfileRequest request, @RequestPart MultipartFile photo) {
        return userService.editProfile(request, photo);
    }

    @GetMapping("/statistics/my")
    @PreAuthorize("hasAuthority('WRITE')")
    public ResponseEntity<StatisticResponse> getMyStatistics() {
        return userService.getStatistics(userService.getCurrentUser(), false);
    }

    @GetMapping("/statistics/all")
    public ResponseEntity<StatisticResponse> getAllStatistics() {
        return userService.getStatistics(userService.getCurrentUser(), true);
    }
}
