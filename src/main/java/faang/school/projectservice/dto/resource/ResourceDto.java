package faang.school.projectservice.dto.resource;

import faang.school.projectservice.model.ResourceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ResourceDto implements Serializable {
    private Long id;
    private String name;
    private String key;
    private long size;
    private ResourceType resourceType; //TODO: should I map here like that or use String type or create one more Dto for Enum class
}
