package faang.school.projectservice.service.meet.filter;

import faang.school.projectservice.model.Meet;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class MeetDateFilter implements MeetFilter {
    @Override
    public boolean isApplicable(MeetFilters meetFilters) {
        return meetFilters.getBegin() != null || meetFilters.getEnd() != null;
    }

    @Override
    public Stream<Meet> apply(Stream<Meet> meets, MeetFilters meetFilters) {
        if (meetFilters.getBegin() != null && meetFilters.getEnd() != null) {
            return meets.filter(
                    meet -> (
                            meet.getCreatedAt().isAfter(meetFilters.getBegin()) &&
                                    meet.getCreatedAt().isBefore(meetFilters.getEnd())
                    )
            );
        } else if (meetFilters.getBegin() != null) {
            return meets.filter(meet -> meet.getCreatedAt().isAfter(meetFilters.getBegin()));
        } else {
            return meets.filter(meet -> meet.getCreatedAt().isBefore(meetFilters.getEnd()));
        }
    }
}
