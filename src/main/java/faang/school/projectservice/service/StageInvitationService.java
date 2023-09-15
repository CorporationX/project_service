package faang.school.projectservice.service;

import faang.school.projectservice.dto.invitation.DtoStage;
import faang.school.projectservice.dto.invitation.StageInvitationDto;
import faang.school.projectservice.dto.invitation.DtoStageInvitationFilter;
import faang.school.projectservice.exception.ValidException;
import faang.school.projectservice.filter.stageInvitation.StageInvitationFilter;
import faang.school.projectservice.mapper.invitationMaper.StageInvitationMapper;
import faang.school.projectservice.mapper.invitationMaper.StageMapper;
import faang.school.projectservice.mapper.invitationMaper.TeamMemberMapper;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.publisher.InviteSentEvent;
import faang.school.projectservice.repository.StageInvitationRepository;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class StageInvitationService {

    private final StageInvitationMapper stageInvitationMapper = StageInvitationMapper.INSTANCE;
    private final TeamMemberMapper memberMapper = TeamMemberMapper.INSTANCE;
    private final StageMapper stageMapper = StageMapper.INSTANCE;

    private final StageInvitationRepository invitationRepository;

    private final StageRepository stageRepository;

    private final TeamMemberRepository memberRepository;

    private final List<StageInvitationFilter> invitationFilter;
    private final InviteSentEvent inviteSentEvent;

    @Transactional
    public StageInvitationDto invitationHasBeenSent(StageInvitationDto dto) {
        dataVerificationStageInvitation(dto);

        StageInvitation stageInvitation = stageInvitationMapper.toStageInvitation(dto);
        StageInvitation savedInvitation = invitationRepository.save(stageInvitation);

        inviteSentEvent.publish(stageInvitationMapper.toEventDto(savedInvitation));
        return stageInvitationMapper.toDto(savedInvitation);
    }

    @Transactional
    public StageInvitationStatus acceptDeclineInvitation(String status, long idInvitation) {
        StageInvitation invitation = invitationRepository.findById(idInvitation);

        switch (status) {
            case "ACCEPTED" -> invitation.setStatus(StageInvitationStatus.ACCEPTED);
            case "REJECTED" -> invitation.setStatus(StageInvitationStatus.REJECTED);
            case "PENDING" -> invitation.setStatus(StageInvitationStatus.PENDING);
        }

        return invitation.getStatus();
    }

    public List<StageInvitationDto> getAllStageInvitation(long userId, DtoStageInvitationFilter filters) {
        List<StageInvitation> stageInvitations = invitationRepository.findAll().stream()
                .filter(invitation -> invitation.getInvited().getId() == userId).toList();

        return invitationFilter.stream().filter(filter -> filter.isApplication(filters))
                .flatMap(filter -> filter.apply(stageInvitations.stream(), filters))
                .map(stageInvitationMapper::toDto).toList();
    }

    private void dataVerificationStageInvitation(StageInvitationDto dto) {
        long authorTeamMemberId = dto.getIdAuthor();
        long invitedTeamMemberId = dto.getIdInvited();
        DtoStage dtoStage = dto.getStage();
        TeamMember teamMemberAuthor = memberRepository.findById(authorTeamMemberId);
        memberRepository.findById(invitedTeamMemberId);
        stageRepository.getById(dto.getStage().getStageId());

        if (!teamMemberAuthor.getStages().stream().map(stageMapper::toDtoStage).toList().contains(dtoStage)) {
            throw new ValidException("the author is not a performer");
        }

        if (invitationRepository.existsByAuthorAndInvitedAndStage(memberMapper.toTeamMember(dto.getIdAuthor()),
                memberMapper.toTeamMember(dto.getIdInvited()),
                stageMapper.toStage(dto.getStage()))) {
            throw new ValidException("the invitation has already been sent!");
        }

        if (Objects.equals(dto.getIdAuthor(), dto.getIdInvited())) {
            throw new ValidException("repeated id");
        }
    }
}
