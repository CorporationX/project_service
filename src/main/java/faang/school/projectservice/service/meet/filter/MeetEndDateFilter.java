package faang.school.projectservice.service.meet.filter;

import faang.school.projectservice.model.Meet;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class MeetEndDateFilter implements MeetFilter {
    @Override
    public boolean isApplicable(MeetFilters meetFilters) {
        return meetFilters.getEnd() != null;
    }

    @Override
    public Stream<Meet> apply(Stream<Meet> meets, MeetFilters meetFilters) {
        return meets.filter(meet -> meet.getCreatedAt().isBefore(meetFilters.getEnd()));
    }
}
