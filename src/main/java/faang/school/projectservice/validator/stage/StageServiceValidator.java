package faang.school.projectservice.validator.stage;

import faang.school.projectservice.model.stage.Stage;
import org.springframework.stereotype.Component;

import java.util.List;

import static faang.school.projectservice.model.ProjectStatus.CANCELLED;
import static faang.school.projectservice.model.ProjectStatus.COMPLETED;

@Component
public class StageServiceValidator {
    public void validate(Stage stage) {
        if (List.of(COMPLETED, CANCELLED).contains(stage.getProject().getStatus())) {
            throw new RuntimeException("Вы пытаетесь создать этап у отмененного или закрытого проекта");
        }
    }
}
