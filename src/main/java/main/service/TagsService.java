package main.service;

import main.repository.PostRepository;
import main.repository.TagRepository;
import main.response.TagResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class TagsService {

    private final TagRepository tagRepository;
    private final PostRepository postRepository;

    public TagsService(TagRepository tagRepository, PostRepository postRepository) {
        this.tagRepository = tagRepository;
        this.postRepository = postRepository;
    }

    public Map<String, List<TagResponse>> getTags(String query){
        List<TagResponse> tags = tagRepository.getTags(query);
        int postCount = postRepository.countActivePosts();
        float maxWeight = 0;
        for (TagResponse tag : tags){
            tag.setWeight(tag.getWeight() / postCount);
            if (tag.getWeight() > maxWeight)
                maxWeight = tag.getWeight();
        }
        for (TagResponse tag : tags){
            tag.setWeight(tag.getWeight() / maxWeight);
        }
        return Map.of("tags", tags);
    }
}
