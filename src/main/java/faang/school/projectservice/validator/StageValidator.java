package faang.school.projectservice.validator;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.stage.Stage;
import org.springframework.stereotype.Component;

@Component
public class StageValidator {

    public void isCompletedOrCancelled(Stage stageFromRepository) {
        if (stageFromRepository.getStatus().toString().equalsIgnoreCase("CANCELLED")
                || stageFromRepository.getStatus().toString().equalsIgnoreCase("COMPLETED")) {
            throw new DataValidationException("Stage is completed or cancelled");
        }
        if (stageFromRepository.getProject().getStatus().toString().equalsIgnoreCase("CANCELLED")
                || stageFromRepository.getProject().getStatus().toString().equalsIgnoreCase("COMPLETED")) {
            throw new DataValidationException("Project is completed or cancelled");
        }
    }
}