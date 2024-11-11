package faang.school.projectservice.dto.stage;

import faang.school.projectservice.dto.team_member.TeamMemberDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StageDto {
    private Long stageId;
    private String stageName;
    private List<TeamMemberDto> executors;
}
