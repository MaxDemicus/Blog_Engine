package main.service;

import main.model.Post;
import main.model.PostComment;
import main.model.User;
import main.repository.PostCommentRepository;
import main.repository.PostRepository;
import main.repository.UserRepository;
import main.request.CommentRequest;
import main.response.ResponseWithErrors;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class CommentService {

    private static final int MIN_COMMENT_LENGTH = 10;

    private final PostCommentRepository postCommentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public CommentService(PostCommentRepository postCommentRepository, PostRepository postRepository, UserRepository userRepository) {
        this.postCommentRepository = postCommentRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    /**
     * Добавляет комментарий к посту.
     *
     * @param request Запрос на добавление: родительский комментарий, пост, текст комментария
     * @return номер созданного комментария или текст ошибки, если она произошла
     */
    public ResponseEntity<Object> addComment(CommentRequest request) {
        if (request.getText().length() < MIN_COMMENT_LENGTH) {
            Map<String, String> error = Map.of("text", "Текст комментария не задан или слишком короткий");
            return ResponseEntity.badRequest().body(new ResponseWithErrors(error));
        }
        PostComment parent = null;
        if (request.getParentId() != null) {
            parent = postCommentRepository.findById(request.getParentId()).orElse(null);
            if (parent == null) {
                return ResponseEntity.badRequest().build();
            }
        }
        Post post = postRepository.findById(request.getPostId()).orElse(null);
        if (post == null) {
            return ResponseEntity.badRequest().build();
        }
        String author = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(author);
        PostComment comment = new PostComment(parent, post, user, request.getText());
        postCommentRepository.saveAndFlush(comment);
        return ResponseEntity.ok(Map.of("id", comment.getId()));
    }
}
