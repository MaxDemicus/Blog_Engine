package main.repository;

import main.model.Post;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.persistence.Tuple;
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
    @Query(value = "select *" + activePostsConditions + " order by :page", nativeQuery = true)
    List<Post> findActivePosts(PageRequest page);

    /**
     * Возвращает посты, соответствующие поисковому запросу.
     * @param page объект PаgeRequest, задающий пагинацию и метод сортировки
     * @param query посиковый запрос
     * @return список постов
     */
    @Query(value = "select *" + activePostsConditions + " and p.text like %:query% order by :page", nativeQuery = true)
    List<Post> findActivePostsBySearch(PageRequest page, String query);

    /**
     * Выводит посты за указанную дату
     * @param page объект PаgeRequest, задающий пагинацию
     * @param date поисковый запрос
     * @return список постов
     */
    @Query(value = "select *" + activePostsConditions + " and date(time) = :date order by :page", nativeQuery = true)
    List<Post> findActivePostsByDate(PageRequest page, String date);

    /**
     * Возвращает общее количество активных постов
     * @return количество постов
     */
    @Query(value = "select count(*)" + activePostsConditions, nativeQuery = true)
    int countActivePosts();

    /**
     * Возвращает количество активных постов, соответствующих поисковому запросу
     * @param query поисковый запрос
     * @return количество постов
     */
    @Query(value = "select count(*)" + activePostsConditions + " and p.text like %:query%", nativeQuery = true)
    int countActivePostsBySearch(String query);

    /**
     * Возвращает количество активных постов за указанную дату
     * @param date поисковый запрос
     * @return количество постов
     */
    @Query(value = "select count(*)" + activePostsConditions + " and date(time) = :date", nativeQuery = true)
    int countActivePostsByDate(String date);

    /**
     * Возвращает года, за которые есть хотя бы одна активная публикация.
     * @return Список годов в порядке возрастания
     */
    @Query(value = "select year(time) as year" + activePostsConditions + " group by year order by year", nativeQuery = true)
    List<Integer> getYears();

    /**
     * Возвращает количества публикаций на каждую дату переданного в параметре year года
     * @param year год
     * @return список кортежей (класс {@link Tuple}) формата 'дата - количеств публикаций'
     */
    @Query(value = "select date(time) as date, count(*) as count" + activePostsConditions + " and year(time) = :year group by date(time)", nativeQuery = true)
    List<Tuple> getCalendar(String year);
}
