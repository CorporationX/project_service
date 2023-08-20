package faang.school.projectservice.service;

import faang.school.projectservice.dto.StageInvitationDto;
import faang.school.projectservice.exception.InvalidUserException;
import faang.school.projectservice.mapper.StageInvitationMapper;
import faang.school.projectservice.messages.ErrorMessages;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.filter.stageinvite.FilterStageInviteDto;
import faang.school.projectservice.filter.stageinvite.StageInviteFilter;
import faang.school.projectservice.repository.StageInvitationRepository;
import faang.school.projectservice.service.stage.StageService;
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
    private final StageService stageService;
    private final StageInvitationMapper stageInvitationMapper;
    private final List<StageInviteFilter> filters;

    @Transactional
    public StageInvitationDto sendInvite(StageInvitationDto stageInvitationDto, Long currentUserId) {
        Stage currentStage = stageService.getEntityStageById(stageInvitationDto.getStageId());
        validateCurrentUser(currentUserId, currentStage);
        validateInvited(stageInvitationDto.getInvitedId(), currentStage);

        StageInvitation stageInvitation = stageInvitationMapper.dtoToEntity(stageInvitationDto);
        stageInvitation.setStatus(StageInvitationStatus.PENDING);

        TeamMember author = currentStage.getExecutors().stream()
                .filter(teamMember -> teamMember.getRoles().contains(TeamRole.OWNER))
                .toList().get(0);

        TeamMember invited = TeamMember.builder().userId(stageInvitationDto.getInvitedId()).build();
        stageInvitation.setAuthor(author);
        stageInvitation.setStage(currentStage);

        return stageInvitationMapper.entityToDto(stageInvitationRepository.save(stageInvitation));
    }

    public StageInvitationDto acceptInvite(StageInvitationDto stageInvitationDto, Long currentUserId) {
        validateIsInvitedInInvitation(stageInvitationDto.getInvitedId(), currentUserId);
        StageInvitation stageInvitation = stageInvitationRepository.findById(stageInvitationDto.getId());
        stageInvitation.setStatus(StageInvitationStatus.ACCEPTED);
        stageInvitation.getStage().getExecutors().add(stageInvitation.getInvited());
        return stageInvitationMapper.entityToDto(stageInvitationRepository.save(stageInvitation));
    }

    public StageInvitationDto rejectInvite(StageInvitationDto stageInvitationDto, Long currentUserId) {
        validateIsInvitedInInvitation(stageInvitationDto.getInvitedId(), currentUserId);
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

    private void validateInvited(Long invitedId, Stage stageForInvite){
        boolean isInvitedInStage = stageForInvite.getExecutors().stream()
                .anyMatch(teamMember -> teamMember.getUserId().equals(invitedId));
        if(isInvitedInStage){
            throw new IllegalArgumentException(String.format("User with ID %d has been invited to stage with ID %d, yet.",
                    invitedId, stageForInvite.getStageId()));
        }
    }

    private void validateIsInvitedInInvitation(Long invitedId, Long currentUserId) {
        if(invitedId.equals(currentUserId)){
            throw new InvalidUserException(ErrorMessages.INVALID_CURRENT_USER);
        }
    }
}
