package main.service;

import main.model.Post;
import main.repository.PostRepository;
import main.response.PostResponse;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.JpaSort;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PostService {

    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public Map<String, Object> getPost(int offset, int limit, String mode) {
        PageRequest page = PageRequest.of(offset, limit, getSort(mode));
        List<Post> posts = postRepository.findActivePosts(page);
        List<PostResponse> responses = new ArrayList<>();
        for (Post post : posts)
            responses.add(new PostResponse(post));
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("count", postRepository.countActivePosts());
        responseBody.put("posts", responses);
        return responseBody;
    }

    private Sort getSort(String mode) {
        switch (mode) {
            case ("early"):
                return JpaSort.by(Sort.Direction.ASC, "time");
            case ("popular"):
                return JpaSort.unsafe(Sort.Direction.DESC, "(select count(c.post_id) from post_comments c where p.id=c.post_id)");
            case ("best"):
                return JpaSort.unsafe(Sort.Direction.DESC, "(select count(v.post_id) from post_votes v where p.id=v.post_id and v.value=1)");
            case ("recent"):
            default:
                return JpaSort.by(Sort.Direction.DESC, "time");
        }
    }
}
