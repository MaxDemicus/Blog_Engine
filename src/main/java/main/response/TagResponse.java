package main.response;

import lombok.Getter;
import lombok.Setter;

@Getter
public class TagResponse {

    public TagResponse(String name, int weight) {
        this.name = name;
        this.weight = weight;
    }

    private final String name;
    @Setter
    private float weight;
}
