package faang.school.projectservice.model.stage;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TeamMember;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

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

    @OneToMany(mappedBy = "stage", cascade = CascadeType.ALL)
    private List<StageRoles> stageRoles;

    @OneToMany(cascade = CascadeType.MERGE)
    @JoinColumn(name = "stage_id")
    private List<Task> tasks;

    @ManyToMany
    @JoinTable(name = "project_stage_executors",
            joinColumns = @JoinColumn(name = "stage_id"),
            inverseJoinColumns = @JoinColumn(name = "executor_id"))
    private List<TeamMember> executors;
}