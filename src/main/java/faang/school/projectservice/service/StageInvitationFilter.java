package faang.school.projectservice.service;

import faang.school.projectservice.dto.FilterInvitationDto;
import faang.school.projectservice.model.stage_invitation.StageInvitation;

import java.util.stream.Stream;
public interface StageInvitationFilter<T, F> {
    boolean isApplicable(FilterInvitationDto filterInvitationDto);

    Stream<T> apply(Stream<StageInvitation> stream, FilterInvitationDto filterInvitationDto);
}
