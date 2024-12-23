package faang.school.projectservice.dto.resource;

import faang.school.projectservice.model.ResourceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Builder
@AllArgsConstructor
@Data
public class ResourceDto implements Serializable {
    private Long id;
    private String name;
    private String key;
    private long size;
    private ResourceType resourceType;
}
