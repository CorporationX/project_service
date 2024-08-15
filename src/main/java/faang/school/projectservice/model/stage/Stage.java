package faang.school.projectservice.model.stage;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.StageStatus;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
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
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Entity
@Table(name = "project_stage")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Stage {
    @Id
    @Column(name = "project_stage_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long stageId;

    @Column(name = "project_stage_name", nullable = false)
    private String stageName;

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ElementCollection
    @CollectionTable(name = "roles_deficit_mapping",
            joinColumns = @JoinColumn(name = "project_stage_id"))
    @MapKeyColumn(name = "team_role")
    @Column(name = "deficit_count")
    private Map<TeamRole, Integer> rolesDeficit;

    @OneToMany(mappedBy = "stage", cascade = CascadeType.ALL)
    private List<StageRoles> stageRoles;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StageStatus status;

    @OneToMany(cascade = CascadeType.MERGE)
    @JoinColumn(name = "stage_id")
    private List<Task> tasks;

    @ManyToMany
    @JoinTable(name = "project_stage_executors",
            joinColumns = @JoinColumn(name = "stage_id"),
            inverseJoinColumns = @JoinColumn(name = "executor_id"))
    private List<TeamMember> executors;
}