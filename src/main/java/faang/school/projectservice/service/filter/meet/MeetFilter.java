package faang.school.projectservice.service.filter.meet;

import faang.school.projectservice.dto.meet.MeetFilterRequest;
import faang.school.projectservice.model.Meet;

import java.util.stream.Stream;

public interface MeetFilter {
    Stream<Meet> filter(Stream<Meet> stream, MeetFilterRequest request);
}
