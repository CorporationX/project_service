package faang.school.projectservice.dto.resource;

import com.fasterxml.jackson.annotation.JsonFormat;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.ResourceType;
import faang.school.projectservice.model.TeamRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class ResourceDto {
    private Long id;
    private String name;
    private String key;
    private BigInteger size;
    private Set<TeamRole> allowedRoles;
    private ResourceType type;
    private ResourceStatus status;
    private Long createdById;
    private Long updatedById;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
    private Long projectId;
}
