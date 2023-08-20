package faang.school.projectservice.dto.resource;

import faang.school.projectservice.model.resource.ResourceStatus;
import faang.school.projectservice.model.resource.ResourceType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class ResourceDto {
    private String name;
    private String key;
    private BigInteger size;
    private ResourceType type;
    private ResourceStatus status;
    private Long projectId;
    private Long createdById;
    private LocalDateTime createdAt;
}
