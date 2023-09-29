package faang.school.projectservice.dto.project;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectEventDto {
    private String name;
    private Long ownerId;
    private Long projectId;
}
