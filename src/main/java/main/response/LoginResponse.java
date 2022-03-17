package main.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LoginResponse {

    public LoginResponse(boolean result) {
        this.result = result;
    }

    private boolean result;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private UserResponse user;
}
