package main.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import main.enums.Role;
import main.request.RegisterRequest;
import main.config.SecurityConfig;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.util.List;

@Entity(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class User {

    public static User from(RegisterRequest request) {
        User user = new User();
        user.isModerator = 0;
        user.regTime = new Timestamp(System.currentTimeMillis());
        user.name = request.getName();
        user.email = request.getEMail();
        user.password = SecurityConfig.encoder.encode(request.getPassword());
        return user;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    public Role getRole(){
        return isModerator == 1 ? Role.MODERATOR : Role.USER;
    }
}
