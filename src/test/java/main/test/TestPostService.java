package main.test;

import main.response.PostFullResponse;
import main.response.PostResponse;
import main.service.PostService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class TestPostService {

    @Autowired
    PostService postService;

    private Map<String, Object> response;

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

    @DisplayName("Поиск поста по id")
    @Test
    @Transactional
    void testGetPostById(){
        //поиск существующего поста
        ResponseEntity<PostFullResponse> response = postService.getPostById(2);
        assertEquals(200, response.getStatusCodeValue(), "неверный код ошибки");
        PostResponse post = response.getBody();
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
        Map<String, Object> calendar = postService.getCalendar("2022");
        List<Integer> years = (List<Integer>) calendar.get("years");
        assertThat(years).containsOnly(2008, 2009, 2013, 2017, 2018, 2020, 2022);
        Map<String, Object> posts = (Map<String, Object>) calendar.get("posts");
        assertThat(posts).containsOnly(entry("2022-01-07", BigInteger.valueOf(2)), entry("2022-02-01", BigInteger.valueOf(1)));
    }

    private void check(int expectedCount, String... expectedTitles){
        assertThat(response).as("Неверное количество результатов").contains(entry("count", expectedCount));
        List<PostResponse> posts = (List<PostResponse>) response.get("posts");
        List<String> titles = posts.stream().map(PostResponse::getTitle).collect(Collectors.toList());
        assertThat(titles).as("Неверный результат запроса").containsExactly(expectedTitles);
    }
}
