package faang.school.projectservice.dto.resource;

import lombok.Builder;
import lombok.Data;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Data
@Builder
public class ResourceDto {

    private Long id;
    private String name;
    private String key;
    private BigInteger size;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long projectId;
}
