package main.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import main.enums.PostStatusInDB;
import main.request.PostRequest;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.util.List;

@Entity(name = "posts")
@Data
@NoArgsConstructor
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotNull
    @Column(name = "is_active")
    private byte isActive;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "moderation_status", columnDefinition = "enum('NEW','ACCEPTED','DECLINED') default 'NEW'")
    private PostStatusInDB moderationStatus;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "moderator_id")
    private User moderator;

    @NotNull
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private User user;

    @NotNull
    private Timestamp time;

    @NotNull
    private String title;

    @NotNull
    @Column(columnDefinition = "text")
    private String text;

    @NotNull
    @Column(name = "view_count")
    private int viewCount;

    @OneToMany(mappedBy = "post")
    private List<PostVote> votes;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "tag2post",
            joinColumns = {@JoinColumn(name = "post_id")},
            inverseJoinColumns = {@JoinColumn(name = "tag_id")})
    private List<Tag> tags;

    @OneToMany(mappedBy = "post")
    private List<PostComment> comments;

    public void fillFromRequest (PostRequest request) {
        setIsActive(request.getActive());
        setTitle(request.getTitle());
        setText(request.getText());
        if (request.getTimestamp() < System.currentTimeMillis()) {
            setTime(new Timestamp(System.currentTimeMillis()));
        } else {
            setTime(new Timestamp(request.getTimestamp()));
        }
    }

    @Override
    public String toString() {
        return "Post{" +
                "id=" + id +
                ", isActive=" + isActive +
                ", moderationStatus=" + moderationStatus +
                ", time=" + time +
                ", title='" + title + '\'' +
                ", text='" + text + '\'' +
                '}';
    }
}
