package main.service;

import main.enums.PostStatusInDB;
import main.enums.PostStatusInRequest;
import main.enums.SortMode;
import main.model.Post;
import main.model.Tag;
import main.model.User;
import main.repository.PostRepository;
import main.repository.TagRepository;
import main.repository.UserRepository;
import main.request.PostRequest;
import main.response.*;
import main.response.post.InnerPostAnnounceResponse;
import main.response.post.InnerPostFullResponse;
import main.response.post.InnerPostResponse;
import main.response.post.PostResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.persistence.Tuple;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final TagRepository tagRepository;
    private final AuthService authService;

    public PostService(PostRepository postRepository, UserRepository userRepository, TagRepository tagRepository, AuthService authService) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.tagRepository = tagRepository;
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
        PageRequest page = PageRequest.of(offset, limit, SortMode.valueOf(mode).getSort());
        Page<Post> posts = postRepository.findActivePosts(page);
        return getResponse(posts);
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
        PageRequest page = PageRequest.of(offset, limit, SortMode.recent.getSort());
        Page<Post> posts = postRepository.findActivePostsBySearch(query, page);
        return getResponse(posts);
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
        Page<Post> posts = postRepository.findActivePostsByDate(date, page);
        return getResponse(posts);
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
        Page<Post> posts = postRepository.findActivePostsByTag(tag, page);
        return getResponse(posts);
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
        Page<Post> posts;
        if (status.equalsIgnoreCase("inactive")) {
            posts = postRepository.findInactivePostsByUser(id, page);
        } else {
            String statusInDB = PostStatusInRequest.valueOf(status).getStatusInDB();
            posts = postRepository.findPostsByUserAndStatus(id, statusInDB, page);
        }
        return getResponse(posts);
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
        Page<Post> posts;
        if (status.equalsIgnoreCase(PostStatusInDB.NEW.toString())) {
            posts = postRepository.findModeration(page);
        } else {
            int id = authService.check().getUser().getId();
            posts = postRepository.findPostsByModeratorAndStatus(id, status.toUpperCase(), page);
        }
        return getResponse(posts);
    }

    private PostResponse getResponse(Page<Post> posts) {
        List<InnerPostResponse> responses = posts.getContent().stream().map(InnerPostAnnounceResponse::new).collect(Collectors.toList());
        return new PostResponse(posts.getTotalElements(), responses);
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
        if (post == null) {
            return ResponseEntity.notFound().build();
        }
        LoginResponse auth = authService.check();
        //если пользователь не авторизован или не является модератором или автором поста, увеличить количество просмотров
        if ((!auth.isResult()) || (!auth.getUser().isModeration() && auth.getUser().getId() != post.getUser().getId())) {
            post.setViewCount(post.getViewCount() + 1);
            postRepository.save(post);
        }
        return ResponseEntity.ok(new InnerPostFullResponse(post));
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

    /**
     * Создаёт новый пост, если в введённых данных нет ошибок
     *
     * @param request данные поста: время публикации, скрыт ли пост, название, текст, теги
     * @return true, если ошибок нет, false и список ошибок, если они есть
     */
    public ResponseWithErrors addPost(PostRequest request) {
        Map<String, String> errors = checkPostRequest(request);
        if (!errors.isEmpty()) {
            return new ResponseWithErrors(errors);
        }
        Post post = new Post();
        post.fillFromRequest(request);
        post.setModerationStatus(PostStatusInDB.NEW);
        String author = SecurityContextHolder.getContext().getAuthentication().getName();
        post.setUser(userRepository.findByEmail(author));
        List<Tag> tags = new ArrayList<>();
        for (String tagName : request.getTags()) {
            tags.add(tagRepository.findByName(tagName));
        }
        post.setTags(tags);
        postRepository.saveAndFlush(post);
        return new ResponseWithErrors(true);
    }

    /**
     * Метод изменяет данные поста с идентификатором ID на те, которые пользователь ввёл в форму
     * публикации.
     *
     * @param request данные поста: время публикации, скрыт ли пост, название, текст, теги
     * @return true, если ошибок нет, false и список ошибок, если они есть
     */
    public ResponseWithErrors editPost(PostRequest request, int postID) {
        Map<String, String> errors = checkPostRequest(request);
        if (!errors.isEmpty()) {
            return new ResponseWithErrors(errors);
        }
        Post post = postRepository.findById(postID).orElse(null);
        if (post == null) {
            return new ResponseWithErrors(false);
        }
        post.fillFromRequest(request);
        String curUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User curUser = userRepository.findByEmail(curUserEmail);
        if (curUser.getId() == post.getUser().getId()) {
            post.setModerationStatus(PostStatusInDB.NEW);
        }
        List<Tag> tags = new ArrayList<>();
        for (String tagName : request.getTags()) {
            tags.add(tagRepository.findByName(tagName));
        }
        post.setTags(tags);
        postRepository.saveAndFlush(post);
        return new ResponseWithErrors(true);
    }

    private Map<String, String> checkPostRequest(PostRequest request) {
        Map<String, String> errors = new HashMap<>();
        if (request.getTitle().length() < 3) {
            errors.put("title", "Заголовок не установлен");
        }
        if (request.getText().length() < 50) {
            errors.put("text", "Текст публикации слишком короткий");
        }
        return errors;
    }


}
