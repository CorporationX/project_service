package faang.school.projectservice.dto.moment;

import faang.school.projectservice.dto.project.ProjectDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Component
public class MomentDto {
    private Long id;
    private String name;
    private String description;
    private LocalDateTime date;
    private List<ProjectDto> projects;
    private List<Long> userIds;
    private Long createdBy;
    private Long updatedBy;
}
