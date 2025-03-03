package faang.school.projectservice.service.validator;

import faang.school.projectservice.exception.BusinessException;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StageInvitationValidator {
    private final TeamMemberRepository teamMemberRepository;

    public void validateStatusPendingCheck(StageInvitation stageInvitation) {
        if (stageInvitation.getStatus().equals(StageInvitationStatus.ACCEPTED)) {
            throw new BusinessException(String.format(
                    "Приглашение присоединиться к этапу с id: %s уже принято", stageInvitation.getId())
            );
        }
        if (stageInvitation.getStatus().equals(StageInvitationStatus.REJECTED)) {
            throw new BusinessException(String.format(
                    "Приглашение присоединиться к этапу с id: %s уже отклонено", stageInvitation.getId())
            );
        }
    }

    public void validateEqualsId(long authorId, long invitedId) {
        if (authorId == invitedId) {
            throw new BusinessException(String.format(
                    "Автор приглашения на этап и приглашенный на этот этап не могут быть одним человеком. " +
                            "\n authorId: %d\n invitedId: %d", authorId, invitedId));
        }
    }

    public void validateInvitedMemberTeam(long authorId, long invitedId) {
        TeamMember authorTeamMember = teamMemberRepository.findById(authorId)
                .orElseThrow(() -> new EntityNotFoundException(String.format(
                        "Автор с id: %s не найден в базе данных.", authorId)));

        TeamMember invitedTeamMember = teamMemberRepository.findById(invitedId)
                .orElseThrow(() -> new EntityNotFoundException(String.format(
                        "Приглашенный с id: %s не найден в базе данных.", invitedId)));

        Long authorTeamId = authorTeamMember.getTeam().getId();
        Long invitedTeamId = invitedTeamMember.getTeam().getId();

        if (!authorTeamId.equals(invitedTeamId)) {
            throw new BusinessException(String.format(
                    "Автор приглашения и приглашенный должны быть в одной команде." +
                            "\nauthorTeamId: %d invitedTeamId: %d", authorTeamId, invitedTeamId));
        }
    }
}
