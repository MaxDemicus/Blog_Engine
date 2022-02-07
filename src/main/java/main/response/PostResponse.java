package main.response;

import lombok.Getter;
import main.model.Post;

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

    @Getter
    private int id;
    @Getter
    private long timestamp;
    @Getter
    private User user = new User();
    @Getter
    private String title;
    @Getter
    private String announce;
    @Getter
    private int likeCount;
    @Getter
    private int dislikeCount;
    @Getter
    private int commentCount;
    @Getter
    private int viewCount;

    @Getter
    public static class User{
        @Getter
        private int id;
        @Getter
        private String name;
    }
}
