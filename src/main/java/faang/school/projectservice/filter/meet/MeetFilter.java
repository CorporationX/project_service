package faang.school.projectservice.filter.meet;

import faang.school.projectservice.dto.meet.MeetFilterDto;
import faang.school.projectservice.model.Meet;

import java.util.stream.Stream;

public interface MeetFilter {
    boolean isApplicable(MeetFilterDto filtersDto);

    Stream<Meet> apply(Stream<Meet> meetStream, MeetFilterDto filtersDto);
}