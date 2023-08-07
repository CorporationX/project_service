package faang.school.projectservice.filter;

import faang.school.projectservice.dto.invitation.StageInvitationFilterDto;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import org.springframework.stereotype.Component;
import java.util.Objects;
import java.util.stream.Stream;

@Component
public class StageInvitationStageFilter implements StageInvitationFilter{
        @Override
        public boolean isApplicable(StageInvitationFilterDto stageInvitationFilterDto) {
            return stageInvitationFilterDto.getStagePattern() != null;
        }

        @Override
        public Stream<StageInvitation> apply(Stream<StageInvitation> invitationsStream, StageInvitationFilterDto stageInvitationFilterDto) {
            String stagePattern = stageInvitationFilterDto.getStagePattern();
            Objects.requireNonNull(stagePattern, "Stage can`t be empty");

            return invitationsStream
                    .filter(stageInvitation -> {
                        String invitationStageName = stageInvitation.getStage().getStageName();
                        return invitationStageName != null && invitationStageName.contains(stagePattern);
                    });
        }
}