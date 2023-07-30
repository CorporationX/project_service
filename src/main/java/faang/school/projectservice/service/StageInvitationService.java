package faang.school.projectservice.service;

import faang.school.projectservice.dto.invitation.StageInvitationDto;
import faang.school.projectservice.dto.invitation.StageInvitationFilterDto;
import faang.school.projectservice.exception.DataValidateInviteException;
import faang.school.projectservice.filter.StageInvitationFilter;
import faang.school.projectservice.mapper.StageInvitationMapper;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.repository.StageInvitationRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import lombok.NonNull;
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
    private final List<StageInvitationFilter> filters;
    private final TeamMemberRepository teamMemberRepository;

    @Transactional
    public StageInvitationDto sendInvitation(@NonNull StageInvitationDto stageInvitationDto) {
        Long invitedUserId = stageInvitationDto.getInvited().getUserId();
        TeamMember invitedUser = teamMemberRepository.findById(invitedUserId);

        if (invitedUser.getRoles().contains(TeamRole.OWNER) || invitedUser.getRoles().contains(TeamRole.MANAGER)) {
            throw new DataValidateInviteException("Can't send invitation to user with OWNER or MANAGER role");
        }

        List<TeamMember> executors = stageInvitationDto.getStage().getExecutors();
        boolean isUserAlreadyExecutor = executors.stream()
                .anyMatch(executor -> executor.getId().equals(invitedUser.getId()));

        if (!isUserAlreadyExecutor) {
            StageInvitation stageInvitation = stageInvitationMapper.toEntity(stageInvitationDto);
            stageInvitation.setStatus(StageInvitationStatus.PENDING);
            TeamMember author = stageInvitationDto.getStage().getExecutors().stream()
                    .filter(teamMember -> teamMember.getRoles().contains(TeamRole.OWNER))
                    .toList().get(0);
            TeamMember invited = stageInvitationDto.getInvited();
            stageInvitation.setAuthor(author);
            stageInvitation.setInvited(invited);
            return stageInvitationMapper.toDto(stageInvitationRepository.save(stageInvitation));
        } else {
            throw new DataValidateInviteException("User is already executor for this stage");
        }
    }

    @Transactional
    public StageInvitationDto acceptInvitation(@NonNull StageInvitationDto stageInvitationDto) {
        StageInvitation stageInvitation = stageInvitationRepository.findById(stageInvitationDto.getId());
        stageInvitation.setStatus(StageInvitationStatus.ACCEPTED);

        TeamMember invitedUser = stageInvitation.getInvited();
        Stage stage = stageInvitation.getStage();

        boolean isUserAlreadyExecutor = stage.getExecutors().stream()
                .anyMatch(executor -> executor.getId().equals(invitedUser.getId()));

        if (!isUserAlreadyExecutor) {
            stageInvitation.getStage().getExecutors().add(stageInvitation.getInvited());
        } else {
            throw new DataValidateInviteException("User is already executor for this stage");
        }
        return stageInvitationMapper.toDto(stageInvitationRepository.save(stageInvitation));
    }

    @Transactional
    public StageInvitationDto rejectInvitation(@NonNull StageInvitationDto stageInvitationDto) {
        StageInvitation stageInvitation = stageInvitationRepository.findById(stageInvitationDto.getId());
        stageInvitationMapper.updateDto(stageInvitationDto, stageInvitation);
        stageInvitation.setStatus(StageInvitationStatus.REJECTED);
        return stageInvitationMapper.toDto(stageInvitationRepository.save(stageInvitation));
    }

    @Transactional(readOnly = true)
    public List<StageInvitationDto> getInvitationsForTeamMemberWithFilters(Long userId, StageInvitationFilterDto stageInvitationFilterDto) {
        Stream<StageInvitation> userInvitations = stageInvitationRepository.findAll().stream()
                .filter(stageInvitation -> stageInvitation.getInvited().getUserId().equals(userId));
        List<StageInvitationFilter> requestedFilters = filters.stream()
                .filter(stageInvitationFilter -> stageInvitationFilter.isApplicable(stageInvitationFilterDto))
                .toList();

        for (StageInvitationFilter requestedFilter : requestedFilters) {
            userInvitations = requestedFilter.apply(userInvitations, stageInvitationFilterDto);
        }
        return userInvitations.map(stageInvitationMapper::toDto)
                .toList();
    }
}