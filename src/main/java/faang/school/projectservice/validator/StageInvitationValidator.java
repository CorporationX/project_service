package faang.school.projectservice.validator;

import faang.school.projectservice.dto.stage_invitation.StageInvitationDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class StageInvitationValidator {
    private final TeamMemberRepository tmRepository;

    public void validateStageInvitation(StageInvitationDto stageInvitationDto) {
        Long authorId = stageInvitationDto.getAuthorId();
        Long invitedId = stageInvitationDto.getInvitedId();
        tmRepository.findById(authorId);
        tmRepository.findById(invitedId);
        if (Objects.equals(invitedId, authorId)) {
            throw new DataValidationException("Invited can't be author!");
        }
    }
}
