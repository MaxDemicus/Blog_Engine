package main.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import main.model.User;

@Getter
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class UserResponse {

    public UserResponse(User user) {
        id = user.getId();
        name = user.getName();
        photo = user.getPhoto();
        email = user.getEmail();
        moderation = user.getIsModerator() != 0;
        settings = moderation;
        moderationCount = 0;
    }

    int id;
    String name;
    String photo;
    String email;
    boolean moderation;
    int moderationCount;
    boolean settings;
}
