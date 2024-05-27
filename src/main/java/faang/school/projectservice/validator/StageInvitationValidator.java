package faang.school.projectservice.validator;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.jpa.StageInvitationJpaRepository;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StageInvitationValidator {
    private final StageInvitationJpaRepository stageInvitationJpaRepository;

    public void createValidationService(Stage stage, TeamMember teamMember) {
        log.info("Validating creation of invitation for stageId: {} and teamMemberId: {}", stage.getStageId(), teamMember.getId());

        if (stageInvitationJpaRepository.existsByInvitedAndStage(teamMember, stage)) {
            log.warn("Validation failed: team member {} has already been invited to stage {}", teamMember.getId(), stage.getStageId());
            throw new DataValidationException("It isn't possible to send an invitation. The user has already been invited");
        }

        log.info("Validation passed for creating invitation for stageId: {} and teamMemberId: {}", stage.getStageId(), teamMember.getId());
    }

    public void acceptOrRejectInvitationService(long invitationId) {
        log.info("Validating acceptance/rejection of invitation with ID: {}", invitationId);
        log.warn("Validation failed: no invitation found with ID: {}", invitationId);
        if (!stageInvitationJpaRepository.existsById(invitationId)) {

            throw new DataValidationException("Couldn't find an invitation with an id: " + invitationId);
        }
        log.info("Validation passed for acceptance/rejection of invitation with ID: {}", invitationId);
    }
}