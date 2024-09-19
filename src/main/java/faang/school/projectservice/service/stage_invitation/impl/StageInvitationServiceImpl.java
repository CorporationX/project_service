package faang.school.projectservice.service.stage_invitation.impl;

import faang.school.projectservice.dto.stage_invitation.StageInvitationDto;
import faang.school.projectservice.dto.stage_invitation.StageInvitationFilterDto;
import faang.school.projectservice.filter.StageInvitationFilter;
import faang.school.projectservice.filter.impl.InvitedIdFilter;
import faang.school.projectservice.filter.impl.StatusFilter;
import faang.school.projectservice.mapper.StageInvitationMapper;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.repository.StageInvitationRepository;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.service.stage_invitation.StageInvitationService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StageInvitationServiceImpl implements StageInvitationService {

    private final StageInvitationRepository stageInvitationRepository;
    private final StageInvitationMapper stageInvitationMapper;
    private final StageRepository stageRepository;
    private final TeamMemberRepository teamMemberRepository;

    @Override
    @Transactional
    public StageInvitationDto sendInvitation(StageInvitationDto invitationDto) {
        Stage stage = stageRepository.getById(invitationDto.stageId());

        TeamMember author = teamMemberRepository.findById(invitationDto.authorId());

        TeamMember invited = teamMemberRepository.findById(invitationDto.invitedId());

        if (stageInvitationRepository.existsByAuthorAndInvitedAndStage(author, invited, stage)) {
            throw new IllegalArgumentException("Invitation already exists.");
        }

        StageInvitation invitation = stageInvitationMapper.toStageInvitation(invitationDto);
        invitation.setStage(stage);
        invitation.setAuthor(author);
        invitation.setInvited(invited);
        invitation.setStatus(StageInvitationStatus.PENDING);

        StageInvitation savedInvitation = stageInvitationRepository.save(invitation);
        return stageInvitationMapper.toStageInvitationDto(savedInvitation);
    }

    @Override
    @Transactional
    public StageInvitationDto acceptInvitation(Long id) {
        StageInvitation invitation = stageInvitationRepository.findById(id);

        if (invitation.getStatus() != StageInvitationStatus.PENDING) {
            throw new IllegalStateException("Invitation cannot be accepted in its current status.");
        }

        invitation.setStatus(StageInvitationStatus.ACCEPTED);
        StageInvitation updatedInvitation = stageInvitationRepository.save(invitation);

        Stage stage = invitation.getStage();
        TeamMember invited = invitation.getInvited();

        stage.getExecutors().add(invited);
        stageRepository.save(stage);

        return stageInvitationMapper.toStageInvitationDto(updatedInvitation);
    }

    @Override
    @Transactional
    public StageInvitationDto declineInvitation(Long id, String reason) {
        StageInvitation invitation = stageInvitationRepository.findById(id);

        if (invitation.getStatus() != StageInvitationStatus.PENDING) {
            throw new IllegalStateException("Invitation cannot be declined in its current status.");
        }

        invitation.setStatus(StageInvitationStatus.REJECTED);
        invitation.setDescription(reason);

        StageInvitation updatedInvitation = stageInvitationRepository.save(invitation);
        return stageInvitationMapper.toStageInvitationDto(updatedInvitation);
    }

    @Override
    @Transactional
    public List<StageInvitationDto> getInvitations(StageInvitationFilterDto filterDto) {
        List<StageInvitation> invitations = stageInvitationRepository.findAll();

        List<StageInvitationFilter> filters = createFilters(filterDto);
        for (StageInvitationFilter filter : filters) {
            invitations = filter.apply(invitations);
        }

        return invitations.stream()
                .map(stageInvitationMapper::toStageInvitationDto)
                .collect(Collectors.toList());
    }

    private List<StageInvitationFilter> createFilters(StageInvitationFilterDto filterDto) {
        List<StageInvitationFilter> filters = new ArrayList<>();

        if (filterDto.getInvitedId() != null) {
            filters.add(new InvitedIdFilter(filterDto.getInvitedId()));
        }

        if (filterDto.getStatus() != null) {
            filters.add(new StatusFilter(filterDto.getStatus()));
        }

        return filters;
    }
}