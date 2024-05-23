package faang.school.projectservice.service;

import faang.school.projectservice.dto.filter.StageInvitationFilterDto;
import faang.school.projectservice.dto.stage_invintation.StageInvitationDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.StageInvitationMapper;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.repository.StageInvitationRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.service.filter.stage_invitation_filter.StageInvitationFilter;
import faang.school.projectservice.validator.StageInvitationValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StageInvitationService {
    private final StageInvitationMapper stageInvitationMapper;
    private final StageInvitationValidator stageInvitationValidator;
    private final List<StageInvitationFilter> stageInvitationFilters;
    private final StageInvitationRepository stageInvitationRepository;
    private final TeamMemberRepository teamMemberRepository;

    @Transactional
    public StageInvitationDto sendInvite(StageInvitationDto stageInvitationDto) {
        stageInvitationValidator.validateAll(stageInvitationDto);
        StageInvitation stageInvitation = stageInvitationMapper.toEntity(stageInvitationDto);
        stageInvitation.setStatus(StageInvitationStatus.PENDING);
        stageInvitationRepository.save(stageInvitation);

        return stageInvitationMapper.toDto(stageInvitation);
    }

    @Transactional
    public StageInvitationDto acceptInvitation(StageInvitationDto stageInvitationDto) {
        stageInvitationValidator.validateAll(stageInvitationDto);
        long id = stageInvitationDto.getId();
        StageInvitation stageInvitation = stageInvitationRepository.findById(id);

        checkAndSetStatus(stageInvitation, StageInvitationStatus.PENDING, StageInvitationStatus.ACCEPTED);
        long userId = stageInvitation.getInvited().getUserId();
        TeamMember teamMember = teamMemberRepository.findById(userId);
        stageInvitation.setInvited(teamMember);
        stageInvitationRepository.save(stageInvitation);
        return stageInvitationMapper.toDto(stageInvitation);
    }

    @Transactional
    public StageInvitationDto rejectInvitation(StageInvitationDto stageInvitationDto) {
        stageInvitationValidator.validateAll(stageInvitationDto);
        Long invitationId = stageInvitationDto.getId();
        StageInvitation stageInvitation = stageInvitationRepository.findById(invitationId);

        checkAndSetStatus(stageInvitation, StageInvitationStatus.PENDING, StageInvitationStatus.REJECTED);

        stageInvitationRepository.save(stageInvitation);
        return stageInvitationMapper.toDto(stageInvitation);
    }

    @Transactional
    public List<StageInvitationDto> getAllInvitationsForUserWithStatus(Long userId, StageInvitationFilterDto stageInvitationFilterDto) {
        stageInvitationValidator.validateId(userId);
        List<StageInvitation> stageInvitations = stageInvitationRepository.findAll();
        List<StageInvitationFilter> invitationFilters = stageInvitationFilters.stream()
                .filter(invFilter -> invFilter.isApplicable(stageInvitationFilterDto))
                .toList();
        return stageInvitations.stream()
                .filter(stageInvitation -> invitationFilters.stream()
                        .allMatch(stageInvitationFilter -> stageInvitationFilter.apply(stageInvitation, stageInvitationFilterDto)))
                .map(stageInvitation -> stageInvitationMapper.toDto(stageInvitation))
                .toList();
    }

    private void checkAndSetStatus(StageInvitation stageInvitation, StageInvitationStatus oldStatus, StageInvitationStatus updatedStatus) {
        if (!stageInvitation.getStatus().equals(oldStatus)) {
            throw new DataValidationException("This task cannot be accepted yet.");
        }
        stageInvitation.setStatus(updatedStatus);
    }
}