package faang.school.projectservice.model;

import faang.school.projectservice.model.stage.Stage;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "project")
@Getter
@Setter
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Project project)) return false;
        return Objects.equals(getId(), project.getId()) && Objects.equals(getName(), project.getName()) &&
                Objects.equals(getDescription(), project.getDescription()) &&
                Objects.equals(getStorageSize(), project.getStorageSize()) &&
                Objects.equals(getMaxStorageSize(), project.getMaxStorageSize()) &&
                Objects.equals(getOwnerId(), project.getOwnerId()) &&
                Objects.equals(getParentProject(), project.getParentProject()) &&
                Objects.equals(getChildren(), project.getChildren()) &&
                Objects.equals(getTasks(), project.getTasks()) &&
                Objects.equals(getResources(), project.getResources()) &&
                Objects.equals(getCreatedAt(), project.getCreatedAt()) &&
                Objects.equals(getUpdatedAt(), project.getUpdatedAt()) &&
                getStatus() == project.getStatus() && getVisibility() == project.getVisibility() &&
                Objects.equals(getCoverImageId(), project.getCoverImageId()) &&
                Objects.equals(getTeams(), project.getTeams()) &&
                Objects.equals(getSchedule(), project.getSchedule()) &&
                Objects.equals(getStages(), project.getStages()) &&
                Objects.equals(getVacancies(), project.getVacancies()) &&
                Objects.equals(getMoments(), project.getMoments()) &&
                Objects.equals(getMeets(), project.getMeets());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName(), getDescription(), getStorageSize(), getMaxStorageSize(), getOwnerId(),
                getParentProject(), getChildren(), getTasks(), getResources(), getCreatedAt(), getUpdatedAt(),
                getStatus(), getVisibility(), getCoverImageId(), getTeams(), getSchedule(), getStages(),
                getVacancies(), getMoments(), getMeets());
    }
}
