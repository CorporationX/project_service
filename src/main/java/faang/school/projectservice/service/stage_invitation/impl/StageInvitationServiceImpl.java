package faang.school.projectservice.service.stage_invitation.impl;

import faang.school.projectservice.dto.stage_invitation.StageInvitationDto;
import faang.school.projectservice.dto.stage_invitation.StageInvitationFilterDto;
import faang.school.projectservice.exception.InvalidInvitationStatusException;
import faang.school.projectservice.exception.InvitationAlreadyExistsException;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class StageInvitationServiceImpl implements StageInvitationService {

    private final StageInvitationRepository stageInvitationRepository;
    private final StageInvitationMapper stageInvitationMapper;
    private final StageRepository stageRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final List<StageInvitationFilter> filters;


    @Override
    @Transactional
    public StageInvitationDto sendInvitation(StageInvitationDto invitationDto) {
        log.info("Sending invitation: {}", invitationDto);

        Stage stage = stageRepository.getById(invitationDto.stageId());

        TeamMember author = teamMemberRepository.findById(invitationDto.authorId());

        TeamMember invited = teamMemberRepository.findById(invitationDto.invitedId());

        if (stageInvitationRepository.existsByAuthorAndInvitedAndStage(author, invited, stage)) {
            String message = String.format(
                    "Invitation from authorId=%d to invitedId=%d for stageId=%d already exists.",
                    invitationDto.authorId(), invitationDto.invitedId(), invitationDto.stageId());
            log.warn(message);
            throw new InvitationAlreadyExistsException(message);
        }

        StageInvitation invitation = stageInvitationMapper.toStageInvitation(invitationDto);
        invitation.setStage(stage);
        invitation.setAuthor(author);
        invitation.setInvited(invited);
        invitation.setStatus(StageInvitationStatus.PENDING);

        invitation = stageInvitationRepository.save(invitation);
        log.info("Invitation sent successfully: {}", invitation);
        return stageInvitationMapper.toStageInvitationDto(invitation);
    }

    @Override
    @Transactional
    public StageInvitationDto acceptInvitation(Long id) {
        log.info("Accepting invitation with id: {}", id);

        StageInvitation invitation = stageInvitationRepository.findById(id);

        if (invitation.getStatus() != StageInvitationStatus.PENDING) {
            String message = String.format(
                    "Invitation with id=%d cannot be accepted in its current status: %s",
                    id, invitation.getStatus());
            log.warn(message);
            throw new InvalidInvitationStatusException(message);
        }

        invitation.setStatus(StageInvitationStatus.ACCEPTED);
        invitation = stageInvitationRepository.save(invitation);

        Stage stage = invitation.getStage();
        TeamMember invited = invitation.getInvited();

        stage.getExecutors().add(invited);
        stageRepository.save(stage);

        log.info("Invitation accepted: {}", invitation);
        return stageInvitationMapper.toStageInvitationDto(invitation);
    }

    @Override
    @Transactional
    public StageInvitationDto declineInvitation(Long id, String reason) {
        log.debug("Declining invitation with id: {} for reason: {}", id, reason);

        StageInvitation invitation = stageInvitationRepository.findById(id);

        if (invitation.getStatus() != StageInvitationStatus.PENDING) {
            String message = String.format(
                    "Invitation with id=%d cannot be declined in its current status: %s",
                    id, invitation.getStatus());
            log.warn(message);
            throw new InvalidInvitationStatusException(message);
        }

        invitation.setStatus(StageInvitationStatus.REJECTED);
        invitation.setDescription(reason);

        invitation = stageInvitationRepository.save(invitation);
        log.info("Invitation declined: {}", invitation);
        return stageInvitationMapper.toStageInvitationDto(invitation);
    }

    @Override
    @Transactional
    public List<StageInvitationDto> getInvitations(StageInvitationFilterDto filterDto) {
        log.info("Getting invitations with filters: {}", filterDto);

        List<StageInvitation> invitations = stageInvitationRepository.findAll();

        for (StageInvitationFilter filter : filters) {
            invitations = filter.apply(invitations, filterDto);
        }

        return invitations.stream()
                .map(stageInvitationMapper::toStageInvitationDto)
                .collect(Collectors.toList());
    }
}