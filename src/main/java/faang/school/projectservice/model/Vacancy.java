package faang.school.projectservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "vacancy")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Vacancy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    private String description;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    @OneToMany(mappedBy = "vacancy")
    private List<Candidate> candidates;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @CreatedBy
    private Long createdBy;

    @LastModifiedBy
    private Long updatedBy;

    @Enumerated(EnumType.STRING)
    @NotNull
    private VacancyStatus status;

    private Double salary;

    @Enumerated(EnumType.STRING)
    private WorkSchedule workSchedule;

    @ElementCollection
    @CollectionTable(name = "vacancy_skills", joinColumns = @JoinColumn(name = "vacancy_id"))
    @Column(name = "skill_id")
    private List<Long> requiredSkillIds;

    @Column(name = "position", nullable = false)
    private String position;

    @Column(name = "vacancy_places", nullable = false)
    private Long vacancyPlaces;
}
