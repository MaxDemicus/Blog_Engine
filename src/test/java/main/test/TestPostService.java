package main.test;

import main.response.CalendarResponse;
import main.response.post.InnerPostFullResponse;
import main.response.post.InnerPostResponse;
import main.response.post.PostResponse;
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
}
