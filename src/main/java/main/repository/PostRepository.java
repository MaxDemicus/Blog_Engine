package main.repository;

import main.model.Post;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {
    String activePostsConditions = " from posts p " +
            "where p.is_active = 1 " +
            "and p.moderation_status = 'ACCEPTED' " +
            "and p.time < now()";

    /**
     * Возвращает активные посты для главной страницы и подразделов
     * @param page объект PаgeRequest, задающий пагинацию и метод сортировки
     * @return список постов
     */
    @Query(value = "select *" + activePostsConditions + " order by ?1", countQuery = "select * from posts", nativeQuery = true)
    List<Post> findActivePosts(PageRequest page);

    /**
     * Возвращает общее количество активных постов
     * @return количество постов
     */
    @Query(value = "select count(*)" + activePostsConditions, countQuery = "select * from posts", nativeQuery = true)
    int countActivePosts();
}
