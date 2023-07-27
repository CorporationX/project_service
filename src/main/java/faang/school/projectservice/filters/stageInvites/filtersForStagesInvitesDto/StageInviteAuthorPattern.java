package faang.school.projectservice.filters.stageInvites.filtersForStagesInvitesDto;

import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.filters.stageInvites.FilterStageInviteDto;
import faang.school.projectservice.filters.stageInvites.StageInviteFilter;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class StageInviteAuthorPattern implements StageInviteFilter {
    @Override
    public boolean isApplicable(FilterStageInviteDto filterStageInviteDto) {
        return filterStageInviteDto.getAuthorPattern() != null;
    }

    @Override
    public Stream<StageInvitation> apply(Stream<StageInvitation> stageInvitationStream, FilterStageInviteDto filterStageInviteDto) {
        return stageInvitationStream.filter(stageInvitation ->
                stageInvitation.getAuthor().getUserId().equals(filterStageInviteDto.getAuthorPattern()));
    }
}
