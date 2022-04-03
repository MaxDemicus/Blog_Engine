package main.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

@Entity(name = "post_comments")
@Data
@NoArgsConstructor
public class PostComment {

    public PostComment(PostComment parentComment, Post post, User user, String text) {
        this.parentComment = parentComment;
        this.post = post;
        this.user = user;
        this.text = text;
        this.time = new Timestamp(System.currentTimeMillis());
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "parent_id")
    private PostComment parentComment;

    @NotNull
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "post_id")
    private Post post;

    @NotNull
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private User user;

    @NotNull
    private Timestamp time;

    @NotNull
    @Column(columnDefinition = "text")
    private String text;
}
