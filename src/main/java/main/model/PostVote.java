package main.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

@Entity(name = "post_votes")
@Getter
@Setter
public class PostVote {

    public PostVote() {
        this.time = new Timestamp(System.currentTimeMillis());
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private int id;

    @NotNull
    @JoinColumn(name = "user_id")
    @ManyToOne(cascade = CascadeType.ALL)
    private User user;

    @NotNull
    @JoinColumn(name = "post_id")
    @ManyToOne(cascade = CascadeType.ALL)
    private Post post;

    @NotNull
    private Timestamp time;

    @NotNull
    private byte value;
}
