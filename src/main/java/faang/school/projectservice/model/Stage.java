package faang.school.projectservice.model;

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

    @Column(name = "project_stage_name")
    private String stageName;

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @OneToMany(mappedBy = "stage", cascade = CascadeType.REMOVE)
    private List<StageRoles> stageRoles;

    @OneToMany(mappedBy = "stage", cascade = CascadeType.ALL)
    private List<Task> tasks;
}