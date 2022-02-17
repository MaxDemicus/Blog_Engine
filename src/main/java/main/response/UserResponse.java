package main.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class UserResponse {
    int id;
    String name;
    String photo;
    String email;
    boolean moderation;
    int moderationCount;
    boolean settings;
}
