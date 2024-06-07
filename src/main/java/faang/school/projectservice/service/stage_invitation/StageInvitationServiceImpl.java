package faang.school.projectservice.service.stage_invitation;

import faang.school.projectservice.dto.stage_invitation.StageInvitationCreateDto;
import faang.school.projectservice.dto.stage_invitation.StageInvitationDto;
import faang.school.projectservice.dto.stage_invitation.StageInvitationFilterDto;
import faang.school.projectservice.exceptions.NotFoundException;
import faang.school.projectservice.mapper.StageInvitationMapper;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.repository.StageInvitationRepository;
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
    private final StageInvitationValidator stageInvitationValidator;
    private final StageInvitationFilterService stageInvitationFilterService;

    @Override
    @Transactional
    public StageInvitationDto sendInvitation(StageInvitationCreateDto stageInvitationCreateDto) {

        StageInvitation stageInvitation = stageInvitationMapper.toEntity(stageInvitationCreateDto);

        stageInvitationValidator.validateExistences(stageInvitationCreateDto);

        return stageInvitationMapper.toDto(stageInvitationRepository.save(stageInvitation));
    }

    @Override
    @Transactional
    public StageInvitationDto acceptInvitation(long userId, long inviteId) {

        TeamMember invited = teamMemberRepository.findById(userId);
        StageInvitation stageInvitation = findById(inviteId);

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
        StageInvitation stageInvitation = findById(inviteId);

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

    private StageInvitation findById(Long stageInvitationId) {
        return stageInvitationRepository.findById(stageInvitationId)
                .orElseThrow(() -> new NotFoundException(String.format("Stage invitation doesn't exist by id: %s", stageInvitationId))
        );
    }
}
