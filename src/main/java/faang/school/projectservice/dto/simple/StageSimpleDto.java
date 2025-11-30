package faang.school.projectservice.dto.simple;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StageSimpleDto {
    private Long stageId;
    private String stageName;
}