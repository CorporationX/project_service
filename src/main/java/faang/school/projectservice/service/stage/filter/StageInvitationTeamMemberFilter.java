package faang.school.projectservice.service.stage.filter;

import faang.school.projectservice.dto.filter.StageInvitationFilterDto;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StageInvitationTeamMemberFilter implements StageInvitationFilter {

    @Override
    public boolean isApplicable(StageInvitationFilterDto filterDto) {
        return filterDto.getTeamMember() != null;
    }

    @Override
    public boolean apply(StageInvitation stageInvitation, StageInvitationFilterDto filterDto) {
        return stageInvitation.getInvited().getId().equals(filterDto.getTeamMember());
    }
}