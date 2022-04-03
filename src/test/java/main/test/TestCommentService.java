package main.test;

import main.repository.PostCommentRepository;
import main.request.CommentRequest;
import main.request.LoginRequest;
import main.service.AuthService;
import main.service.CommentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;


import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class TestCommentService {

    @Autowired
    private CommentService commentService;
    @Autowired
    private AuthService authService;
    @Autowired
    private PostCommentRepository postCommentRepository;

    @DisplayName("Запрос каптчи")
    @Test
    @Transactional
    void testAddComment() {
        authService.login(new LoginRequest("email1@mail.ru", "password1"));

        //неправильные запросы
        CommentRequest request = new CommentRequest(20, 1, "Текст комментария");
        assertEquals(400, commentService.addComment(request).getStatusCodeValue());
        request = new CommentRequest(1, 20, "Текст комментария");
        assertEquals(400, commentService.addComment(request).getStatusCodeValue());
        request = new CommentRequest(1, 1, "");
        assertEquals(400, commentService.addComment(request).getStatusCodeValue());

        //правильные запросы
        request = new CommentRequest(1, 1, "Текст комментария");
        ResponseEntity<Object> response = commentService.addComment(request);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(9, ((Map<String, Integer>) response.getBody()).get("id"));
        assertTrue(postCommentRepository.findById(9).isPresent());

        request = new CommentRequest(null, 1, "Текст комментария");
        response = commentService.addComment(request);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(10, ((Map<String, Integer>) response.getBody()).get("id"));
        assertTrue(postCommentRepository.findById(10).isPresent());

        authService.logout();
    }
}
