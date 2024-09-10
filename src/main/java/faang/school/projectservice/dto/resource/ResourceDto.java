package faang.school.projectservice.dto.resource;

import faang.school.projectservice.model.ResourceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ResourceDto {
    private Long id;
    private String name;
    private String key;
    private BigInteger size;
    private ResourceType resourceType;
    private LocalDateTime updatedAt;
}
