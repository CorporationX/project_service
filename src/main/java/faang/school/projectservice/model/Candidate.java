package faang.school.projectservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "candidate")
public class Candidate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    private Long userId;
    @NotNull
    private String username;
    private String resumeDocKey;
    private String coverLetter;
    @Enumerated(EnumType.STRING)
    private CandidateStatus candidateStatus;

    @Column(name = "is_accepted", columnDefinition = "boolean default false")
    private Boolean isAccepted;

    @ManyToOne
    @JoinColumn(name = "vacancy_id")
    private Vacancy vacancy;
}
