package main.repository;

import main.model.PostVote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;

@Repository
public interface VoteRepository extends JpaRepository<PostVote, Integer> {

    /**
     * Находит лайк/дизлайк по номеру пользователя и номеру поста
     *
     * @param userId номер пользователя
     * @param postId номер поста
     * @return информация об оценке в виде объекта {@link PostVote}
     */
    PostVote findByUserIdAndPostId(int userId, int postId);

    /**
     * Сохраняет лайк/дизлайк по номеру пользователя и номеру поста
     *
     * @param userId номер пользователя
     * @param postId номер поста
     * @param value 1 (лайк) или -1 (дизлайк)
     */
    @Modifying
    @Query(value = "insert into post_votes (user_id, post_id, value, time) values (?1, ?2, ?3, now())", nativeQuery = true)
    void saveVote(int userId, int postId, byte value);
}
