package main.repository;

import main.model.Tag;
import main.response.TagResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TagRepository extends JpaRepository<Tag, Integer> {

    /**
     * Возвращает список тэгов, начинающихся на строку, заданную в параметре query, и количество публикаций с ними
     * @param query строка для поиска, при отсутствии выводятся все теги
     * @return список объектов {@link TagResponse}: имя тега и количество постов с его участием в качестве первоначального веса
     */
    @Query("select new main.response.TagResponse(t.name, t.posts.size) from tags t where t.name like :query%")
    List<TagResponse> getTags(String query);
}
