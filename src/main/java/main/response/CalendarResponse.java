package main.response;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.Map;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CalendarResponse {

    List<Integer> years;
    Map<String, Integer> posts;
}
