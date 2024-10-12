package faang.school.projectservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Data
@Entity
@Table(name = "moment")
public class Moment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name cannot be blank")
    private String name;

    private String description;

    @NotNull(message = "Date cannot be null")
    private LocalDateTime date;

    @ManyToMany
    @JoinTable(
            name = "moment_resource",
            joinColumns = @JoinColumn(name = "moment_id"),
            inverseJoinColumns = @JoinColumn(name = "resource_id")
    )
    private List<Resource> resource;

    @ManyToMany
    @JoinTable(
            name = "moment_project",
            joinColumns = @JoinColumn(name = "moment_id"),
            inverseJoinColumns = @JoinColumn(name = "project_id")
    )
    private List<Project> projects;


    @NotBlank(message = "Image ID cannot be blank")
    @Column(name = "image_id")
    private String imageId;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @NotNull(message = "Created by cannot be null")
    @Column(name = "created_by")
    private Long createdBy;

    @NotNull(message = "Updated by cannot be null")
    @Column(name = "updated_by")
    private Long updatedBy;

    public List<Long> getPartnerProjectIds() {
        return projects.stream().map(Project::getId).collect(Collectors.toList());
    }
}