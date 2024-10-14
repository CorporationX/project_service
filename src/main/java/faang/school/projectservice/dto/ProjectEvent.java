package faang.school.projectservice.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class ProjectEvent {
    Long authorId;
    Long projectId;
}
