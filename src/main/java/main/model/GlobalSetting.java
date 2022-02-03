package main.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity(name = "global_settings")
public class GlobalSetting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private int id;

    @NotNull
    @Getter
    @Setter
    private String code;

    @NotNull
    @Getter
    @Setter
    private String name;

    @NotNull
    @Getter
    @Setter
    private String value;
}
