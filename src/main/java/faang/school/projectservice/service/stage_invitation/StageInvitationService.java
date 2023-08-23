package faang.school.projectservice.service.stage_invitation;

import faang.school.projectservice.dto.stage_invitation.StageInvitationDto;
import faang.school.projectservice.dto.stage_invitation.StageInvitationFilterDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.filter.stage_invitation.StageInvitationFilter;
import faang.school.projectservice.mapper.stage_invitation.StageInvitationMapper;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.publisher.InviteSentEventPublisher;
import faang.school.projectservice.publisher.event.InviteSentEvent;
import faang.school.projectservice.repository.StageInvitationRepository;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class StageInvitationService {
    private final StageInvitationRepository repository;
    private final StageRepository stageRepository;
    private final TeamMemberRepository TMRepository;
    private final StageInvitationMapper mapper;
    private final List<StageInvitationFilter> filters;
    private final InviteSentEventPublisher publisher;

    @Transactional
    public StageInvitationDto create(StageInvitationDto invitationDto) {
        validate(invitationDto);

        StageInvitation stageInvitation = mapper.toEntity(invitationDto);
        stageInvitation.setStatus(StageInvitationStatus.PENDING);
        StageInvitationDto result = mapper.toDTO(repository.save(stageInvitation));

        InviteSentEvent event = mapper.toEvent(invitationDto);
        Stage stage = stageRepository.getById(invitationDto.getStageId());
        event.setProjectId(stage.getProject().getId());
        publisher.publish(event);

        return result;
    }

    public StageInvitationDto accept(long invitationId) {
        StageInvitation invitation = repository.findById(invitationId);
        Set<TeamMember> executors = invitation
                .getStage()
                .getExecutors()
                .stream()
                .collect(Collectors.toSet());
        executors.add(invitation.getInvited());
        invitation.getStage().setExecutors(executors.stream().toList());
        invitation.setStatus(StageInvitationStatus.ACCEPTED);
        return mapper.toDTO(repository.save(invitation));
    }

    public StageInvitationDto reject(long invitationId, String message){
        StageInvitation invitation = repository.findById(invitationId);
        invitation.setStatus(StageInvitationStatus.REJECTED);
        invitation.setDescription(message);
        return mapper.toDTO(repository.save(invitation));
    }

    public List<StageInvitationDto> getStageInvitationsWithFilters(StageInvitationFilterDto filterDto) {
        var stageInvitationStream = repository.findAll().stream();
        List<StageInvitationFilter> filteredFilters = filters.stream()
                .filter(filter -> filter.isApplicable(filterDto))
                .toList();
        for (StageInvitationFilter filter : filteredFilters) {
            stageInvitationStream = filter.apply(stageInvitationStream, filterDto);
        }
        return stageInvitationStream.map(mapper::toDTO).toList();
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
