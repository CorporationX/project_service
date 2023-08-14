package faang.school.projectservice.service;

import faang.school.projectservice.dto.invitation.DtoStageInvitation;
import faang.school.projectservice.dto.invitation.DtoStageInvitationFilter;
import faang.school.projectservice.dto.invitation.DtoStatus;
import faang.school.projectservice.exception.ValidException;
import faang.school.projectservice.filter.stageInvitation.StageInvitationFilter;
import faang.school.projectservice.mapper.invitationMaper.StageInvitationMapper;
import faang.school.projectservice.mapper.invitationMaper.StageMapper;
import faang.school.projectservice.mapper.invitationMaper.TeamMemberMapper;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.repository.StageInvitationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StageInvitationService {

    private final StageInvitationMapper stageInvitationMapper = StageInvitationMapper.INSTANCE;
    private final TeamMemberMapper memberMapper = TeamMemberMapper.INSTANCE;
    private final StageMapper stageMapper = StageMapper.INSTANCE;

    private final StageInvitationRepository invitationRepository;

    private final List<StageInvitationFilter> invitationFilter;

    public DtoStageInvitation invitationHasBeenSent(DtoStageInvitation dto) {
        if (!invitationRepository.existsByAuthorAndInvitedAndStage(memberMapper.toTeamMember(dto.getIdAuthor()), memberMapper.toTeamMember(dto.getIdInvited()),
                stageMapper.toStage(dto.getStage()))) {
            throw new ValidException("check the data");
        }
        if (dto.getIdAuthor() == dto.getIdInvited()) {
            throw new ValidException("repeated id");
        }

        return stageInvitationMapper.toDto(invitationRepository.save(stageInvitationMapper.toStageInvitation(dto)));
    }

    @Transactional
    public DtoStatus acceptDeclineInvitation(String status, long idInvitation) {
        StageInvitation invitation = invitationRepository.findById(idInvitation);

        if (status.equals("ACCEPTED")) {
            invitation.setStatus(StageInvitationStatus.ACCEPTED);
        } else if (status.equals("REJECTED")) {
            invitation.setStatus(StageInvitationStatus.REJECTED);
        } else if (status.equals("PENDING")) {
            invitation.setStatus(StageInvitationStatus.PENDING);
        }

        return new DtoStatus(invitation.getStatus());
    }

    public List<DtoStageInvitation> getAllStageInvitation(long userId, DtoStageInvitationFilter filters) {
        List<StageInvitation> stageInvitations = invitationRepository.findAll().stream()
                .filter(invitation -> invitation.getInvited().getId() == userId).toList();

        return invitationFilter.stream().filter(filter -> filter.isApplication(filters))
                .flatMap(filter -> filter.apply(stageInvitations.stream(), filters))
                .map(stageInvitationMapper::toDto).toList();
    }
}
