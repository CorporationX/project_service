package faang.school.projectservice.dto.project;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectPresentationDto {
    private String title;
    private LocalDateTime createdDate;
    private String ownerName;
    private String status;
    private String description;
    private List<String> completedTasks;
    private List<List<TeamMemberDto>> teams;
}
