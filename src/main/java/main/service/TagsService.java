package main.service;

import main.repository.TagRepository;
import main.response.TagResponse;
import org.springframework.stereotype.Service;

import javax.persistence.Tuple;
import java.math.BigInteger;
import java.util.List;

@Service
public class TagsService {

    private final TagRepository tagRepository;

    public TagsService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    /**
     * Возвращает список тегов, начинающихся на строку, заданную в параметре query,
     * и их относительный нормированный вес от 0 до 1, соответствующий частоте встречаемости.
     * Если query не задан, возвращаются все теги.
     * @param query - строка для поиска
     * @return {@link TagResponse}: список тегов и их нормированный вес
     */
    public TagResponse getTags(String query){
        List<Tuple> tags = tagRepository.getTags(query);
        float maxCount = 0;
        for (Tuple tag : tags) {
            maxCount = Math.max(maxCount, tag.get("count", BigInteger.class).intValue());
        }
        TagResponse response = new TagResponse();
        for (Tuple tag : tags) {
            String name = tag.get("tag", String.class);
            float weight = tag.get("count", BigInteger.class).intValue() / maxCount;
            response.addTag(name, weight);
        }
        return response;
    }
}
