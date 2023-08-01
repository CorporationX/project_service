package faang.school.projectservice.service;

import faang.school.projectservice.dto.StageInvitationDto;
import faang.school.projectservice.exceptions.InvalidUserException;
import faang.school.projectservice.filters.mappers.StageInvitationMapper;
import faang.school.projectservice.messages.ErrorMessages;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.filters.stageInvites.FilterStageInviteDto;
import faang.school.projectservice.filters.stageInvites.StageInviteFilter;
import faang.school.projectservice.repository.StageInvitationRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class StageInvitationService {
    private final StageInvitationRepository stageInvitationRepository;
    private final StageInvitationMapper stageInvitationMapper;
    private final List<StageInviteFilter> filters;

    @Transactional
    public StageInvitationDto sendInvite(StageInvitationDto stageInvitationDto, Long currentUserId) {
        validateCurrentUser(currentUserId, stageInvitationDto.getStage());
        validateInvited(stageInvitationDto.getInvited(), stageInvitationDto.getStage());
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

    public StageInvitationDto acceptInvite(StageInvitationDto stageInvitationDto, Long currentUserId) {
        validateIsInvitedInInvitation(stageInvitationDto.getInvited(), currentUserId);
        StageInvitation stageInvitation = stageInvitationRepository.findById(stageInvitationDto.getId());
        stageInvitation.setStatus(StageInvitationStatus.ACCEPTED);
        stageInvitation.getStage().getExecutors().add(stageInvitation.getInvited());
        return stageInvitationMapper.entityToDto(stageInvitationRepository.save(stageInvitation));
    }

    public StageInvitationDto rejectInvite(StageInvitationDto stageInvitationDto, Long currentUserId) {
        validateIsInvitedInInvitation(stageInvitationDto.getInvited(), currentUserId);
        StageInvitation stageInvitation = stageInvitationRepository.findById(stageInvitationDto.getId());
        stageInvitationMapper.updateEntityViaDto(stageInvitationDto, stageInvitation);
        stageInvitation.setStatus(StageInvitationStatus.REJECTED);
        return stageInvitationMapper.entityToDto(stageInvitationRepository.save(stageInvitation));
    }

    public List<StageInvitationDto> getFilteredInvites(Long userId, FilterStageInviteDto filterStageInviteDto) {
        Stream<StageInvitation> userInvites = stageInvitationRepository.findAll().stream()
                .filter(stageInvitation -> stageInvitation.getInvited().getUserId().equals(userId));
        List<StageInviteFilter> requiredFilters = filters.stream()
                .filter(filter -> filter.isApplicable(filterStageInviteDto))
                .toList();
        for (StageInviteFilter requiredFilter : requiredFilters) {
            userInvites = requiredFilter.apply(userInvites, filterStageInviteDto);
        }
        return userInvites.map(stageInvitationMapper::entityToDto).toList();
    }

    private void validateCurrentUser(Long currentUserId, Stage stage) {
        TeamMember currentUser = stage.getExecutors().stream()
                .filter(teamMember -> teamMember.getUserId().equals(currentUserId))
                .findAny()
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessages.INVALID_CURRENT_USER));
        List<TeamRole> requiredRoles = List.of(TeamRole.OWNER, TeamRole.MANAGER);
        if(currentUser.getRoles().stream().noneMatch(requiredRoles::contains)){
            throw new InvalidUserException(ErrorMessages.INVALID_CURRENT_USER);
        }
    }

    private void validateInvited(TeamMember invited, Stage stageForInvite){
        if(stageForInvite.getExecutors().contains(invited)){
            throw new IllegalArgumentException(String.format("User with ID %d has been invited, yet.", invited.getUserId()));
        }
    }

    private void validateIsInvitedInInvitation(TeamMember invited, Long currentUserId) {
        if(invited.getUserId().equals(currentUserId)){
            throw new InvalidUserException(ErrorMessages.INVALID_CURRENT_USER);
        }
    }
}
