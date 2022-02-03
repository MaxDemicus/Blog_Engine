package main.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@Entity(name = "tags")
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private int id;

    @NotNull
    @Getter
    @Setter
    private String name;

    @ManyToMany(mappedBy = "tags")
    @Getter
    private List<Post> posts;
}
