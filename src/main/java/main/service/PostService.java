package main.service;

import main.enums.PostStatusInDB;
import main.enums.PostStatusInRequest;
import main.model.Post;
import main.repository.PostRepository;
import main.response.*;
import main.response.post.InnerPostAnnounceResponse;
import main.response.post.InnerPostFullResponse;
import main.response.post.InnerPostResponse;
import main.response.post.PostResponse;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.JpaSort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.persistence.Tuple;
import java.math.BigInteger;
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
     *
     * @param offset номер страницы, 0 по умолчанию
     * @param limit  количество постов на странице, 10 по умолчанию
     * @param mode   метод сортировки:
     *               <ul>
     *               <li>recent - по дате публикации, сначала новые (по умолчанию);
     *               <li>early - по дате публикации, сначала старые;
     *               <li>popular - по убыванию количества комментариев;
     *               <li>best - по убыванию количества лайков
     *               </ul>
     * @return {@link PostResponse}, в котором:
     * <ul>
     * <li> поле 'count' - общее количество постов, которое доступно по данному запросу с
     * учётом всех фильтров, параметров доступности, кроме offset и limit
     * <li> поле 'posts' - список публикаций и сопутствующей информации для отображения на одной странице в виде объектов {@link InnerPostAnnounceResponse}
     * </ul>
     */
    public PostResponse getPost(int offset, int limit, String mode) {
        PageRequest page = PageRequest.of(offset, limit, getSort(mode));
        int postCount = postRepository.countActivePosts();
        List<Post> posts = postRepository.findActivePosts(page);
        return getResponse(postCount, posts);
    }

    /**
     * Возвращает посты, соответствующие поисковому запросу. В случае, если запрос
     * пустой или содержит только пробелы, метод должен выводить все посты.
     *
     * @param offset номер страницы, 0 по умолчанию
     * @param limit  количество постов на странице, 10 по умолчанию
     * @param query  поисковый запорс
     * @return {@link PostResponse}, в котором:
     * <ul>
     * <li> поле 'count' - общее количество постов, которое доступно по данному запросу
     * <li> поле 'posts' - список публикаций и сопутствующей информации для отображения на одной странице в виде объектов {@link InnerPostAnnounceResponse}
     * </ul>
     */
    public PostResponse getPostBySearch(int offset, int limit, String query) {
        PageRequest page = PageRequest.of(offset, limit, getSort("recent"));
        List<Post> posts = postRepository.findActivePostsBySearch(query, page);
        int postCount = postRepository.countActivePostsBySearch(query);
        return getResponse(postCount, posts);
    }

    /**
     * Выводит посты за указанную дату, переданную в запросе в параметре date.
     *
     * @param offset номер страницы, 0 по умолчанию
     * @param limit  количество постов на странице, 10 по умолчанию
     * @param date   поисковый запрос
     * @return {@link PostResponse}, в котором:
     * <ul>
     * <li> поле 'count' - общее количество постов, которое доступно по данному запросу
     * <li> поле 'posts' - список публикаций и сопутствующей информации для отображения на одной странице в виде объектов {@link InnerPostAnnounceResponse}
     * </ul>
     */
    public PostResponse getPostByDate(int offset, int limit, String date) {
        PageRequest page = PageRequest.of(offset, limit);
        List<Post> posts = postRepository.findActivePostsByDate(date, page);
        int postCount = postRepository.countActivePostsByDate(date);
        return getResponse(postCount, posts);
    }

    /**
     * Выводит список постов, привязанных к тэгу, который был передан методу в качестве параметра
     * tag
     *
     * @param offset сдвиг от 0 для постраничного вывода, 0 по умолчанию
     * @param limit  количество постов, которое надо вывести, 10 по умолчанию
     * @param tag    тэг, по которому нужно вывести все посты
     * @return {@link PostResponse}, в котором:
     * <ul>
     * <li> поле 'count' - общее количество постов, которое доступно по данному запросу
     * <li> поле 'posts' - список публикаций и сопутствующей информации для отображения на одной странице в виде объектов {@link InnerPostAnnounceResponse}
     * </ul>
     */
    public PostResponse getPostByTag(int offset, int limit, String tag) {
        PageRequest page = PageRequest.of(offset, limit);
        List<Post> posts = postRepository.findActivePostsByTag(tag, page);
        int postCount = postRepository.countActivePostsByTag(tag);
        return getResponse(postCount, posts);
    }

    /**
     * Выводит список постов, которые создал текущий пользователь
     *
     * @param offset сдвиг от 0 для постраничного вывода, 0 по умолчанию
     * @param limit  количество постов, которое надо вывести, 10 по умолчанию
     * @param status status - статус модерации:
     * <ul>
     * <li> inactive - скрытые, ещё не опубликованы
     * <li> pending - активные, ожидают утверждения модератором
     * <li> declined - отклонённые по итогам модерации
     * <li> published - опубликованные по итогам модерации
     * </ul>
     * @return {@link PostResponse}, в котором:
     * <ul>
     * <li> поле 'count' - общее количество постов, которое доступно по данному запросу
     * <li> поле 'posts' - список публикаций и сопутствующей информации для отображения на одной странице в виде объектов {@link InnerPostAnnounceResponse}
     * </ul>
     */
    public PostResponse getMyPosts(int offset, int limit, String status) {
        PageRequest page = PageRequest.of(offset, limit);
        int id = authService.check().getUser().getId();
        List<Post> posts;
        int postCount;
        if (status.equalsIgnoreCase("inactive")) {
            posts = postRepository.findInactivePostsByUser(id, page);
            postCount = postRepository.countInactivePostsByUser(id);
        } else {
            String statusInDB = PostStatusInRequest.valueOf(status).getStatusInDB();
            posts = postRepository.findPostsByUserAndStatus(id, statusInDB, page);
            postCount = postRepository.countPostsByUserAndStatus(id, statusInDB);
        }
        return getResponse(postCount, posts);
    }

    /**
     * Выводит все посты, которые требуют модерационных действий (которые нужно утвердить или
     * отклонить) или над которыми текущим пользователем были совершены модерационные действия
     *
     * @param offset сдвиг от 0 для постраничного вывода, 0 по умолчанию
     * @param limit  количество постов, которое надо вывести, 10 по умолчанию
     * @param status status - статус модерации:
     * <ul>
     * <li> new - новые, необходима модерация
     * <li> declined - отклонённые текущим пользователем
     * <li> accepted - утверждённые текущим пользователем
     * </ul>
     * @return {@link PostResponse}, в котором:
     * <ul>
     * <li> поле 'count' - общее количество постов, которое доступно по данному запросу
     * <li> поле 'posts' - список публикаций и сопутствующей информации для отображения на одной странице в виде объектов {@link InnerPostAnnounceResponse}
     * </ul>
     */
    public PostResponse getModeratedPosts(int offset, int limit, String status) {
        PageRequest page = PageRequest.of(offset, limit);
        List<Post> posts;
        int postCount;
        if (status.equalsIgnoreCase(PostStatusInDB.NEW.toString())) {
            posts = postRepository.findModeration(page);
            postCount = postRepository.countModeration();
        } else {
            int id = authService.check().getUser().getId();
            posts = postRepository.findPostsByModeratorAndStatus(id, status.toUpperCase(), page);
            postCount = postRepository.countPostsByModeratorAndStatus(id, status.toUpperCase());
        }
        return getResponse(postCount, posts);
    }

    private PostResponse getResponse(int postCount, List<Post> posts) {
        List<InnerPostResponse> responses = posts.stream().map(InnerPostAnnounceResponse::new).collect(Collectors.toList());
        return new PostResponse(postCount, responses);
    }

    /**
     * Возвращает данные конкретного поста, в том числе список
     * комментариев и тегов, привязанных к данному посту. Если пост не найден, возвращает код 404 (Документ на найден)
     *
     * @param id номер поста
     * @return полную информацию о посте в виде объекта {@link InnerPostFullResponse} или код 404, если пост не найден
     */
    public ResponseEntity<InnerPostFullResponse> getPostById(int id) {
        Post post = postRepository.findById(id).orElse(null);
        if (post != null) {
            increaseViewCount(post);
            return ResponseEntity.ok(new InnerPostFullResponse(post));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    private void increaseViewCount(Post post) {
        LoginResponse auth = authService.check();
        if (auth.isResult()) {
            LoginResponse.UserResponse user = auth.getUser();
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
     *
     * @param year - год в виде четырёхзначного числа, если не передан - возвращать за текущий год.
     * @return {@link CalendarResponse}, в котором:
     * <ul>
     * <li> поле 'years' - список всех годов, за которые была хотя бы одна публикация,
     *  в порядке возрастания
     * <li> поле 'posts' - количества публикаций на каждую дату года
     * </ul>
     */
    public CalendarResponse getCalendar(String year) {
        if (year == null) {
            year = String.valueOf(LocalDate.now().getYear());
        }
        Map<String, Integer> posts = new HashMap<>();
        for (Tuple tuple : postRepository.getCalendar(year)) {
            String date = String.valueOf(tuple.get("date"));
            int count = ((BigInteger) tuple.get("count")).intValue();
            posts.put(date, count);
        }
        return new CalendarResponse(postRepository.getYears(), posts);
    }
}
