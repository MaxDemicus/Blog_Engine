package main.response;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import main.model.User;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {

    public UserResponse(User user) {
        id = user.getId();
        name = user.getName();
        photo = user.getPhoto();
        email = user.getEmail();
        moderation = user.getIsModerator() != 0;
        settings = moderation;
    }

    final int id;
    final String name;
    final String photo;
    final String email;
    final boolean moderation;
    int moderationCount;
    final boolean settings;
}
