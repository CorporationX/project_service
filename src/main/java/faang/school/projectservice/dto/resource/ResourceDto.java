package faang.school.projectservice.dto.resource;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResourceDto {
    private String name;
    private Long teamMemberId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
