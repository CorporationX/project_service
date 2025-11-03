package faang.school.projectservice.filter.meet;

import faang.school.projectservice.dto.meet.MeetFilterDto;
import faang.school.projectservice.model.Meet;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class MeetAfterDateFilter implements MeetFilter {

    @Override
    public boolean isApplicable(MeetFilterDto meetFilterDto) {
        return meetFilterDto.startsAt() != null;
    }

    @Override
    public Stream<Meet> apply(Stream<Meet> meetStream, MeetFilterDto filtersDto) {
        return meetStream.filter(meet -> meet.getStartsAt().isAfter(filtersDto.startsAt()));
    }
}