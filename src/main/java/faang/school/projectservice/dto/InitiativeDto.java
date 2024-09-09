package faang.school.projectservice.dto;

import faang.school.projectservice.model.stage.Stage;
import lombok.Data;
import java.util.List;

@Data
public class InitiativeDto {
    private Long id;
    private String name;
    private String description;
    private Long projectId;
    private List<Stage> stages;
    private String status;
    private Long curatorId;
}