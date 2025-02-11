package faang.school.projectservice.dto.stage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StageDeleteDto {
    private Long id;
    private Long projectId;
    private List<Long> tasksIds;
    private List<Long> teamMembers;
    private List<Long> executorsId;
}
