package main.response;

import lombok.Getter;
import main.model.Post;

@Getter
public class PostResponse {

    public PostResponse(Post post) {
        id = post.getId();
        timestamp = post.getTime().getTime() / 1000;
        user.id = post.getUser().getId();
        user.name = post.getUser().getName();
        title = post.getTitle();
        announce = getAnnounce(post.getText());
        likeCount = (int) post.getVotes().stream().filter(o -> o.getValue() == 1).count();
        dislikeCount = post.getVotes().size() - likeCount;
        commentCount = post.getComments().size();
        viewCount = post.getViewCount();
    }

    private String getAnnounce(String text){
        if (text.length() > 150) {
            text = text.substring(0, 146);
            text = text + "...";
        }
        text = text.replaceAll("<.*?>" , " ");
        return text;
    }

    private final int id;
    private final long timestamp;
    private final User user = new User();
    private final String title;
    private final String announce;
    private final int likeCount;
    private final int dislikeCount;
    private final int commentCount;
    private final int viewCount;

    @Getter
    public static class User{
        private int id;
        private String name;
    }
}
