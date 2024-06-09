package faang.school.projectservice.filter;

import faang.school.projectservice.dto.filter.MeetFilterDto;
import faang.school.projectservice.model.Meet;

import java.util.function.Supplier;
import java.util.stream.Stream;

public interface MeetFilter {

    boolean isApplicable(MeetFilterDto filters);

    Stream<Meet> apply(Supplier<Stream<Meet>> requests, MeetFilterDto filters);
}
