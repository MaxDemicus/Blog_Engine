package main.service;

import main.model.PostVote;
import main.repository.VoteRepository;
import main.request.VoteRequest;
import main.response.ResponseWithErrors;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class VoteService {

    private final VoteRepository voteRepository;
    private final UserService userService;

    public VoteService(VoteRepository voteRepository, UserService userService) {
        this.voteRepository = voteRepository;
        this.userService = userService;
    }

    /**
     * Cохраняет лайк текущего авторизованного пользователя.
     *
     * @param request json-объект, содержащий номер поста
     * @param value   значение оценки (1 или -1)
     * @return {result: true}, если сохранение успешно, и {result: false}, если произошла ошибка
     */
    @Transactional
    public ResponseWithErrors voteForPost(VoteRequest request, byte value) {
        int userId = userService.getCurrentUser().getId();
        int postId = request.getPostId();
        PostVote vote = voteRepository.findByUserIdAndPostId(userId, postId);
        if (vote != null) {
            if (vote.getValue() == value) {
                return new ResponseWithErrors(false);
            } else {
                vote.setValue(value);
                voteRepository.saveAndFlush(vote);
            }
        } else {
            voteRepository.saveVote(userId, postId, value);
        }
        return new ResponseWithErrors(true);
    }
}
