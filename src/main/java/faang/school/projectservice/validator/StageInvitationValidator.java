package faang.school.projectservice.validator;

import faang.school.projectservice.dto.stage_invintation.StageInvitationDto;
import faang.school.projectservice.exception.DataValidationException;
import org.springframework.stereotype.Component;

@Component
public class StageInvitationValidator {

    public void validateId(Long id) {
        if (id == null) {
            throw new DataValidationException("Id is null.");
        }
        if (id <= 0) {
            throw new DataValidationException("Id ID cannot be 0 or less than 0");
        }
    }

    public void validateDescription(String description) {
        if (description == null) {
            throw new DataValidationException("Description is null");
        }
        if (description.trim().isEmpty()) {
            throw new DataValidationException("Description cannot is empty.");
        }
    }

    public void validateAll(StageInvitationDto stageInvitationDto) {
        validateId(stageInvitationDto.getId());
        validateDescription(stageInvitationDto.getDescription());
    }
}