package faang.school.projectservice.validator;

import faang.school.projectservice.model.stage.Stage;
import org.springframework.stereotype.Component;

@Component
public class StageValidator {

    public boolean isExecutorExist(Stage stage, String role) {
        return stage.getExecutors().stream()
                .flatMap(teamMember -> teamMember.getRoles().stream())
                .anyMatch(teamRole -> teamRole.toString().equalsIgnoreCase(role));
    }
}
