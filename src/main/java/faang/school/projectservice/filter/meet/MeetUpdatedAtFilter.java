package faang.school.projectservice.filter.meet;

import faang.school.projectservice.dto.meet.MeetFilterDto;
import faang.school.projectservice.model.meet.Meet;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class MeetUpdatedAtFilter implements MeetFilter {

    @Override
    public boolean isApplicable(MeetFilterDto filter) {
        return filter.updatedAt() != null;
    }

    @Override
    public Stream<Meet> apply(Stream<Meet> meets, MeetFilterDto filter) {
        return meets.filter(meet -> meet.getUpdatedAt().isBefore(filter.updatedAt()));
    }
}
