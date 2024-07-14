package faang.school.projectservice.validator.stage;

import org.springframework.stereotype.Component;

@Component
public class StageIdValidator {
    public void validateReplaceId(Long stageId, Long replaceStageId) {
        if (stageId.equals(replaceStageId)) {
            throw new IllegalArgumentException(
                    "replace stage id should not be equals current stage id\n" +
                    "current stage id: \n" +
                    stageId + "\n" +
                    "replace stage id: \n" +
                    replaceStageId);
        }
    }
}
