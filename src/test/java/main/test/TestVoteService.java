package main.test;

import main.repository.VoteRepository;
import main.request.LoginRequest;
import main.request.VoteRequest;
import main.response.ResponseWithErrors;
import main.service.AuthService;
import main.service.VoteService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class TestVoteService {

    @Autowired
    private VoteService voteService;

    @Autowired
    private AuthService authService;

    @Autowired
    private VoteRepository voteRepository;

    @DisplayName("Лайк и дизлайк поста")
    @Test
    @Transactional
    void testVote() {
        authService.login(new LoginRequest("email1@mail.ru", "password1"));

        //повторный лайк
        ResponseWithErrors response = voteService.voteForPost(new VoteRequest(2), (byte) 1);
        assertFalse(response.isResult());

        //замена лайка на дизлайк
        response = voteService.voteForPost(new VoteRequest(2), (byte) -1);
        assertTrue(response.isResult());
        assertEquals(-1, voteRepository.findByUserIdAndPostId(1, 2).getValue(), "замена лайка на дизлайк не произошла");

        //новый лайк
        response = voteService.voteForPost(new VoteRequest(3), (byte) 1);
        assertTrue(response.isResult());
        assertEquals(1, voteRepository.findByUserIdAndPostId(1, 3).getValue(), "новый лайк не сохранился");
    }
}
