package main.controller;

import main.service.PostService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/post")
public class ApiPostController {

    private final PostService postService;

    public ApiPostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping()
    private Map<String, Object> getPost(@RequestParam(defaultValue = "0") int offset, @RequestParam(defaultValue = "10") int limit, @RequestParam(defaultValue = "recent") String mode){
        return postService.getPost(offset, limit, mode);
    }

    @GetMapping("/search")
    private Map<String, Object> getPostSearch(@RequestParam(defaultValue = "0") int offset, @RequestParam(defaultValue = "10") int limit, @RequestParam(defaultValue = "") String query){
        return postService.getPostBySearch(offset, limit, query);
    }

    @GetMapping("/byDate")
    private Map<String, Object> getPostByDate(@RequestParam(defaultValue = "0") int offset, @RequestParam(defaultValue = "10") int limit, @RequestParam String date){
        return postService.getPostByDate(offset, limit, date);
    }

    @GetMapping("/byTag")
    private Map<String, Object> getPostByTag(@RequestParam(defaultValue = "0") int offset, @RequestParam(defaultValue = "10") int limit, @RequestParam String tag){
        return postService.getPostByTag(offset, limit, tag);
    }
}
