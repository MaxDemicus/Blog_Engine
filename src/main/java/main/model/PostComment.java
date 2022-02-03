package main.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

@Entity(name = "post_comments")
public class PostComment {

    public PostComment() {
        this.time = new Timestamp(System.currentTimeMillis());
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private int id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "parent_id")
    @Getter
    @Setter
    private PostComment parentComment;

    @NotNull
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "post_id")
    @Getter
    @Setter
    private Post post;

    @NotNull
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    @Getter
    @Setter
    private User user;

    @NotNull
    @Getter
    private Timestamp time;

    @NotNull
    @Column(columnDefinition = "text")
    @Getter
    @Setter
    private String text;
}
