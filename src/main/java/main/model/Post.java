package main.model;

import lombok.Getter;
import lombok.Setter;
import main.enums.Status;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.util.List;

@Entity(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private int id;

    @NotNull
    @Column(name = "is_active")
    @Getter
    @Setter
    private byte isActive;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "moderation_status", columnDefinition = "enum('NEW','ACCEPTED','DECLINED') default 'NEW'")
    @Getter
    @Setter
    private Status moderationStatus;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "moderator_id")
    @Getter
    @Setter
    private User moderator;

    @NotNull
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    @Getter
    @Setter
    private User user;

    @NotNull
    @Getter
    @Setter
    private Timestamp time;

    @NotNull
    @Getter
    @Setter
    private String title;

    @NotNull
    @Column(columnDefinition = "text")
    @Getter
    @Setter
    private String text;

    @NotNull
    @Column(name = "view_count")
    @Getter
    @Setter
    private int viewCount;

    @OneToMany(mappedBy = "post")
    @Getter
    private List<PostVote> votes;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "tag2post",
            joinColumns = {@JoinColumn(name = "post_id")},
            inverseJoinColumns = {@JoinColumn(name = "tag_id")})
    @Getter
    private List<Tag> tags;

    @OneToMany(mappedBy = "post")
    @Getter
    private List<PostComment> comments;


}
