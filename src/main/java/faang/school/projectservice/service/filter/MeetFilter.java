package faang.school.projectservice.service.filter;

import faang.school.projectservice.dto.MeetFilters;
import faang.school.projectservice.model.Meet;

import java.util.stream.Stream;

public interface MeetFilter {
    boolean isApplicable(MeetFilters meetFilters);
    Stream<Meet> apply(Stream<Meet> meets, MeetFilters meetFilters);
}
