package faang.school.projectservice.dto.resource;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigInteger;

@Data
@Builder
@AllArgsConstructor
public class ResourceDto {
    private String name;
    private String key;
    private BigInteger size;

    /*
    @Enumerated(EnumType.STRING) //IMAGE
    private ResourceType type;

    @Enumerated(EnumType.STRING)
    private ResourceStatus status; // ACTIVE

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
     */
}
