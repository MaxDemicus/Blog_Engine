package main.enums;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.JpaSort;

public enum SortMode {
    early(JpaSort.by(Sort.Direction.ASC, "time")),
    popular(JpaSort.unsafe(Sort.Direction.DESC, "(select count(c.post_id) from post_comments c where p.id=c.post_id)")),
    best(JpaSort.unsafe(Sort.Direction.DESC, "(select count(v.post_id) from post_votes v where p.id=v.post_id and v.value=1)")),
    recent(JpaSort.by(Sort.Direction.DESC, "time"));

    private final Sort sort;

    SortMode(Sort sort) {
        this.sort = sort;
    }

    public Sort getSort() {
        return sort;
    }
}
