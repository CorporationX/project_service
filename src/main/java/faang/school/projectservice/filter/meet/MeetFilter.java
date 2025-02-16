package faang.school.projectservice.filter.meet;

import faang.school.projectservice.dto.meet.MeetDto;
import faang.school.projectservice.dto.meet.MeetFilterDto;
import faang.school.projectservice.model.Meet;


import java.util.List;


public interface MeetFilter {
    boolean isApplicable(MeetFilterDto filter);

    List<MeetDto> applyFilter(List<MeetDto> meets, MeetFilterDto filter);
}
