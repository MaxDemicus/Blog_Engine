package main.repository;

import main.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.persistence.Tuple;
import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {
    String ACTIVE_POSTS_CONDITIONS = " from posts p " +
            "where p.is_active = 1 and p.moderation_status = 'ACCEPTED' and p.time < now()";
    String ACTIVE_POSTS_AND_TAGS_CONDITIONS = " from posts p inner join tag2post tp on p.id=tp.post_id inner join tags t on tp.tag_id=t.id " +
            "where p.is_active = 1 and p.moderation_status = 'ACCEPTED' and p.time < now()";
    String GET_STATISTICS_QUERY_PART1 = "select count(*) as PostsCount, sum(likes) as LikesCount, sum(dislikes) as DislikesCount, sum(view_count) as ViewsCount, min(time) as FirstPublication " +
            "from (select p.time, view_count, count(case when value = 1 then 1 else null end) as likes, count(case when value = -1 then 1 else null end) as dislikes " +
            "from posts as p " +
            "left join post_votes v on p.id=v.post_id " +
            "where p.is_active = 1 and p.moderation_status = 'ACCEPTED' and p.time < now() ";
    String GET_STATISTICS_QUERY_PART2 = "group by p.id) as post_data";

    /**
     * Возвращает активные посты для главной страницы и подразделов
     *
     * @param page объект PаgeRequest, задающий пагинацию и метод сортировки
     * @return список постов
     */
    @Query(value = "select *" + ACTIVE_POSTS_CONDITIONS, nativeQuery = true)
    Page<Post> findActivePosts(PageRequest page);

    /**
     * Возвращает посты, соответствующие поисковому запросу.
     *
     * @param page  объект PаgeRequest, задающий пагинацию и метод сортировки
     * @param query посиковый запрос
     * @return список постов
     */
    @Query(value = "select *" + ACTIVE_POSTS_CONDITIONS + " and p.text like %?1%", nativeQuery = true)
    Page<Post> findActivePostsBySearch(String query, PageRequest page);

    /**
     * Выводит посты за указанную дату
     *
     * @param page объект PаgeRequest, задающий пагинацию
     * @param date поисковый запрос
     * @return список постов
     */
    @Query(value = "select *" + ACTIVE_POSTS_CONDITIONS + " and date(time) = ?1", nativeQuery = true)
    Page<Post> findActivePostsByDate(String date, PageRequest page);

    /**
     * Выводит посты, привязанные к указанному тегу
     *
     * @param page объект PаgeRequest, задающий пагинацию
     * @param tag  запрошенный тег
     * @return список постов
     */
    @Query(value = "select p.*" + ACTIVE_POSTS_AND_TAGS_CONDITIONS + " and t.name = ?1", nativeQuery = true)
    Page<Post> findActivePostsByTag(String tag, PageRequest page);

    /**
     * Выводит список неактивных постов, которые создал пользователь
     *
     * @param userID номер пользователя
     * @param page объект PаgeRequest, задающий пагинацию
     * @return список постов
     */
    @Query(value = "select * from posts p where user_id = ?1 and is_active = 0", nativeQuery = true)
    Page<Post> findInactivePostsByUser(int userID, PageRequest page);

    /**
     * Выводит список новых постов, для которых требуется модерация
     * @param page объект PаgeRequest, задающий пагинацию
     * @return список постов
     */
    @Query(value = "select * from posts p where moderation_status = 'NEW' and is_active = 1", nativeQuery = true)
    Page<Post> findModeration(PageRequest page);

    /**
     * Выводит список активных постов, которые создал пользователь, с определённым статусом модерации
     *
     * @param userID номер пользователя
     * @param status статус модерации: "NEW", "ACCEPTED" или "DECLINED"
     * @param page объект PаgeRequest, задающий пагинацию
     * @return список постов
     */
    @Query(value = "select * from posts p where user_id = ?1 and is_active = 1 and moderation_status = ?2", nativeQuery = true)
    Page<Post> findPostsByUserAndStatus(int userID, String status, PageRequest page);

    /**
     * Выводит список активных постов, которые данный модератор отклонил или утвердил
     *
     * @param moderatorID номер пользователя
     * @param status статус модерации: "ACCEPTED" или "DECLINED"
     * @param page объект PаgeRequest, задающий пагинацию
     * @return список постов
     */
    @Query(value = "select * from posts p where moderator_id = ?1 and is_active = 1 and moderation_status = ?2", nativeQuery = true)
    Page<Post> findPostsByModeratorAndStatus(int moderatorID, String status, PageRequest page);

    /**
     * Возвращает количество постов, требующих модерации
     *
     * @return количество постов
     */
    @Query(value = "select count(*) from posts where moderation_status = 'NEW' and moderator_id is null and is_active = 1", nativeQuery = true)
    int countModeration();

    /**
     * Возвращает года, за которые есть хотя бы одна активная публикация.
     *
     * @return Список годов в порядке возрастания
     */
    @Query(value = "select year(time) as year" + ACTIVE_POSTS_CONDITIONS + " group by year order by year", nativeQuery = true)
    List<Integer> getYears();

    /**
     * Возвращает количества публикаций на каждую дату переданного в параметре year года
     *
     * @param year год
     * @return список кортежей (класс {@link Tuple}) формата 'дата - количеств публикаций'
     */
    @Query(value = "select date(time) as date, count(*) as count" + ACTIVE_POSTS_CONDITIONS + " and year(time) = :year group by date(time)", nativeQuery = true)
    List<Tuple> getCalendar(String year);

    /**
     * Возвращает информацию о постах, созданных данным пользователем и доступных для чтения:
     * количество публикаций, количество лайков, дизлайков и просмотров, дата самой первой публикации
     *
     * @param userId номер пользователя
     * @return список постов
     */
    @Query(value = GET_STATISTICS_QUERY_PART1 + "and p.user_id = ?1 " + GET_STATISTICS_QUERY_PART2, nativeQuery = true)
    Tuple findStatisticsByUser(int userId);

}
