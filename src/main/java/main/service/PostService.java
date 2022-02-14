package main.service;

import main.model.Post;
import main.repository.PostRepository;
import main.response.PostResponse;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.JpaSort;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PostService {

    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    /**
     * Возвращает список активных постов со всей сопутствующей информацией для главной страницы и подразделов
     * "Новые", "Самые обсуждаемые", "Лучшие" и "Старые". Метод выводит посты, отсортированные в
     * соответствии с параметром mode.
     * @param offset номер страницы, 0 по умолчанию
     * @param limit количество постов на странице, 10 по умолчанию
     * @param mode метод сортировки:
     *             <ul>
     *             <li>recent - по дате публикации, сначала новые (по умолчанию);
     *             <li>early - по дате публикации, сначала старые;
     *             <li>popular - по убыванию количества комментариев;
     *             <li>best - по убыванию количества лайков
     *             </ul>
     * @return Map, в котором:
     * <ul>
     * <li> ключ 'count' - общее количество постов, которое доступно по данному запросу с
     * учётом всех фильтров, параметров доступности, кроме offset и limit
     * <li> ключ 'posts' - список публикаций и сопутствующей информации для отображения на одной странице в виде объектов {@link PostResponse}
     * </ul>
     */
    public Map<String, Object> getPost(int offset, int limit, String mode) {
        PageRequest page = PageRequest.of(offset, limit, getSort(mode));
        List<Post> posts = postRepository.findActivePosts(page);
        List<PostResponse> responses = new ArrayList<>();
        for (Post post : posts)
            responses.add(new PostResponse(post));
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("count", postRepository.countActivePosts());
        responseBody.put("posts", responses);
        return responseBody;
    }

    private Sort getSort(String mode) {
        switch (mode) {
            case ("early"):
                return JpaSort.by(Sort.Direction.ASC, "time");
            case ("popular"):
                return JpaSort.unsafe(Sort.Direction.DESC, "(select count(c.post_id) from post_comments c where p.id=c.post_id)");
            case ("best"):
                return JpaSort.unsafe(Sort.Direction.DESC, "(select count(v.post_id) from post_votes v where p.id=v.post_id and v.value=1)");
            case ("recent"):
            default:
                return JpaSort.by(Sort.Direction.DESC, "time");
        }
    }
}
