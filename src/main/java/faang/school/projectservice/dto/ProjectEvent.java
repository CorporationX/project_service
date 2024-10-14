package faang.school.projectservice.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class ProjectEvent {
    private Long authorId;
    private Long projectId;
}
