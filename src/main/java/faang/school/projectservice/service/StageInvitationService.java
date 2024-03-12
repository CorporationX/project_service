package faang.school.projectservice.service;

import faang.school.projectservice.dto.StageInvitationDto;
import faang.school.projectservice.filters.stageinvitation.StageInvitationFilter;
import faang.school.projectservice.mapper.StageInvitationMapper;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.repository.StageInvitationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class StageInvitationService {

    private final StageInvitationMapper stageInvitationMapper;
    private final StageInvitationRepository stageInvitationRepository;
    private final TeamMemberService teamMemberService;
    private final List<StageInvitationFilter> stageInvitationFilterList;

    public StageInvitationDto create(StageInvitationDto stageInvitationDto) {
        return saveStageInvitation(stageInvitationMapper.toEntity(stageInvitationDto));
    }

    public StageInvitationDto accept(Long userId, Long invitationId) {
        StageInvitation invitation = getInvitation(userId, invitationId);

        invitation.setStatus(StageInvitationStatus.ACCEPTED);
        invitation.setInvited(teamMemberService.getTeamMember(userId));
        return saveStageInvitation(invitation);
    }

    public StageInvitationDto reject(Long userId, Long invitationId, String description) {
        StageInvitation invitation = getInvitation(userId, invitationId);

        invitation.setDescription(description);
        invitation.setStatus(StageInvitationStatus.REJECTED);
        return saveStageInvitation(invitation);
    }

    public List<StageInvitationDto> getAll(Long id, StageInvitationDto filter) {
        if (filter != null) {
            Stream<StageInvitation> recommendationRequests = stageInvitationRepository.findAll().stream();
            for (StageInvitationFilter stageInvitationFilter : stageInvitationFilterList) {
                if (stageInvitationFilter.isApplicable(filter))
                    recommendationRequests = stageInvitationFilter.apply(recommendationRequests, filter);
            }
            List<StageInvitationDto> recommendationRequestDtos = new ArrayList<>();
            for (StageInvitation requests : recommendationRequests.toList()) {
                recommendationRequestDtos.add(stageInvitationMapper.toDto(requests));
            }
            return recommendationRequestDtos;
        } else
            return stageInvitationMapper.toDtoList(stageInvitationRepository
                    .findAll()
                    .stream()
                    .filter(stageInvitation -> stageInvitation.getInvited().getUserId().equals(id))
                    .toList());
    }

    private StageInvitation getInvitation(Long userId, Long invitationId) {
        StageInvitation invitation = stageInvitationRepository.findById(invitationId);

        if (invitation == null) {
            throw new NullPointerException("Invitation with provided id: " + invitationId + " are not exist");
        }

        if (invitation.getInvited().getId().equals(userId)) {
            throw new IllegalArgumentException("User " + userId + " not found in invitation");
        }

        return invitation;
    }

    private StageInvitationDto saveStageInvitation(StageInvitation invitation) {
        stageInvitationRepository.save(invitation);
        return stageInvitationMapper.toDto(invitation);
    }

}
