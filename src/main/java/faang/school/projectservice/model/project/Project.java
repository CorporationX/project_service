package faang.school.projectservice.model.project;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import faang.school.projectservice.model.Meet;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.resource.Resource;
import faang.school.projectservice.model.Schedule;
import faang.school.projectservice.model.task.Task;
import faang.school.projectservice.model.team.Team;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.stage.Stage;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "project")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", length = 128, nullable = false)
    private String name;

    @Column(name = "description", length = 4096)
    private String description;

    @Column(name = "storage_size")
    private BigInteger storageSize;

    @Column(name = "max_storage_size")
    private BigInteger maxStorageSize;

    @Column(name = "owner_id")
    private Long ownerId;

    @ManyToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name = "parent_project_id")
    private Project parentProject;

    @OneToMany(mappedBy = "parentProject", fetch = FetchType.EAGER)
    private List<Project> children;

    @OneToMany(mappedBy = "project")
    private List<Task> tasks;

    @OneToMany(mappedBy = "project")
    private List<Resource> resources;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProjectStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProjectVisibility visibility;

    @Column(name = "cover_image_id")
    private String coverImageId;

    @OneToMany(mappedBy = "project")
    private List<Team> teams;

    @OneToOne(mappedBy = "project")
    private Schedule schedule;

    @OneToMany(mappedBy = "project")
    private List<Stage> stages;

    @OneToMany(mappedBy = "project")
    private List<Vacancy> vacancies;

    @ManyToMany(mappedBy = "projects")
    private List<Moment> moments;

    @OneToMany(mappedBy = "project")
    private List<Meet> meets;

    @Column(name = "jira_project_key")
    private String jiraProjectKey;

    public void setPrivateVisibility() {
        setVisibility(ProjectVisibility.PRIVATE);

        if (children != null) {
            children.forEach(Project::setPrivateVisibility);
        }
    }

    public void addMoment(Moment moment) {
        if (moments == null) {
            moments = new ArrayList<>();
        }
        moments.add(moment);
    }

    public void addChildren(Project subProject) {
        if (children == null) {
            children = new ArrayList<>();
        }
        children.add(subProject);
    }


    public boolean hasChildren() {
        return children != null && !children.isEmpty();
    }

    public boolean isProjectInactive() {
        return isCompleted() || isOnHold() || isCancelled();
    }

    public boolean isCompleted() {
        return status.equals(ProjectStatus.COMPLETED);
    }

    public boolean isNotCompleted() {
        return !status.equals(ProjectStatus.COMPLETED);
    }

    public boolean isOnHold() {
        return status.equals(ProjectStatus.ON_HOLD);
    }

    public boolean isCancelled() {
        return status.equals(ProjectStatus.CANCELLED);
    }

    public boolean isPrivate() {
        return visibility.equals(ProjectVisibility.PRIVATE);
    }

    public void addResource(Resource resource) {
        resources.add(resource);
    }
}
