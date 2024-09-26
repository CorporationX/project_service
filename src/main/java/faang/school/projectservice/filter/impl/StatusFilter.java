package faang.school.projectservice.filter.impl;

import faang.school.projectservice.dto.stage_invitation.StageInvitationFilterDto;
import faang.school.projectservice.filter.StageInvitationFilter;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class StatusFilter implements StageInvitationFilter {
    @Override
    public List<StageInvitation> apply(List<StageInvitation> invitations, StageInvitationFilterDto filterDto) {
        if (filterDto.status() == null) {
            return invitations;
        }
        return invitations.stream()
                .filter(invitation -> invitation.getStatus() == filterDto.status())
                .collect(Collectors.toList());
    }
}