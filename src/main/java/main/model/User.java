package main.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.util.List;

@Entity(name = "users")
@Getter
@Setter
public class User {

    public User() {
        this.regTime = new Timestamp(System.currentTimeMillis());
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private int id;

    @NotNull
    @Column(name = "is_moderator")
    private byte isModerator;

    @NotNull
    @Column(name = "reg_time")
    private Timestamp regTime;

    @NotNull
    private String name;

    @NotNull
    private String email;

    @NotNull
    private String password;

    private String code;

    @Column(columnDefinition="text")
    private String photo;

    @OneToMany(mappedBy = "moderator")
    private List<Post> moderatedPosts;

    @OneToMany(mappedBy = "user")
    private List<Post> createdPosts;

    @OneToMany(mappedBy = "user")
    private List<PostVote> votes;

    @OneToMany(mappedBy = "user")
    private List<PostComment> comments;
}
