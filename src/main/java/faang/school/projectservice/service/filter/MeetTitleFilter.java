package faang.school.projectservice.service.filter;

import faang.school.projectservice.dto.MeetFilters;
import faang.school.projectservice.model.Meet;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class MeetTitleFilter implements MeetFilter {
    @Override
    public boolean isApplicable(MeetFilters meetFilters) {
        return meetFilters.getTitle() != null && !meetFilters.getTitle().isBlank();
    }

    @Override
    public Stream<Meet> apply(Stream<Meet> meets, MeetFilters meetFilters) {
        return meets.filter(
                meet -> meet.getTitle().toLowerCase().contains(meetFilters.getTitle().toLowerCase())
        );
    }
}
