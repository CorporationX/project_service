package faang.school.projectservice.filter;

import faang.school.projectservice.dto.stageInvitation.StageInvitationFilterDto;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class StageInvitationTeamMemberFilter implements StageInvitationFilter {

    @Override
    public boolean isApplicable(StageInvitationFilterDto stageInvitationFilterDto) {
        return stageInvitationFilterDto.getTeamMember() != null;
    }

    @Override
    public Stream<StageInvitation> apply(Stream<StageInvitation> stageInvitations,
                                         StageInvitationFilterDto stageInvitationFilterDto) {
        return stageInvitations.filter(stageInvitation ->
                stageInvitation.getInvited().getId().equals(stageInvitationFilterDto.getTeamMember()));
    }
}