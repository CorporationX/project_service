package faang.school.projectservice.service.meet.filter;

import faang.school.projectservice.dto.meet.MeetDto;
import faang.school.projectservice.dto.meet.MeetFilterDto;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class TitleFilter implements MeetFilter {

    @Override
    public boolean isApplicable(MeetFilterDto meetFilter) {
        return meetFilter.getTitlePattern() != null && !meetFilter.getTitlePattern().isEmpty();
    }

    @Override
    public Stream<MeetDto> apply(Stream<MeetDto> meets, MeetFilterDto filter) {
        return meets.filter(meet -> meet.getTitle().toLowerCase().contains(filter.getTitlePattern().toLowerCase()));
    }
}
