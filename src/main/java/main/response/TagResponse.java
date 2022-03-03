package main.response;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@EqualsAndHashCode
@ToString
public class TagResponse {

    public TagResponse(String name, float weight) {
        this.name = name;
        this.weight = weight;
    }

    private final String name;
    private final float weight;
}
