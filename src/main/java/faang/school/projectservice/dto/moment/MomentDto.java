package faang.school.projectservice.dto.moment;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class MomentDto {
    private Long id;
    private String name;
    private LocalDateTime date;
    private String description;
    private List<ProjectDto> projectDtos;
}
