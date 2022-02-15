package main.response;

import lombok.Getter;

@Getter
public class TagResponse {

    public TagResponse(String name, float weight) {
        this.name = name;
        this.weight = weight;
    }

    private final String name;
    private final float weight;
}
