package main.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import main.model.Post;

@Getter
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class PostAnnounceResponse extends PostResponse{

    public PostAnnounceResponse(Post post) {
        super(post);
        announce = getAnnounce(post.getText());
        commentCount = post.getComments().size();
    }

    String announce;
    int commentCount;

    private String getAnnounce(String text){
        if (text.length() > 150) {
            text = text.substring(0, 146);
            text = text + "...";
        }
        text = text.replaceAll("<.*?>" , " ");
        return text;
    }
}
