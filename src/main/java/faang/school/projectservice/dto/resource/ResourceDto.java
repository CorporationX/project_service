package faang.school.projectservice.dto.resource;

import faang.school.projectservice.model.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResourceDto {
    private Long id;
    private String name;
    private ResourceStatus status;
    private Long updatedById;
    private Long createdById;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
