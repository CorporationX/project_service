package faang.school.projectservice.dto.project;

import java.time.LocalDateTime;
import java.util.List;

public record ProjectPresentationDto(

        String title,
        LocalDateTime createdDate,
        String ownerName,
        String status,
        String description,
        List<String> completedTasks,
        List<List<ProjectTeamMemberDto>> teams) {
}
