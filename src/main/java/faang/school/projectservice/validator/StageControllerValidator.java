package faang.school.projectservice.validator;

import faang.school.projectservice.dto.client.StageDto;
import org.springframework.stereotype.Component;

@Component
public class StageControllerValidator {
    public void validatorStageDto(StageDto stageDto) {
        if (stageDto.getStageName() == null) {
            throw new IllegalArgumentException("stage does not have a name");
        }
    }

    public void validateId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("project id id null");
        }
    }
}
