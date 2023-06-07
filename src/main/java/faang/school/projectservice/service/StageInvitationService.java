package faang.school.projectservice.service;

import faang.school.projectservice.dto.stage_invitation.AcceptInviteDto;
import faang.school.projectservice.dto.stage_invitation.RejectInviteDto;
import faang.school.projectservice.dto.stage_invitation.StageInvitationDto;
import faang.school.projectservice.dto.stage_invitation.StageInvitationFilterDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.StageInvitationMapper;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.repository.StageInvitationRepository;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StageInvitationService {
    private final StageInvitationRepository repository;
    private final StageRepository stageRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final StageInvitationMapper mapper;

    @Transactional
    public StageInvitationDto sendInvite(StageInvitationDto request) {
        Stage stage = stageRepository.getById(request.getStageId());
        validateSendInvite(request, stage);
        StageInvitation entity = mapper.toEntity(request);
        entity.setStatus(StageInvitationStatus.AWAIT_A_RESPONSE);
        return mapper.toDto(repository.save(entity));
    }

    @Transactional
    public StageInvitationDto acceptInvite(AcceptInviteDto request) {
        StageInvitation entity = repository.findById(request.getId());
        TeamMember executor = teamMemberRepository.findById(request.getTeamMemberId());
        validateAcceptInvite(entity, executor);
        entity.setStatus(StageInvitationStatus.ACCEPTED);
        entity.getStage().getExecutors().add(executor);
        repository.save(entity);
        return mapper.toDto(entity);
    }

    @Transactional
    public StageInvitationDto rejectInvite(RejectInviteDto request) {
        StageInvitation entity = repository.findById(request.getId());
        entity.setDescription(request.getDescription());
        entity.setStatus(StageInvitationStatus.REJECTED);
        return mapper.toDto(repository.save(entity));
    }

    @Transactional(readOnly = true)
    public List<StageInvitationDto> findByFilter(StageInvitationFilterDto filter) {
        return repository.findByFilter(filter).stream().map(mapper::toDto).toList();
    }

    private void validateSendInvite(StageInvitationDto stageInvitationDto, Stage stage) {
        if (repository.isInvitationExist(stageInvitationDto, stage)) {
            throw new DataValidationException("Such an invitation already exists");
        }
    }

    private void validateAcceptInvite(StageInvitation entity, TeamMember executor) {
        if (entity.getStatus() == StageInvitationStatus.ACCEPTED) {
            throw new DataValidationException("Invite already accepted");
        }
        if (!entity.getInvited().equals(executor)) {
            throw new DataValidationException(String.format("the team member id is specified incorrectly: " +
                    "%s, expected : %s", executor.getId(), entity.getInvited().getId()));
        }
    }
}
