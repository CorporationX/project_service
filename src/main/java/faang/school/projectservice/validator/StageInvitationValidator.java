package faang.school.projectservice.validator;

import faang.school.projectservice.dto.stage_invintation.StageInvitationDto;
import faang.school.projectservice.exception.DataValidationStageInvitationException;
import faang.school.projectservice.repository.StageInvitationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StageInvitationValidator {
    private final StageInvitationRepository stageInvitationRepository;

    public void validateId(Long id) {
        if (id == null) {
            throw new DataValidationStageInvitationException("Id is null.");
        }
        if (id <= 0) {
            throw new DataValidationStageInvitationException("Id ID cannot be 0 or less than 0");
        }
    }

    public void validateDescription(String description) {
        if (description == null) {
            throw new DataValidationStageInvitationException("Description is null");
        }
        if (description.trim().isEmpty()) {
            throw new DataValidationStageInvitationException("Description cannot is empty.");
        }
    }

    public void validateAll(StageInvitationDto stageInvitationDto) {
        validateId(stageInvitationDto.getId());
        validateDescription(stageInvitationDto.getDescription());
    }
}