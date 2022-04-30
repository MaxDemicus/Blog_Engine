package main.response;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StatisticResponse {

    int postsCount;
    long likesCount;
    long dislikesCount;
    int viewsCount;
    long firstPublication;
}
