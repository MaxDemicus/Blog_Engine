package main.repository;

import main.model.Tag;
import main.response.TagResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TagRepository extends JpaRepository<Tag, Integer> {

    @Query("select new main.response.TagResponse(t.name, t.posts.size) from tags t where t.name like :query%")
    List<TagResponse> getTags(String query);
}
