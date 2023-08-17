package faang.school.projectservice.dto.stages;

import faang.school.projectservice.model.Project;
import lombok.Data;

import java.util.List;

@Data
public class StageDto {
    private Long stageId;
    private String stageName;
    private Project project;
    private List<Long> executors;
}
