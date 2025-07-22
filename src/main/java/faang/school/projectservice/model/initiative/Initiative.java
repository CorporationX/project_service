package faang.school.projectservice.model.initiative;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "initiative")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Initiative {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 64)
    private String name;

    @Column(name = "description", nullable = false, length = 4096)
    private String description;

    @ManyToOne
    @JoinColumn(name = "curator_id",  nullable = false)
    private TeamMember curator;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private InitiativeStatus status;

    @ManyToMany
    @JoinTable(
            name = "initiative_project_stages",
            joinColumns = @JoinColumn(name = "initiative_id"),
            inverseJoinColumns = @JoinColumn(name = "project_stage_id")
    )
    private List<Stage> stages;

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToMany
    @JoinTable(
            name = "initiative_project",
            joinColumns = @JoinColumn(name = "initiative_id"),
            inverseJoinColumns = @JoinColumn(name = "project_id")
    )
    private List<Project> sharingProjects;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}