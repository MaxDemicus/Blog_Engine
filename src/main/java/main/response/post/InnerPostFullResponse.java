package main.response.post;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import main.enums.PostStatusInDB;
import main.model.Post;
import main.model.PostComment;
import main.model.Tag;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class InnerPostFullResponse extends InnerPostResponse {

    public InnerPostFullResponse(Post post) {
        super(post);
        active = post.getIsActive() == 1 && post.getModerationStatus() == PostStatusInDB.ACCEPTED && !post.getTime().after(new Date());
        text = post.getText();
        comments = post.getComments().stream().map(Comment::new).collect(Collectors.toList());
        tags = post.getTags().stream().map(Tag::getName).collect(Collectors.toList());
    }

    boolean active;
    String text;
    List<Comment> comments;
    List<String> tags;

    @Getter
    @FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
    public static class Comment{
        int id;
        long timestamp;
        String text;
        Map<String, Object> user;

        public Comment(PostComment comment) {
            id = comment.getId();
            timestamp = comment.getTime().getTime();
            text = comment.getText();
            user = new HashMap<>();
            user.put("id", comment.getUser().getId());
            user.put("name", comment.getUser().getName());
            user.put("photo", comment.getUser().getPhoto());
        }
    }
}
