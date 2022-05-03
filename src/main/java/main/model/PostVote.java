package main.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

@Entity(name = "post_votes")
@Data
public class PostVote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
