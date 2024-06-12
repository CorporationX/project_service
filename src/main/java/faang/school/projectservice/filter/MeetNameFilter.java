package faang.school.projectservice.filter;

import faang.school.projectservice.dto.filter.MeetFilterDto;
import faang.school.projectservice.model.Meet;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;
import java.util.stream.Stream;

@Component
public class MeetNameFilter implements MeetFilter {
    @Override
    public boolean isApplicable(MeetFilterDto filters) {
        return filters.getName() != null && !filters.getName().isBlank();
    }

    @Override
    public Stream<Meet> apply(Supplier<Stream<Meet>> meetings, MeetFilterDto filters) {
        return meetings.get().filter(meet -> meet.getName().equals(filters.getName()));
    }
}
