package faang.school.projectservice.filter.meet;

import faang.school.projectservice.dto.meet.MeetFilterDto;
import faang.school.projectservice.model.meet.Meet;

import java.util.stream.Stream;

public interface MeetFilter {
    boolean isApplicable(MeetFilterDto meetFilterDto);

    Stream<Meet> filter(Stream<Meet> meetStream, MeetFilterDto meetFilterDto);
}
