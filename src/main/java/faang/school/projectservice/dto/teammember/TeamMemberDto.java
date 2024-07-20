package faang.school.projectservice.dto.teammember;

import faang.school.projectservice.dto.StageDto;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class TeamMemberDto {
    private Long id;
    private Long userId;
    private List<StageDto> stages;
}
