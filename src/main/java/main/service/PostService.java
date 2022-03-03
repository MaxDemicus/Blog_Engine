package main.service;

import main.model.Post;
import main.repository.PostRepository;
import main.response.PostAnnounceResponse;
import main.response.PostFullResponse;
import main.response.UserResponse;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.JpaSort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.persistence.Tuple;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final AuthService authService;

    public PostService(PostRepository postRepository, AuthService authService) {
        this.postRepository = postRepository;
        this.authService = authService;
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
     * <li> ключ 'posts' - список публикаций и сопутствующей информации для отображения на одной странице в виде объектов {@link PostAnnounceResponse}
     * </ul>
     */
    public Map<String, Object> getPost(int offset, int limit, String mode) {
        PageRequest page = PageRequest.of(offset, limit, getSort(mode));
        List<Post> posts = postRepository.findActivePosts(page);
        int postCount = postRepository.countActivePosts();
        return formResponseBody(postCount, posts);
    }

    /**
     * Возвращает посты, соответствующие поисковому запросу. В случае, если запрос
     * пустой или содержит только пробелы, метод должен выводить все посты.
     * @param offset номер страницы, 0 по умолчанию
     * @param limit количество постов на странице, 10 по умолчанию
     * @param query поисковый запорс
     * @return Map, в котором:
     * <ul>
     * <li> ключ 'count' - общее количество постов, которое доступно по данному запросу
     * <li> ключ 'posts' - список публикаций и сопутствующей информации для отображения на одной странице в виде объектов {@link PostAnnounceResponse}
     * </ul>
     */
    public Map<String, Object> getPostBySearch(int offset, int limit, String query) {
        PageRequest page = PageRequest.of(offset, limit, getSort("recent"));
        List<Post> posts = postRepository.findActivePostsBySearch(query, page);
        int postCount = postRepository.countActivePostsBySearch(query);
        return formResponseBody(postCount, posts);
    }

    /**
     * Выводит посты за указанную дату, переданную в запросе в параметре date.
     * @param offset номер страницы, 0 по умолчанию
     * @param limit количество постов на странице, 10 по умолчанию
     * @param date поисковый запрос
     * @return Map, в котором:
     * <ul>
     * <li> ключ 'count' - общее количество постов, которое доступно по данному запросу
     * <li> ключ 'posts' - список публикаций и сопутствующей информации для отображения на одной странице в виде объектов {@link PostAnnounceResponse}
     * </ul>
     */
    public Map<String, Object> getPostByDate(int offset, int limit, String date) {
        PageRequest page = PageRequest.of(offset, limit);
        List<Post> posts = postRepository.findActivePostsByDate(date, page);
        int postCount = postRepository.countActivePostsByDate(date);
        return formResponseBody(postCount, posts);
    }

    /**
     * Выводит список постов, привязанных к тэгу, который был передан методу в качестве параметра
     * tag
     * @param offset сдвиг от 0 для постраничного вывода, 0 по умолчанию
     * @param limit количество постов, которое надо вывести, 10 по умолчанию
     * @param tag тэг, по которому нужно вывести все посты
     * @return Map, в котором:
     * <ul>
     * <li> ключ 'count' - общее количество постов, которое доступно по данному запросу
     * <li> ключ 'posts' - список публикаций и сопутствующей информации для отображения на одной странице в виде объектов {@link PostAnnounceResponse}
     * </ul>
     */
    public Map<String, Object> getPostByTag(int offset, int limit, String tag) {
        PageRequest page = PageRequest.of(offset, limit);
        List<Post> posts = postRepository.findActivePostsByTag(tag, page);
        int postCount = postRepository.countActivePostsByTag(tag);
        return formResponseBody(postCount, posts);
    }

    private Map<String, Object> formResponseBody(int postCount, List<Post> posts){
        List<PostAnnounceResponse> responses = posts.stream().map(PostAnnounceResponse::new).collect(Collectors.toList());
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("count", postCount);
        responseBody.put("posts", responses);
        return responseBody;
    }

    /**
     * Возвращает данные конкретного поста, в том числе список
     * комментариев и тегов, привязанных к данному посту. Если пост не найден, возвращает код 404 (Документ на найден)
     * @param id номер поста
     * @return полную информацию о посте в виде объекта {@link PostFullResponse} или код 404, если пост не найден
     */
    public ResponseEntity<PostFullResponse> getPostById(int id){
        Post post = postRepository.findById(id).orElse(null);
        if (post != null) {
            increaseViewCount(post);
            return ResponseEntity.ok(new PostFullResponse(post));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    private void increaseViewCount(Post post){
        Map<String, Object> auth = authService.check();
        boolean userAuthorized = (boolean) auth.get("result");
        if (userAuthorized){
            UserResponse user = (UserResponse) auth.get("user");
            if (user.isModeration() || user.getId() == post.getUser().getId()) {
                return;
            }
        }
        post.setViewCount(post.getViewCount() + 1);
        postRepository.save(post);
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

    /**
     * Возвращает количества публикаций на каждую дату переданного в параметре year года
     * или текущего года, если параметр year не задан.
     * @param year - год в виде четырёхзначного числа, если не передан - возвращать за текущий год.
     * @return Map, в котором:
     * <ul>
     * <li> ключ 'years' - список всех годов, за которые была хотя бы одна публикация,
     *  в порядке возрастания
     * <li> ключ 'posts' - количества публикаций на каждую дату года
     * </ul>
     */
    public Map<String, Object> getCalendar(String year){
        if (year == null) {
            year = String.valueOf(LocalDate.now().getYear());
        }
        Map<String, Object> counts = new HashMap<>();
        for (Tuple tuple : postRepository.getCalendar(year)){
            counts.put(String.valueOf(tuple.get("date")), tuple.get("count"));
        }
        return Map.of("years", postRepository.getYears(), "posts", counts);
    }
}
