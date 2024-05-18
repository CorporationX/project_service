package faang.school.projectservice.service;

import faang.school.projectservice.dto.client.StageInvitationDto;
import faang.school.projectservice.filter.InvitationFilter;
import faang.school.projectservice.filter.InvitationFilterDto;
import faang.school.projectservice.mapper.InvitationMapper;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.repository.StageInvitationRepository;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.validator.StageInvitationValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StageInvitationService {
    private final StageInvitationValidator stageInvitationValidator;
    private final InvitationMapper invitationMapper;
    private final StageInvitationRepository stageInvitationRepository;
    private final StageRepository stageRepository;
    private final List<InvitationFilter> invitationFilters;
    private final TeamMemberRepository teamMemberRepository;

    @Transactional
    public StageInvitationDto createInvitation(StageInvitationDto stageInvitationDto) {
        Stage stage = stageRepository.getById(stageInvitationDto.getStageId());
        TeamMember invited = teamMemberRepository.findById(stageInvitationDto.getInvitedId());

        stageInvitationValidator.createValidationService(stage, invited);
        StageInvitation stageInvitation = invitationMapper.toEntity(stageInvitationDto);
        stageInvitation.setStatus(StageInvitationStatus.PENDING);

        return invitationMapper.toDto(stageInvitationRepository.save(stageInvitation));
    }

    @Transactional
    public StageInvitationDto acceptInvitation(StageInvitationDto stageInvitationDto) {
        stageInvitationValidator.acceptOrRejectInvitationService(stageInvitationDto);
        StageInvitation stageInvitation = invitationMapper.toEntity(stageInvitationDto);
        StageInvitation stageInvitationFromDB = stageInvitationRepository.findById(stageInvitation.getId());

        stageInvitationFromDB.setStatus(StageInvitationStatus.ACCEPTED);
        addInvitationToProject(stageInvitationFromDB,  stageInvitationFromDB.getInvited().getId());

        return invitationMapper.toDto(stageInvitationFromDB);
    }

    @Transactional
    public StageInvitationDto rejectInvitation(StageInvitationDto stageInvitationDto) {
        stageInvitationValidator.acceptOrRejectInvitationService(stageInvitationDto);
        StageInvitation stageInvitation = invitationMapper.toEntity(stageInvitationDto);
        StageInvitation stageInvitationFromDB = stageInvitationRepository.findById(stageInvitation.getId());

        stageInvitationFromDB.setStatus(StageInvitationStatus.REJECTED);
        stageInvitationFromDB.setDescription(stageInvitationDto.getExplanation());

        return invitationMapper.toDto(stageInvitationFromDB);
    }

    @Transactional(readOnly = true)
    public List<StageInvitationDto> showAllInvitationForMember(Long userId, InvitationFilterDto invitationFilterDto) {
        List<StageInvitation> invitations = stageInvitationRepository.findAll().stream()
                .filter(stageInvitation -> stageInvitation.getInvited().getUserId().equals(userId)).toList();

        return invitationFilters.stream()
                .filter(filter -> filter.isApplicable(invitationFilterDto))
                .flatMap(filter -> filter.apply(invitations.stream(), invitationFilterDto))
                .map(invitationMapper::toDto).toList();
    }

    private void addInvitationToProject(StageInvitation stageInvitation, Long invitationId) {
        List<TeamMember> teamMembers = new ArrayList<>(stageInvitation.getStage().getExecutors());

        TeamMember newExecutor = teamMemberRepository.findById(invitationId);
        teamMembers.add(newExecutor);

        stageInvitation.getStage().setExecutors(teamMembers);
        stageInvitationRepository.save(stageInvitation);
    }
}