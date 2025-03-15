package faang.school.projectservice.filter.meet;

import faang.school.projectservice.dto.meet.MeetDto;
import faang.school.projectservice.dto.meet.MeetFilterDto;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MeetTitleFilter implements MeetFilter {
    public boolean isApplicable(MeetFilterDto filter) {
        return filter.getTitle() != null;
    }

    public List<MeetDto> applyFilter(List<MeetDto> meets, MeetFilterDto filter) {
        return meets.stream().
                filter(meet -> meet.getTitle().equals(filter.getTitle())).
                toList();
    }
}
