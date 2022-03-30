package main.controller;

import main.request.PostRequest;
import main.response.ResponseWithErrors;
import main.response.post.InnerPostFullResponse;
import main.response.post.PostResponse;
import main.service.PostService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/post")
public class ApiPostController {

    private final PostService postService;

    public ApiPostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping()
    public PostResponse getPost(@RequestParam(defaultValue = "0") int offset, @RequestParam(defaultValue = "10") int limit, @RequestParam(defaultValue = "recent") String mode){
        return postService.getPost(offset, limit, mode);
    }

    @GetMapping("/search")
    public PostResponse getPostSearch(@RequestParam(defaultValue = "0") int offset, @RequestParam(defaultValue = "10") int limit, @RequestParam(defaultValue = "") String query){
        return postService.getPostBySearch(offset, limit, query);
    }

    @GetMapping("/byDate")
    public PostResponse getPostByDate(@RequestParam(defaultValue = "0") int offset, @RequestParam(defaultValue = "10") int limit, @RequestParam String date){
        return postService.getPostByDate(offset, limit, date);
    }

    @GetMapping("/byTag")
    public PostResponse getPostByTag(@RequestParam(defaultValue = "0") int offset, @RequestParam(defaultValue = "10") int limit, @RequestParam String tag){
        return postService.getPostByTag(offset, limit, tag);
    }

    @GetMapping("/{ID}")
    public ResponseEntity<InnerPostFullResponse> getPostById(@PathVariable int ID){
        return postService.getPostById(ID);
    }

    @GetMapping("/my")
    @PreAuthorize("hasAuthority('WRITE')")
    public PostResponse getMyPosts(@RequestParam(defaultValue = "0") int offset, @RequestParam(defaultValue = "10") int limit, @RequestParam String status) {
        return postService.getMyPosts(offset, limit, status);
    }

    @GetMapping("/moderation")
    @PreAuthorize("hasAuthority('MODERATE')")
    public PostResponse getModeratedPosts(@RequestParam(defaultValue = "0") int offset, @RequestParam(defaultValue = "10") int limit, @RequestParam String status) {
        return postService.getModeratedPosts(offset, limit, status);
    }

    @PostMapping("")
    @PreAuthorize("hasAuthority('WRITE')")
    public ResponseWithErrors addPost(@RequestBody PostRequest request) {
        return postService.addPost(request);
    }

    @PutMapping("/{ID}")
    @PreAuthorize("hasAuthority('WRITE')")
    public ResponseWithErrors editPost(@RequestBody PostRequest request, @PathVariable int ID) {
        return postService.editPost(request, ID);
    }
}
