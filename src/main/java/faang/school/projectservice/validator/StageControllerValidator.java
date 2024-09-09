package faang.school.projectservice.validator;

import faang.school.projectservice.dto.StageDto;
import faang.school.projectservice.exceptions.stage.StageNotHaveNameException;
import faang.school.projectservice.exceptions.stage.StageHaveNoRolesException;
import org.springframework.stereotype.Component;

@Component
public class StageControllerValidator {
    public void validateStageDto(StageDto stageDto) {
        if (stageDto.getStageName() == null) {
            throw new StageNotHaveNameException();
        }

        if (stageDto.getStageRoles().isEmpty()) {
            throw new StageHaveNoRolesException();
        }
    }

    public void validateId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("id is null");
        }
    }
}

