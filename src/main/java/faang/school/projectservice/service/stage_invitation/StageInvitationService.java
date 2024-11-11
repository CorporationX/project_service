package faang.school.projectservice.service.stage_invitation;

import faang.school.projectservice.dto.invitation.StageInvitationDto;
import faang.school.projectservice.dto.invitation.StageInvitationFilterDto;
import faang.school.projectservice.mapper.StageInvitationMapper;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.repository.StageInvitationRepository;
import faang.school.projectservice.service.stage_invitation.filter.StageInvitationFilter;
import faang.school.projectservice.validator.StageInvitationValidator;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Data
@Service
public class StageInvitationService {
    private final StageInvitationRepository stageInvitationRepository;
    private final StageInvitationMapper stageInvitationMapper;
    private final StageInvitationValidator stageInvitationValidate;
    private final List<StageInvitationFilter> invitationFilters;


    public StageInvitationDto createInvitation(StageInvitationDto stageInvitationDto) {
        stageInvitationValidate.validateInvitation(stageInvitationDto);
        StageInvitation invitation = stageInvitationMapper.toEntity(stageInvitationDto);
        stageInvitationRepository.save(invitation);

        return stageInvitationMapper.toDto(invitation);
    }

    public StageInvitationDto acceptInvitation(StageInvitationDto stageInvitationDto) {
        StageInvitation invitation = stageInvitationMapper.toEntity(stageInvitationDto);
        invitation.setStatus(StageInvitationStatus.ACCEPTED);
        List<TeamMember> executors = invitation.getStage().getExecutors();
        TeamMember executor = invitation.getInvited();
        executors.add(executor);

        return stageInvitationMapper.toDto(invitation);
    }

    public StageInvitationDto rejectInvitation(StageInvitationDto stageInvitationDto) {
        StageInvitation invitation = stageInvitationMapper.toEntity(stageInvitationDto);
        stageInvitationValidate.validateDescription(stageInvitationDto);
        invitation.setStatus(StageInvitationStatus.REJECTED);

        return stageInvitationMapper.toDto(invitation);
    }

    public List<StageInvitationDto> viewAllInvitation(StageInvitationDto stageInvitationDto, StageInvitationFilterDto filter) {
        StageInvitation invitation = stageInvitationMapper.toEntity(stageInvitationDto);
        Long invitedId = invitation.getInvited().getUserId();

        List<StageInvitation> allInvitationOfInvited = stageInvitationRepository.findAll().stream()
                .filter(i -> i.getInvited().getUserId().equals(invitedId))
                .toList();

        return filterInvitation(allInvitationOfInvited, filter);
    }

    private List<StageInvitationDto> filterInvitation(List<StageInvitation> invitations, StageInvitationFilterDto filter) {
        Stream<StageInvitation> invitationStream = invitations.stream();

        return invitationFilters.stream()
                .filter(f -> f.isApplicable(filter))
                .flatMap(f -> f.apply(invitationStream, filter))
                .map(stageInvitationMapper::toDto)
                .toList();
    }
}
