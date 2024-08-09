package faang.school.projectservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "project_resource")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Resource {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    private String key;

    private BigInteger size;

    @ElementCollection(targetClass = TeamRole.class)
    @CollectionTable(name = "resource_allowed_roles",
            joinColumns = @JoinColumn(name = "resource_id"))
    @Column(name = "role_id")
    @Enumerated(EnumType.STRING)
    private List<TeamRole> allowedRoles;

    @Enumerated(EnumType.STRING)
    private ResourceType type;

    @Enumerated(EnumType.STRING)
    private ResourceStatus status;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private TeamMember createdBy;

    @ManyToOne
    @JoinColumn(name = "updated_by")
    private TeamMember updatedBy;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;
}
