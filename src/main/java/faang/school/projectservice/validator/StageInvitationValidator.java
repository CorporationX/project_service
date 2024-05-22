package faang.school.projectservice.validator;

import faang.school.projectservice.dto.client.StageInvitationDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.filter.InvitationFilterDto;
import faang.school.projectservice.jpa.StageInvitationJpaRepository;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StageInvitationValidator {
    private final StageInvitationJpaRepository stageInvitationJpaRepository;

    public void createValidationController(StageInvitationDto stageInvitationDto) {
        if (stageInvitationDto.getStageId() == null) {
            throw new DataValidationException("The stage id can't be empty");
        }
        if (stageInvitationDto.getInvitedId() == null) {
            throw new DataValidationException("The invited id can't be empty");
        }
        if (stageInvitationDto.getAuthorId() == null) {
            throw new DataValidationException("The author id can't be empty");
        }
    }

    public void acceptInvitationValidationController(StageInvitationDto stageInvitationDto) {
        if (stageInvitationDto.getId() == null) {
            throw new DataValidationException("The invitation id can't be empty");
        }
    }

    public void rejectInvitationValidationController(StageInvitationDto stageInvitationDto) {
        if (stageInvitationDto.getExplanation() == null || stageInvitationDto.getExplanation().isBlank()) {
            throw new DataValidationException("The exception can't be empty");
        }
        if (stageInvitationDto.getId() == null) {
            throw new DataValidationException("The invitation id can't be empty");
        }
    }

    public void showAllInvitationForMemberValidationController(Long userId, InvitationFilterDto invitationFilterDto) {
        if (userId == null) {
            throw new DataValidationException("The userId can't be empty");
        }
        if (invitationFilterDto == null) {
            throw new DataValidationException("Передан пустой фильтр");
        }
    }

    public void createValidationService(Stage stage, TeamMember teamMember) {
        if (stageInvitationJpaRepository.existsByInvitedAndStage(teamMember, stage)) {
            throw new DataValidationException("It isn't possible to send an invitation. The user has already been invited");
        }
    }

    public void acceptOrRejectInvitationService(StageInvitationDto stageInvitationDto) {
        if (!stageInvitationJpaRepository.existsById(stageInvitationDto.getId())) {
            throw new DataValidationException("Couldn't find an invitation with an id: " + stageInvitationDto.getId());
        }
    }
}