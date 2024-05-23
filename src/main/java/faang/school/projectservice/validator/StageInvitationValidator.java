package faang.school.projectservice.validator;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.jpa.StageInvitationJpaRepository;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StageInvitationValidator {
    private final StageInvitationJpaRepository stageInvitationJpaRepository;

    public void createValidationService(Stage stage, TeamMember teamMember) {
        if (stageInvitationJpaRepository.existsByInvitedAndStage(teamMember, stage)) {
            throw new DataValidationException("It isn't possible to send an invitation. The user has already been invited");
        }
    }

    public void acceptOrRejectInvitationService(long invitationId) {
        if (!stageInvitationJpaRepository.existsById(invitationId)) {
            throw new DataValidationException("Couldn't find an invitation with an id: " + invitationId);
        }
    }
}