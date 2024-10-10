package faang.school.projectservice.model.filter.meet;

import faang.school.projectservice.model.dto.meet.MeetFilterDto;
import faang.school.projectservice.model.entity.meet.Meet;

import java.util.stream.Stream;

public interface MeetFilter {

    boolean isApplicable(MeetFilterDto filter);

    Stream<Meet> apply(Stream<Meet> meets, MeetFilterDto filter);
}
