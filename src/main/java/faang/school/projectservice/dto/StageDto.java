package faang.school.projectservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class StageDto {
    private Long stageId;
    private String stageName;
    private String projectName;
}
