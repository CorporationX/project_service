package faang.school.projectservice.service.stage_invitation;

import faang.school.projectservice.dto.stage_invitation.StageInvitationDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.stage_invitation.StageInvitationMapper;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.StageInvitationRepository;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class StageInvitationService {
    private final StageInvitationRepository SIRepository;
    private final StageRepository stageRepository;
    private final TeamMemberRepository TMRepository;
    private final StageInvitationMapper mapper;

    public StageInvitationDto create(StageInvitationDto invitationDto) {
        validate(invitationDto);
        return mapper.toDTO(SIRepository.save(mapper.toModel(invitationDto)));
    }

    private void validate(StageInvitationDto invitationDto) {
        Stage stage = stageRepository.getById(invitationDto.getStageId());
        TMRepository.findById(invitationDto.getInvitedId());
        TMRepository.findById(invitationDto.getAuthorId());
        if (!hasStageExecutor(stage, invitationDto.getAuthorId())) {
            throw new DataValidationException("Author is not executor of stage");
        }
    }

    private boolean hasStageExecutor(Stage stage, long executorId) {
        return stage.getExecutors().stream().anyMatch(executor -> executor.getId() == executorId);
    }
}
