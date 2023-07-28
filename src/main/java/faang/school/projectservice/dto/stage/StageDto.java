package faang.school.projectservice.dto.stage;

import faang.school.projectservice.model.Project;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class StageDto {
    private Long stageId;
    private String stageName;
    private Project project;
}
