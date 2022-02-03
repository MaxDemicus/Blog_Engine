package main.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.util.List;

@Entity(name = "users")
public class User {

    public User() {
        this.regTime = new Timestamp(System.currentTimeMillis());
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private int id;

    @NotNull
    @Column(name = "is_moderator")
    @Getter
    @Setter
    private byte isModerator;

    @NotNull
    @Column(name = "reg_time")
    private Timestamp regTime;

    @NotNull
    @Getter
    @Setter
    private String name;

    @NotNull
    @Getter
    @Setter
    private String email;

    @NotNull
    @Getter
    @Setter
    private String password;

    @Getter
    @Setter
    private String code;

    @Column(columnDefinition="text")
    @Getter
    @Setter
    private String photo;

    @OneToMany(mappedBy = "moderator")
    @Getter
    private List<Post> moderatedPosts;

    @OneToMany(mappedBy = "user")
    @Getter
    private List<Post> createdPosts;

    @OneToMany(mappedBy = "user")
    @Getter
    private List<PostVote> votes;

    @OneToMany(mappedBy = "user")
    @Getter
    private List<PostComment> comments;
}
