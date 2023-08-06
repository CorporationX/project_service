package faang.school.projectservice.validate;

import faang.school.projectservice.dto.stageInvitation.StageInvitationDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StageInvitationValidator {
    private TeamMemberRepository teamMemberRepository;
    private StageRepository stageRepository;

    public void validateStageInvitationDto(StageInvitationDto stageInvitationDto) {
        if (stageInvitationDto == null) {
            throw new DataValidationException("StageInvitationDto is null");
        }
        if (stageInvitationDto.getAuthorId() == null) {
            throw new DataValidationException("AuthorId is null");
        }
        if (stageInvitationDto.getInvitedId() == null) {
            throw new DataValidationException("InvitedId is null");
        }
        if (stageInvitationDto.getStageId() == null) {
            throw new DataValidationException("StageId is null");
        }
    }

    public void validateExecutors(StageInvitationDto stageInvitationDto) {
        Stage stage = stageRepository.getById(stageInvitationDto.getStageId());
        teamMemberRepository.findById(stageInvitationDto.getInvitedId());
        teamMemberRepository.findById(stageInvitationDto.getAuthorId());
        if (!hasStageExecutors(stage, stageInvitationDto.getAuthorId())) {
            throw new DataValidationException("AuthorId doesn't executor of stage");
        }
    }

    private boolean hasStageExecutors(Stage stage, long id) {
        return stage.getExecutors().stream()
                .anyMatch(executor -> executor.getId() == id);
    }
}
