package faang.school.projectservice.validator.stage;

import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.stage.Stage;
import org.springframework.stereotype.Component;

import java.util.List;

import static faang.school.projectservice.model.ProjectStatus.CANCELLED;
import static faang.school.projectservice.model.ProjectStatus.COMPLETED;

@Component
public class StageServiceValidator {
    private final List<ProjectStatus> validStatuses = List.of(COMPLETED, CANCELLED);
    public void validate(Stage stage) {
        if (validStatuses.contains(stage.getProject().getStatus())) {
            throw new RuntimeException("Вы пытаетесь создать этап у отмененного или закрытого проекта");
        }
    }
}
