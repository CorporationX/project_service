package faang.school.projectservice.service;

import faang.school.projectservice.dto.stage_invitation.StageInvitationDto;
import faang.school.projectservice.dto.stage_invitation.StageInvitationFilterDto;
import faang.school.projectservice.filter.stage_invitation.StageInvitationFilter;
import faang.school.projectservice.mapper.stage_invitation.StageInvitationMapper;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.repository.StageInvitationRepository;
import faang.school.projectservice.validator.StageInvitationValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StageInvitationService {
    private final StageInvitationValidator validator;
    private final StageInvitationMapper mapper;
    private final StageInvitationRepository repository;
    private final List<StageInvitationFilter> filters;

    public StageInvitationDto create(StageInvitationDto stageInvitationDto) {
        validator.validateStageInvitation(stageInvitationDto);

        StageInvitation stageInvitation = mapper.toEntity(stageInvitationDto);
        stageInvitation.setStatus(StageInvitationStatus.PENDING);

        return mapper.toDto(repository.save(stageInvitation));
    }

    public StageInvitationDto accept(long stageInvitationId) {
        StageInvitation invitation = repository.findById(stageInvitationId);
        List<TeamMember> teamMembers = invitation.getStage().getExecutors();
        teamMembers.add(invitation.getInvited());
        invitation.getStage().setExecutors(teamMembers);
        invitation.setStatus(StageInvitationStatus.ACCEPTED);

        return mapper.toDto(repository.save(invitation));
    }

    public StageInvitationDto reject(long stageInvitationId, String message) {
        StageInvitation invitation = repository.findById(stageInvitationId);
        invitation.setStatus(StageInvitationStatus.REJECTED);
        invitation.setDescription(message);

        return mapper.toDto(repository.save(invitation));
    }

    public List<StageInvitationDto> findAllInviteByFilter(StageInvitationFilterDto filterDto) {
        List<StageInvitation> stageInvitations = repository.findAll();
        filters.stream()
                .filter(f -> f.IsApplicable(filterDto))
                .forEach(f -> f.apply(stageInvitations, filterDto));

        return stageInvitations.stream().map(mapper::toDto).toList();
    }
}
