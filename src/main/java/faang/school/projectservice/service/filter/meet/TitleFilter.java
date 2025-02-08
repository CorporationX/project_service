package faang.school.projectservice.service.filter.meet;

import faang.school.projectservice.dto.meet.MeetFilterRequest;
import faang.school.projectservice.model.Meet;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class TitleFilter implements MeetFilter {
    @Override
    public Stream<Meet> filter(Stream<Meet> stream, MeetFilterRequest request) {
        return request.title() == null
                ? stream
                : stream.filter(meet -> meet.getTitle().equals(request.title()));
    }
}
