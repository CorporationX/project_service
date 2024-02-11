package faang.school.projectservice.validator;

import faang.school.projectservice.dto.stage_invitation.StageInvitationDto;
import faang.school.projectservice.dto.stage_invitation.StageInvitationFilterDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class StageInvitationValidator {
    private final TeamMemberRepository tmRepository;

    public void validateDto(StageInvitationDto stageInvitationDto) {
        if (stageInvitationDto == null) {
            throw new DataValidationException("StageInvitation is null!");
        }
    }

    public void validateStageInvitation(StageInvitationDto stageInvitationDto) {
        Long authorId = stageInvitationDto.getAuthorId();
        Long invitedId = stageInvitationDto.getInvitedId();
        tmRepository.findById(authorId);
        tmRepository.findById(invitedId);
        if (Objects.equals(invitedId, authorId)) {
            throw new DataValidationException("Invited can't be author!");
        }
    }

    public void validateInvitationId(long stageInvitationId) {
        if (stageInvitationId == 0) {
            throw new DataValidationException("StageInvitation ID is 0!");
        }
    }

    public void validateReject(long stageInvitationId, String message) {
        validateInvitationId(stageInvitationId);
        if (message == null || message.isBlank()) {
            throw new DataValidationException("Rejection must contains message!");
        }
    }

    public void validateFilter(StageInvitationFilterDto filterDto) {
        if (filterDto == null) {
            throw new DataValidationException("Filter is null!");
        }
    }
}
