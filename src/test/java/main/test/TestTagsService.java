package main.test;

import main.response.TagResponse;
import main.service.TagsService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class TestTagsService {

    @Autowired
    private TagsService tagsService;

    @DisplayName("Получение списка тегов")
    @Test
    void testGetTags(){
        TagResponse.WeightedTag tag1 = new TagResponse.WeightedTag("tag1 для постов 1 и 2", 0.5f);
        TagResponse.WeightedTag tag2 = new TagResponse.WeightedTag("tag4 для постов 4 и 5", 1f);
        assertThat(tagsService.getTags("").getTags()).containsOnly(tag1, tag2);
        tag1 = new TagResponse.WeightedTag("tag1 для постов 1 и 2", 1f);
        assertThat(tagsService.getTags("tag1").getTags()).containsOnly(tag1);
    }
}
