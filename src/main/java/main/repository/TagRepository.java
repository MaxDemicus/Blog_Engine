package main.repository;

import main.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.persistence.Tuple;
import java.util.List;

@Repository
public interface TagRepository extends JpaRepository<Tag, Integer> {

    /**
     * Возвращает список тэгов, начинающихся на строку, заданную в параметре query, и количество публикаций с ними
     *
     * @param query строка для поиска, при отсутствии выводятся все теги
     * @return список кортежей, каждый из которых состоит из двух элементов: имя тега и количество постов с его участием
     */
    @Query(value = "select t.name as tag, count(p.id) as count" + PostRepository.ACTIVE_POSTS_AND_TAGS_CONDITIONS + " and t.name like :query% group by tag", nativeQuery = true)
    List<Tuple> getTags(String query);

    /**
     * Ищет теги по списку имён
     *
     * @param names список имён
     * @return список тегов
     */
    @Query(value = "select * from tags where name in :names", nativeQuery = true)
    List<Tag> findAllByNames(List<String> names);
}
