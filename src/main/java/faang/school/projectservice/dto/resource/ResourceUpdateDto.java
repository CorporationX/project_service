package faang.school.projectservice.dto.resource;

import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.ResourceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class ResourceUpdateDto extends ResourceDto{
    private String name;
    private String key;
    private Long size;
    private ResourceType type;
    private ResourceStatus status;
    private LocalDateTime updatedAt;
    private Long updatedBy;
    private Long project;
}
