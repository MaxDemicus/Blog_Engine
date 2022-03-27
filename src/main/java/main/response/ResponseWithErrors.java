package main.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.Map;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseWithErrors {

    final boolean result;
    Map<String, String> errors;

    public ResponseWithErrors(boolean result) {
        this.result = result;
    }

    public ResponseWithErrors(Map<String, String> errors) {
        this.result = false;
        this.errors = errors;
    }
}
