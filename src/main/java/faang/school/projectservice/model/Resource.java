package faang.school.projectservice.model;

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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
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
