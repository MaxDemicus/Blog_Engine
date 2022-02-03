package main.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

@Entity(name = "post_votes")
public class PostVote {

    public PostVote() {
        this.time = new Timestamp(System.currentTimeMillis());
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private int id;

    @NotNull
    @JoinColumn(name = "user_id")
    @ManyToOne(cascade = CascadeType.ALL)
    @Getter
    @Setter
    private User user;

    @NotNull
    @JoinColumn(name = "post_id")
    @ManyToOne(cascade = CascadeType.ALL)
    @Getter
    @Setter
    private Post post;

    @NotNull
    @Getter
    private Timestamp time;

    @NotNull
    @Getter
    @Setter
    private byte value;
}
