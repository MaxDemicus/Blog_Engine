package main.response;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class TagResponse {

    public TagResponse(String name, float weight) {
        this.name = name;
        this.weight = weight;
    }

    private final String name;
    private final float weight;
}
