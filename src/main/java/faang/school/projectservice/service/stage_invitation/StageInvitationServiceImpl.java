package faang.school.projectservice.service.stage_invitation;

import faang.school.projectservice.dto.stage_invitation.StageInvitationCreateDto;
import faang.school.projectservice.dto.stage_invitation.StageInvitationDto;
import faang.school.projectservice.dto.stage_invitation.StageInvitationFilterDto;
import faang.school.projectservice.mapper.StageInvitationMapper;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.publisher.InviteSentPublisher;
import faang.school.projectservice.repository.StageInvitationRepository;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.validation.stage_invitation.StageInvitationValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class StageInvitationServiceImpl implements StageInvitationService {

    private final StageInvitationRepository stageInvitationRepository;
    private final StageInvitationMapper stageInvitationMapper;
    private final TeamMemberRepository teamMemberRepository;
    private final StageRepository stageRepository;
    private final StageInvitationValidator stageInvitationValidator;
    private final StageInvitationFilterService stageInvitationFilterService;
    private final InviteSentPublisher inviteSentPublisher;

    @Override
    @Transactional
    public StageInvitationDto sendInvitation(StageInvitationCreateDto stageInvitationCreateDto) {

        StageInvitation stageInvitation = stageInvitationMapper.toEntity(stageInvitationCreateDto);

        stageInvitationValidator.validateExistences(stageInvitationCreateDto);

        long projectId = stageRepository.getById(stageInvitationCreateDto.getStageId()).getProject().getId();
        inviteSentPublisher.publish(stageInvitationMapper.toEvent(stageInvitationCreateDto, projectId));

        return stageInvitationMapper.toDto(stageInvitationRepository.save(stageInvitation));
    }

    @Override
    @Transactional
    public StageInvitationDto acceptInvitation(long userId, long inviteId) {

        TeamMember invited = teamMemberRepository.findById(userId);
        StageInvitation stageInvitation = stageInvitationRepository.findById(inviteId);

        stageInvitationValidator.validateUserInvitePermission(invited, stageInvitation);

        stageInvitation.setStatus(StageInvitationStatus.ACCEPTED);

        Stage stage = stageInvitation.getStage();
        stage.getExecutors().add(invited);

        return stageInvitationMapper.toDto(stageInvitationRepository.save(stageInvitation));
    }

    @Override
    @Transactional
    public StageInvitationDto rejectInvitation(long userId, long inviteId, String reason) {

        TeamMember invited = teamMemberRepository.findById(userId);
        StageInvitation stageInvitation = stageInvitationRepository.findById(inviteId);

        stageInvitationValidator.validateUserInvitePermission(invited, stageInvitation);

        stageInvitation.setStatus(StageInvitationStatus.REJECTED);
        stageInvitation.setDescription(reason);

        return stageInvitationMapper.toDto(stageInvitationRepository.save(stageInvitation));
    }

    @Override
    @Transactional(readOnly = true)
    public List<StageInvitationDto> getInvitationsWithFilters(StageInvitationFilterDto stageInvitationFilterDto) {

        Stream<StageInvitation> invitations = stageInvitationRepository.findAll().stream();

        return stageInvitationFilterService.applyAll(invitations, stageInvitationFilterDto)
                .map(stageInvitationMapper::toDto)
                .toList();
    }
}
