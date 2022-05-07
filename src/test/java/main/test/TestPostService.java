package main.test;

import main.enums.PostStatusInDB;
import main.model.Post;
import main.model.Tag;
import main.repository.PostRepository;
import main.request.LoginRequest;
import main.request.ModerateRequest;
import main.request.PostRequest;
import main.response.CalendarResponse;
import main.response.ResponseWithErrors;
import main.response.post.InnerPostFullResponse;
import main.response.post.InnerPostResponse;
import main.response.post.PostResponse;
import main.service.AuthService;
import main.service.PostService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class TestPostService {

    @Autowired
    PostService postService;
    @Autowired
    AuthService authService;
    @Autowired
    PostRepository postRepository;

    private PostResponse response;

    @DisplayName("Список активных постов")
    @Test
    @Transactional
    void testGetPost(){
        response = postService.getPost(0, 2, "recent");
        check(9, "title12", "title10");
        response = postService.getPost(0, 2, "early");
        check(9, "title2", "title6");
        response = postService.getPost(0, 2, "popular");
        check(9, "title5", "title4");
        response = postService.getPost(0, 2, "best");
        check(9, "title4", "title2");
    }

    @DisplayName("Список постов по запросу")
    @Test
    @Transactional
    void testGetPostBySearch(){
        response = postService.getPostBySearch(0, 10,"1");
        check(3, "title12", "title10", "title11");
    }

    @DisplayName("Список постов по дате")
    @Test
    @Transactional
    void testGetPostByDate(){
        response = postService.getPostByDate(0, 10,"2022-01-07");
        check(2, "title10", "title11");
    }

    @DisplayName("Список постов по тегу")
    @Test
    @Transactional
    void testGetPostByTag(){
        response = postService.getPostByTag(0, 10,"tag4 для постов 4 и 5");
        check(2, "title4", "title5");
    }

    @DisplayName("Список постов текущего пользователя")
    @Test
    @Transactional
    void testGetMyPosts(){
        authService.login(new LoginRequest("email3@mail.ru", "password3"));
        response = postService.getMyPosts(0, 10, "inactive");
        check(1, "title13");
        response = postService.getMyPosts(0, 10, "pending");
        check(1, "title1");
        response = postService.getMyPosts(0, 10, "declined");
        check(1, "title14");
        response = postService.getMyPosts(0, 10, "published");
        check(6, "title2", "title4", "title6", "title8", "title9", "title12");
    }

    @DisplayName("Список постов, модерированных текущим пользователем")
    @Test
    @Transactional
    void testGetModeratedPost(){
        authService.login(new LoginRequest("email2@mail.ru", "password2"));
        response = postService.getModeratedPosts(0, 10, "new");
        check(1, "title1");
        response = postService.getModeratedPosts(0, 10, "accepted");
        check(3, "title4", "title7", "title10");
        response = postService.getModeratedPosts(0, 10, "declined");
        check(1, "title14");
    }

    @DisplayName("Поиск поста по id")
    @Test
    @Transactional
    void testGetPostById(){
        //поиск существующего поста
        ResponseEntity<InnerPostFullResponse> response = postService.getPostById(2);
        assertEquals(200, response.getStatusCodeValue(), "неверный код ошибки");
        InnerPostResponse post = response.getBody();
        assertNotNull(post, "пост не найден");
        assertEquals("title2", post.getTitle(), "найден неверный пост");
        assertEquals(4, post.getViewCount(), "количество просмотров некорректно");

        //поиск несуществующего поста
        response = postService.getPostById(0);
        assertEquals(404, response.getStatusCodeValue(), "неверный код ошибки");
    }

    @DisplayName("Календарь")
    @Test
    @Transactional
    void testGetCalendar(){
        CalendarResponse calendar = postService.getCalendar("2022");
        assertThat(calendar.getYears()).containsOnly(2008, 2009, 2013, 2017, 2018, 2020, 2022);
        assertThat(calendar.getPosts()).containsOnly(entry("2022-01-07", 2), entry("2022-02-01", 1));
    }

    private void check(int expectedCount, String... expectedTitles){
        assertEquals(expectedCount, response.getCount(), "Неверное количество результатов");
        List<String> titles = response.getPosts().stream().map(InnerPostResponse::getTitle).collect(Collectors.toList());
        assertThat(titles).as("Неверный результат запроса").containsExactly(expectedTitles);
    }

    @DisplayName("Добавление поста")
    @Test
    @Transactional
    void testAddPost() {
        authService.login(new LoginRequest("email2@mail.ru", "password2"));
        PostRequest request = new PostRequest(
                System.currentTimeMillis(),
                (byte) 1,
                "new_title",
                "text_text_text_text_text_text_text_text_text_text_",
                List.of("tag1 для постов 1 и 2"));
        postService.addPost(request);
        assertTrue(postRepository.findById(15).isPresent());
    }

    @DisplayName("Редактирование поста")
    @Test
    @Transactional
    void testEditPost() {
        authService.login(new LoginRequest("email3@mail.ru", "password3"));
        PostRequest request = new PostRequest(
                System.currentTimeMillis(),
                (byte) 0,
                "new_title",
                "text_text_text_text_text_text_text_text_text_text_",
                List.of("tag1 для постов 1 и 2", "tag2 для поста 1", "new_tag"));
        postService.editPost(request, 2);
        Post post = postRepository.findById(2).orElseThrow();
        assertEquals(0, post.getIsActive());
        assertEquals("new_title", post.getTitle());
        assertEquals("text_text_text_text_text_text_text_text_text_text_", post.getText());
        assertEquals(post.getModerationStatus(), PostStatusInDB.NEW);
        List<String> tags = post.getTags().stream().map(Tag::getName).collect(Collectors.toList());
        assertThat(tags).containsOnly("tag1 для постов 1 и 2", "tag2 для поста 1", "new_tag");
    }

    @DisplayName("Модерация поста")
    @Test
    @Transactional
    void testModeratePost() {
        authService.login(new LoginRequest("email1@mail.ru", "password1"));

        ModerateRequest request = new ModerateRequest(15, "accept");
        assertFalse(postService.moderatePost(request).isResult());
        request = new ModerateRequest(1, "public");
        assertFalse(postService.moderatePost(request).isResult());

        request = new ModerateRequest(1, "accept");
        ResponseWithErrors response = postService.moderatePost(request);
        assertTrue(response.isResult());
        Post post = postRepository.findById(1).orElseThrow();
        assertEquals(1, post.getModerator().getId());
        assertEquals(PostStatusInDB.ACCEPTED, post.getModerationStatus());
    }
}
