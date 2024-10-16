package com.example.genealogie.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import javax.management.relation.RelationType;

@Getter
@Setter
@Entity
public class RelationFamille {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "profile_id")
    private Profile profile;

    @ManyToOne
    @JoinColumn(name = "relative_id")
    private Profile relative;

    @Enumerated(EnumType.STRING)
    private RelationType relationType;

    public enum RelationType{
        PARENT, GRANDPARENT, CHILD, SPOUSE
    }
}
