package main.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import main.model.User;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoginResponse {

    final boolean result;
    UserResponse user;

    public LoginResponse(boolean result) {
        this.result = result;
    }

    public LoginResponse(User user) {
        this.result = true;
        this.user = new UserResponse(user);
    }

    @Data
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class UserResponse {

        private UserResponse(User user) {
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
}
