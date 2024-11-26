package faang.school.projectservice.validator.stage_invitation;

import faang.school.projectservice.exeption.DataValidationException;
import faang.school.projectservice.repository.StageInvitationRepository;
import faang.school.projectservice.service.team_member.TeamMemberService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@RequiredArgsConstructor
public class StageInvitationValidator {
    private TeamMemberService teamMemberService;
    private StageInvitationRepository stageInvitationRepository;

    public void checkWhetherThisRequestExists(Long stageInvitationId) {
        boolean existedStageInvitation = stageInvitationRepository.stageInvitationExist(stageInvitationId);
        if (existedStageInvitation) {
            throw new DataValidationException("This invitation already exists");
        }
    }

    public void checkTheExistenceOfTheInvitee(Long invitedId) {
        boolean existedTeamMember = teamMemberService.existedTeamMember(invitedId);
        if (!existedTeamMember) {
            throw new DataValidationException("This team member does not exist");
        }
    }

    public void checkTheReasonForTheFailure(String rejection) {
        if (rejection == null) {
            throw new DataValidationException("The reason for the refusal must exist");
        }
        if (rejection.isBlank()) {
            throw new DataValidationException("The reason for the refusal must be indicated");
        }
    }
}
