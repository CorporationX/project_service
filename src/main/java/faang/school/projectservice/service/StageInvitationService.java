package faang.school.projectservice.service;

import faang.school.projectservice.dto.stageinvitation.ChangeStatusDto;
import faang.school.projectservice.dto.stageinvitation.RejectInvitationDto;
import faang.school.projectservice.dto.stageinvitation.StageInvitationDto;
import faang.school.projectservice.dto.stageinvitation.StageInvitationFilterDto;
import faang.school.projectservice.dto.stageinvitation.StageInvitationUpdateDto;
import faang.school.projectservice.event.InviteSentEvent;
import faang.school.projectservice.exception.BusinessException;
import faang.school.projectservice.filter.stageinvitation.StageInvitationFilter;
import faang.school.projectservice.mapper.stageinvitation.ChangeStatusMapper;
import faang.school.projectservice.mapper.stageinvitation.InvitationUpdateMapper;
import faang.school.projectservice.mapper.stageinvitation.RejectInvitationMapper;
import faang.school.projectservice.mapper.stageinvitation.StageInvitationMapper;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.publisher.InviteSentEventPublisher;
import faang.school.projectservice.repository.StageInvitationRepository;
import faang.school.projectservice.validator.stageinvitation.StageInvitationValidator;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Service
@Slf4j
@RequiredArgsConstructor
public class StageInvitationService {
    private final StageInvitationMapper stageInvitationMapper;
    private final StageService stageService;
    private final TeamMemberService teamMemberService;
    private final StageInvitationRepository stageInvitationRepository;
    private final StageInvitationValidator stageInvitationValidator;
    private final ChangeStatusMapper changeStatusMapper;
    private final RejectInvitationMapper rejectInvitationMapper;
    private final List<StageInvitationFilter> stageInvitationFilters;
    private final InvitationUpdateMapper invitationUpdateMapper;
    private final InviteSentEventPublisher invitationPublisher;

    public StageInvitation findById(@NotNull Long stageInvitationId) {
        return stageInvitationRepository.findById(stageInvitationId)
                .orElseThrow(() -> new EntityNotFoundException("Такого приглашения не существует"));
    }

    public StageInvitationDto createStageInvitation(StageInvitationDto dto) {
        StageInvitation stageInvitation = stageInvitationValidator.validateStageInvitation(dto);

        var stage = stageService.findById(dto.getStageId());
        var authorTeamMember = teamMemberService.findById(dto.getAuthorId());
        var invitedTeamMember = teamMemberService.findById(dto.getInvitedId());
        stageInvitation.setStage(stage);
        stageInvitation.setAuthor(authorTeamMember);
        stageInvitation.setInvited(invitedTeamMember);
        stageInvitation.setStatus(StageInvitationStatus.PENDING);

        var event = new InviteSentEvent(invitedTeamMember.getUserId(), authorTeamMember.getUserId(),
                stage.getProject().getId());
        invitationPublisher.publish(event);

        return stageInvitationMapper.toDto(stageInvitationRepository.save(stageInvitation));
    }

    public StageInvitationUpdateDto updateStageInvitation(StageInvitationUpdateDto dto) {
        stageInvitationValidator.validateUpdateInvitation(dto);

        StageInvitation stageInvitation = findById(dto.getId());
        stageInvitation.setStage(stageService.findById(dto.getId()));

        invitationUpdateMapper.update(stageInvitation, dto);
        stageInvitationRepository.save(stageInvitation);

        return invitationUpdateMapper.toDto(stageInvitation);
    }

    public ChangeStatusDto acceptStageInvitation(ChangeStatusDto dto) {
        StageInvitation stageInvitation = findPendingStageInvitation(dto.getId());

        List<Stage> stages = teamMemberService.findById(dto.getInvitedId()).getStages();
        Stage stage = stageInvitation.getStage();

        if (stages.contains(stage)) {
            throw new BusinessException("Пользователь уже является исполнителем этого этапа");
        }

        stages.add(stage);
        updateStatus(stageInvitation, dto, StageInvitationStatus.ACCEPTED);

        return changeStatusMapper.toDto(stageInvitation);
    }

    public RejectInvitationDto rejectStageInvitation(RejectInvitationDto dto) {
        StageInvitation stageInvitation = findPendingStageInvitation(dto.getStatusDto().getId());
        updateStatus(stageInvitation, dto.getStatusDto(), StageInvitationStatus.REJECTED);

        return rejectInvitationMapper.toDto(stageInvitation);
    }

    public List<StageInvitationDto> getStageInvitationForTeamMember(Long invitedId) {
        Stream<StageInvitation> stageInvitationStream = stageInvitationRepository
                .findAll()
                .stream();

        StageInvitationFilterDto filterDto = StageInvitationFilterDto
                .builder()
                .invitedId(invitedId)
                .build();

        stageInvitationFilters.stream()
                .filter(invitationFilter -> invitationFilter.isApplicable(filterDto))
                .forEach(invitationFilter -> invitationFilter.apply(stageInvitationStream, filterDto));

        return stageInvitationStream.map(stageInvitationMapper::toDto).toList();
    }

    private void updateStatus(StageInvitation stageInvitation,
                              ChangeStatusDto statusDto,
                              StageInvitationStatus status) {
        statusDto.setStatus(status);
        changeStatusMapper.update(stageInvitation, statusDto);
    }

    private StageInvitation findPendingStageInvitation(Long stageInvitationId) {
        StageInvitation stageInvitation = findById(stageInvitationId);

        if (stageInvitation.getStatus() != StageInvitationStatus.PENDING) {
            throw new BusinessException("Такое приглашение уже рассмотрено");
        }

        return stageInvitation;
    }
}
