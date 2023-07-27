package faang.school.projectservice.service;

import faang.school.projectservice.dto.StageInvitationDto;
import faang.school.projectservice.filters.mappers.StageInvitationMapper;
import faang.school.projectservice.messages.ErrorMessages;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.filters.stageInvites.FilterStageInviteDto;
import faang.school.projectservice.filters.stageInvites.StageInviteFilter;
import faang.school.projectservice.repository.StageInvitationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class StageInvitationService {
    private final StageInvitationRepository stageInvitationRepository;
    private final StageInvitationMapper stageInvitationMapper;
    private final List<StageInviteFilter> filters;

    public StageInvitationDto sendInvite(StageInvitationDto stageInvitationDto) {
        StageInvitation stageInvitation = stageInvitationMapper.dtoToEntity(stageInvitationDto);
        stageInvitation.setStatus(StageInvitationStatus.PENDING);
        TeamMember author = stageInvitationDto.getStage().getExecutors().stream()
                .filter(teamMember -> teamMember.getRoles().contains(TeamRole.OWNER))
                .toList().get(0);
        TeamMember invited = stageInvitationDto.getInvited();
        stageInvitation.setAuthor(author);
        stageInvitation.setInvited(invited);
        return stageInvitationMapper.entityToDto(stageInvitationRepository.save(stageInvitation));
    }

    public StageInvitationDto acceptInvite(StageInvitationDto stageInvitationDto) {
        StageInvitation stageInvitation = stageInvitationRepository.findById(stageInvitationDto.getId());
        stageInvitation.setStatus(StageInvitationStatus.ACCEPTED);
        stageInvitation.getStage().getExecutors().add(stageInvitation.getInvited());
        return stageInvitationMapper.entityToDto(stageInvitationRepository.save(stageInvitation));
    }

    public StageInvitationDto rejectInvite(StageInvitationDto stageInvitationDto) {
        StageInvitation stageInvitation = stageInvitationRepository.findById(stageInvitationDto.getId());
        stageInvitationMapper.updateEntityViaDto(stageInvitationDto, stageInvitation);
        stageInvitation.setStatus(StageInvitationStatus.REJECTED);
        return stageInvitationMapper.entityToDto(stageInvitationRepository.save(stageInvitation));
    }

    public List<StageInvitationDto> getFilteredInvites(Long userId, FilterStageInviteDto filterStageInviteDto) {
        List<StageInvitation> userInvites = stageInvitationRepository.findAll().stream()
                .filter(stageInvitation -> stageInvitation.getInvited().getUserId().equals(userId)).toList();
        List<List<StageInvitation>> afterFilters = filters.stream()
                .filter(filter -> filter.isApplicable(filterStageInviteDto))
                .map(filter -> filter.apply(userInvites.stream(), filterStageInviteDto).toList())
                .toList();
        List<StageInvitation> result = afterFilters.get(0);
        if(result.size() == 0){
            throw new RuntimeException(ErrorMessages.NO_SUCH_STAGE_INVITATIONS);
        }
        if(afterFilters.size() == 1){
            return stageInvitationMapper.listEntityToDto(result);
        }
        for (int i = 1; i < afterFilters.size(); i++) {
            result = result.stream()
                    .filter(afterFilters.get(i)::contains)
                    .toList();
        }
        return stageInvitationMapper.listEntityToDto(result);
    }
}
