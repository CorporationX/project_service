package faang.school.projectservice.validator;

import faang.school.projectservice.dto.StageDto;
import org.springframework.stereotype.Component;

@Component
public class StageControllerValidator {
    public void validatorStageDto(StageDto stageDto) {
        if (stageDto.getStageName() == null) {
            throw new IllegalArgumentException("stage does not have a name");
        }

        if (stageDto.getRolesMap().isEmpty()) {
            throw new IllegalArgumentException("do not have roles to stage");
        }
    }

    public void validateId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("id is null");
        }
    }
}
