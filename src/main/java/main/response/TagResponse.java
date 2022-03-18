package main.response;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@Data
public class TagResponse {

    private final List<WeightedTag> tags = new ArrayList<>();

    public void addTag(String name, float weight) {
        tags.add(new WeightedTag(name, weight));
    }

    @Data
    @EqualsAndHashCode
    public static class WeightedTag {

        private final String name;
        private final float weight;
    }
}
