package main.response;

import lombok.Getter;
import lombok.Setter;

public class TagResponse {

    public TagResponse(String name, int weight) {
        this.name = name;
        this.weight = weight;
    }

    @Getter
    private final String name;
    @Getter
    @Setter
    private float weight;
}
