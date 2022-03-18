package main.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import main.model.User;

import java.util.Map;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoginResponse {

    final boolean result;
    UserResponse user;
    Map<String, String> errors;

    public static LoginResponse success(User user) {
        LoginResponse response = new LoginResponse(true);
        response.setUser(new UserResponse(user));
        return response;
    }

    public static LoginResponse registersError(Map<String, String> errors) {
        LoginResponse response = new LoginResponse(false);
        response.setErrors(errors);
        return response;
    }

    @Data
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class UserResponse {

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
}
