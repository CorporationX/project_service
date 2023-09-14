package faang.school.projectservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;

@Data
@Entity
@Builder
@Table(name = "internship")
public class Offer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "vacancy_id")
    @NotNull
    private Vacancy vacancy;

    @ManyToOne
    @JoinColumn(name = "candidate_id")
    @NotNull
    private Candidate candidate;

    @CreatedBy
    @NotNull
    private Long createdBy;

    @ManyToOne
    @JoinColumn(name = "team_id")
    @NotNull
    private Team team;

    @Enumerated(EnumType.STRING)
    @NotNull
    private OfferStatus status;
}
