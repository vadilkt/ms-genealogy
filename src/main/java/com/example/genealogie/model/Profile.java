package com.example.genealogie.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.antlr.v4.runtime.misc.NotNull;
import org.springframework.lang.Nullable;

import java.time.ZonedDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "profile")
public class Profile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id", unique = true, nullable=false)
    @NotNull
    private User user;
    @NotNull
    private String firstName;
    private String lastName;
    @NotNull
    private Gender gender;
    @NotNull
    private ZonedDateTime dateOfBirth;
    @Nullable
    private ZonedDateTime dateOfDeath;
    @NotNull
    private String residence;
}


