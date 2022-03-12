package main.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
public class LoginResponse {
    private boolean result;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private UserResponse user;
}
