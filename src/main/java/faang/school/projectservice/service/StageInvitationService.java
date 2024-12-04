package faang.school.projectservice.service;

import faang.school.projectservice.dto.stage.invitation.StageInvitationDto;
import faang.school.projectservice.dto.stage.invitation.StageInvitationFilterDto;
import faang.school.projectservice.filter.StageInvitationFilter;
import faang.school.projectservice.mapper.StageInvitationMapper;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.repository.StageInvitationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class StageInvitationService {
    private final StageInvitationRepository stageInvitationRepository;
    private final StageService stageService;
    private final TeamMemberService teamMemberService;
    private final StageInvitationMapper stageInvitationMapper;
    private final List<StageInvitationFilter> stageInvitationFilters;

    @Transactional
    public StageInvitationDto sendInvitation(StageInvitationDto stageInvitationDto) {
        log.info("Trying to send stage invitation: {}", stageInvitationDto);
        StageInvitation stageInvitation = mapToFullStageInvitation(stageInvitationDto);
        stageInvitation.setStatus(StageInvitationStatus.PENDING);
        stageInvitationRepository.save(stageInvitation);
        log.info("Successfully sent stage invitation: {}", stageInvitation);
        return stageInvitationMapper.toDto(stageInvitation);
    }

    @Transactional
    public StageInvitationDto acceptInvitation(long stageInvitationId) {
        log.info("Trying to accept invitation under id: {}", stageInvitationId);
        StageInvitation stageInvitation = getStageInvitation(stageInvitationId);
        stageInvitation.setStatus(StageInvitationStatus.ACCEPTED);
        log.info("Successfully accepted invitation: {}", stageInvitation);
        return stageInvitationMapper.toDto(stageInvitation);
    }

    @Transactional
    public StageInvitationDto rejectInvitation(long stageInvitationId, String reason) {
        log.info("Trying to reject invitation under id: {}", stageInvitationId);
        StageInvitation stageInvitation = getStageInvitation(stageInvitationId);
        stageInvitation.setStatus(StageInvitationStatus.REJECTED);
        stageInvitation.setDescription(reason);
        log.info("Successfully rejected invitation: {}", stageInvitation);
        return stageInvitationMapper.toDto(stageInvitation);
    }

    @Transactional
    public List<StageInvitationDto> getAllFilteredInvitations(long invitedId,
                                                              StageInvitationFilterDto filter) {
        log.info("Trying to get invitations for user: {}, with the following filters: {}",
                invitedId, filter);
        Stream<StageInvitation> invitedStageInvitations = stageInvitationRepository.findAll().stream()
                .filter(stageInvitation -> stageInvitation.isInvited(invitedId));
        List<StageInvitationFilter> applicableFilters = stageInvitationFilters
                .stream()
                .filter(stageInvitationFilter -> stageInvitationFilter.isApplicable(filter))
                .toList();

        for (StageInvitationFilter stageInvitationFilter : applicableFilters) {
            invitedStageInvitations = stageInvitationFilter.apply(invitedStageInvitations, filter);
        }

        log.info("Successfully got invitations for user: {}, with the following filters: {} ",
                invitedId, filter);

        return stageInvitationMapper.toDto(invitedStageInvitations.toList());
    }

    private StageInvitation mapToFullStageInvitation(StageInvitationDto stageInvitationDto) {
        StageInvitation stageInvitation = stageInvitationMapper.toEntity(stageInvitationDto);

        long stageId = stageInvitationDto.stageId();
        long authorId = stageInvitationDto.authorId();
        long invitedId = stageInvitationDto.invitedId();

        stageInvitation.setStage(stageService.getStage(stageId));
        stageInvitation.setAuthor(teamMemberService.getTeamMember(authorId));
        stageInvitation.setInvited(teamMemberService.getTeamMember(invitedId));

        return stageInvitation;
    }

    private StageInvitation getStageInvitation(long stageInvitationId) {
        return stageInvitationRepository.findById(stageInvitationId);
    }
}
