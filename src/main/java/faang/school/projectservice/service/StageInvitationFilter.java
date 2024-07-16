package faang.school.projectservice.service;

import faang.school.projectservice.dto.FilterDto;
import faang.school.projectservice.model.stage_invitation.StageInvitation;

import java.util.stream.Stream;
public interface StageInvitationFilter<T, F> {
    boolean isApplicable(FilterDto filterDto);

    Stream<T> apply(Stream<StageInvitation> stream, FilterDto filterDto);
}
