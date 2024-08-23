package faang.school.projectservice.filter.meet;

import faang.school.projectservice.dto.meet.MeetFilterDto;
import faang.school.projectservice.model.meet.Meet;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class MeetTitleFilter implements MeetFilter {

    @Override
    public boolean isApplicable(MeetFilterDto meetFilterDto) {
        return meetFilterDto.getTitlePattern() != null;
    }

    @Override
    public Stream<Meet> filter(Stream<Meet> meetStream, MeetFilterDto meetFilterDto) {
        if (isApplicable(meetFilterDto)) {
            return meetStream.filter(meet -> meet.getTitle()
                    .toLowerCase()
                    .contains(meetFilterDto.getTitlePattern().toLowerCase()));
        }
        return meetStream;
    }
}
