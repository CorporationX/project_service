package faang.school.projectservice.model.stage;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TeamMember;
import jakarta.persistence.*;
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

//    @Override
//    public String toString() {
//        return "Stage{" +
//                "stageId=" + stageId +
//                ", stageName='" + stageName + '\'' +
//                ", project=" + project +
//                ", stageRoles=" + stageRoles +
//                ", tasks=" + tasks +
//                ", executors=" + executors +
//                '}';
//    }

    @Override
    public String toString() {
        String projectId;
        if (project == null) {
            projectId = "";
        } else {
            projectId = String.valueOf(project.getId());
        }
        String stageRolesId;
        if (stageRoles == null) {
            stageRolesId = "";
        } else {
            stageRolesId = stageRoles.stream().map(StageRoles::getId).toList().toString();
        }
        String tasksId;
        if (tasks == null) {
            tasksId = "";
        } else {
            tasksId = tasks.stream().map(Task::getId).toList().toString();
        }
        String executorsId;
        if (executors == null) {
            executorsId = "";
        } else {
            executorsId = executors.stream().map(TeamMember::getId).toList().toString();
        }
        return "Stage{" +
                "stageId=" + stageId +
                ", stageName='" + stageName + '\'' +
                ", project=" + projectId +
                ", stageRoles=" + stageRolesId +
                ", tasks=" + tasksId +
                ", executors=" + executorsId +
                '}';
    }
}