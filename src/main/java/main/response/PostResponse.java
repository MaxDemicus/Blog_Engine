package main.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import main.model.Post;

@Getter
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public abstract class PostResponse {
    int id;
    long timestamp;
    User user;
    String title;
    int likeCount;
    int dislikeCount;
    int viewCount;

    public PostResponse(Post post) {
        id = post.getId();
        timestamp = post.getTime().getTime() / 1000;
        user = new User(post.getUser().getId(), post.getUser().getName());
        title = post.getTitle();
        likeCount = (int) post.getVotes().stream().filter(o -> o.getValue() == 1).count();
        dislikeCount = post.getVotes().size() - likeCount;
        viewCount = post.getViewCount();
    }

    @Getter
    @AllArgsConstructor
    @FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
    public static class User{
        int id;
        String name;
    }
}
