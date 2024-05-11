package faang.school.projectservice.service;

import faang.school.projectservice.dto.filter.StageInvitationFilterDto;
import faang.school.projectservice.dto.stage_invintation.StageInvitationDto;
import faang.school.projectservice.exception.DataValidationStageInvitationException;
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

import java.util.List;

@Service
@RequiredArgsConstructor
public class StageInvitationService {
    private final StageInvitationMapper stageInvitationMapper;
    private final StageInvitationValidator stageInvitationValidator;
    private final List<StageInvitationFilter> stageInvitationFilters;
    private final StageInvitationRepository stageInvitationRepository;
    private final TeamMemberRepository teamMemberRepository;

    public StageInvitationDto sendInvite(StageInvitationDto stageInvitationDto) {
        //валидация
        StageInvitation stageInvitation = stageInvitationMapper.toEntity(stageInvitationDto);
        stageInvitation.setStatus(StageInvitationStatus.PENDING);
        stageInvitationRepository.save(stageInvitation);

        return stageInvitationMapper.toDto(stageInvitation);
    }

    public StageInvitationDto acceptInvitation(StageInvitationDto stageInvitationDto) {
        //валидация
        StageInvitation stageInvitation = stageInvitationRepository.findById(stageInvitationDto.getId());

        if (!stageInvitation.getStatus().equals(StageInvitationStatus.PENDING)) {
            throw new DataValidationStageInvitationException("This task cannot be accepted yet.");
        }

        stageInvitation.setStatus(StageInvitationStatus.ACCEPTED);
        Long userId = stageInvitation.getInvited().getUserId();
        TeamMember teamMember = teamMemberRepository.findById(userId);
        stageInvitation.setInvited(teamMember);
        stageInvitationRepository.save(stageInvitation);
        return stageInvitationMapper.toDto(stageInvitation);
    }

    public StageInvitationDto rejectInvitation(StageInvitationDto stageInvitationDto) {
        //валидация
        StageInvitation stageInvitation = stageInvitationMapper.toEntity(stageInvitationDto);
        Long invitationId = stageInvitation.getId();
        stageInvitation = stageInvitationRepository.findById(invitationId);

        if (!stageInvitation.getStatus().equals(StageInvitationStatus.PENDING)) {
            throw new DataValidationStageInvitationException("This task cannot be accepted yet.");
        }

        stageInvitation.setStatus(StageInvitationStatus.REJECTED);
        stageInvitationRepository.save(stageInvitation);
        return stageInvitationMapper.toDto(stageInvitation);
    }

    public List<StageInvitationDto> getAllInvitationsForUserWithStatus(Long userId, StageInvitationFilterDto stageInvitationFilterDto) {
        //валидация
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
}