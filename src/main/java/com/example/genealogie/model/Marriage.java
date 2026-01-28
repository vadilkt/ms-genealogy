package com.example.genealogie.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.ZonedDateTime;

/**
 * Représente un mariage : un homme (husband) peut avoir plusieurs femmes (wives),
 * une femme (wife) ne peut avoir qu'un seul mari.
 */
@Entity
@Table(name = "marriage")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Marriage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "husband_id", nullable = false)
    @JsonBackReference("husband-marriages")
    private Profile husband;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wife_id", nullable = false, unique = true)
    @JsonBackReference("wife-marriage")
    private Profile wife;

    private ZonedDateTime marriageDate;
    private ZonedDateTime endDate; // divorce ou décès
}
