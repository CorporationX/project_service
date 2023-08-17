package faang.school.projectservice.dto;

import faang.school.projectservice.model.Project;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MomentFilterDto {
    private LocalDateTime datePattern;
    private List<Long> projectsPattern;
}
