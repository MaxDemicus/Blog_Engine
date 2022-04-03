package main.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class CommentRequest {

    @JsonProperty("parent_id")
    Integer parentId;
    @JsonProperty("post_id")
    Integer postId;
    String text;
}
